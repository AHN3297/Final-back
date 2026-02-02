package com.kh.replay.auth.oauth.model.sevice;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.kh.replay.auth.oauth.model.dao.SocialMapper;
import com.kh.replay.auth.oauth.model.dto.OAuthUserDTO;
import com.kh.replay.auth.oauth.model.res.GoogleRes;
import com.kh.replay.auth.oauth.model.res.OAuth2Res;
import com.kh.replay.auth.oauth.model.vo.CustomOAuth2User;
import com.kh.replay.global.exception.OAuth2AuthenticationException;
import com.kh.replay.member.model.dao.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SocialMapper socialMapper;
    private final MemberMapper memberMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // provider 판별
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Res oAuth2Response = switch (registrationId) {
            case "google" -> new GoogleRes(oAuth2User.getAttributes());
            default -> throw new OAuth2AuthenticationException("지원하지 않는 OAuth 제공자입니다.");
        };

        String provider = oAuth2Response.getProvier();
        String providerId = oAuth2Response.getProviderId();
        String email = oAuth2Response.getEmail();
        String name = oAuth2Response.getName();

        OAuthUserDTO probe = new OAuthUserDTO();
        probe.setProvider(provider);
        probe.setProviderId(providerId);

        OAuthUserDTO existingSocial = socialMapper.findByProviderAndProviderId(probe);

        if (existingSocial != null) {
            // 프로필 완료 여부 판단 (닉네임 기준)
            boolean profileCompleted =
                    existingSocial.getName() != null &&
                    !existingSocial.getName().isBlank();

            existingSocial.setNewUser(!profileCompleted);
            existingSocial.setEmail(email);
            existingSocial.setName(name);

            return new CustomOAuth2User(existingSocial);
        }

        boolean emailExists = memberMapper.existByEmail(email);
        if (emailExists) {

            boolean isLocal = memberMapper.existsLocalByEmail(email);
            if (isLocal) {
                throw new OAuth2AuthenticationException("이미 일반 회원으로 가입된 이메일입니다.");
            }

            String memberId = memberMapper.findMemberIdByEmail(email);
            if (memberId == null) {
                throw new OAuth2AuthenticationException("회원 정보가 일치하지 않습니다.");
            }

            OAuthUserDTO linkUser = new OAuthUserDTO();
            linkUser.setMemberId(memberId);
            linkUser.setProvider(provider);
            linkUser.setProviderId(providerId);
            linkUser.setEmail(email);
            linkUser.setName(name);
            linkUser.setNewUser(false);

            socialMapper.insertOAuthUser(linkUser);

            return new CustomOAuth2User(linkUser);
        }

        String newMemberId = "#" + providerId;

        OAuthUserDTO newUser = new OAuthUserDTO();
        newUser.setMemberId(newMemberId);
        newUser.setProvider(provider);
        newUser.setProviderId(providerId);
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setNewUser(true);

        memberMapper.insertOAuthBasicInfo(newUser);
        socialMapper.insertOAuthUser(newUser);

        return new CustomOAuth2User(newUser);
    }
}
