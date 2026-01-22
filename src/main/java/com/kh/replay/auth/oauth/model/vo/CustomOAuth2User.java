package com.kh.replay.auth.oauth.model.vo;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.kh.replay.auth.oauth.model.dto.OAuthUserDTO;

import lombok.Builder;
import lombok.Value;

	@Value
	@Builder
	public class CustomOAuth2User implements OAuth2User {
		private final OAuthUserDTO oauthUser;

		  public CustomOAuth2User(OAuthUserDTO oauthUser) {
		        this.oauthUser = oauthUser;
		    }

		    public String getProvider() {
		        return oauthUser.getProvider();
		    }

		    public String getProviderId() {
		        return oauthUser.getProviderId();
		    }

		    public String getGlobalId() {
		        return oauthUser.getGlobalId();
		    }

		    public String getEmail() {
		        return oauthUser.getEmail();
		    }

		    public String getRole() {
		        return oauthUser.getRole().name();
		    }

		    public boolean isNewUser() {
		        return oauthUser.isNewUser();
		    }

		    public boolean isPhoneUnverified() {
		        return oauthUser.isPhoneUnverified();
		    }

		    // 닉네임은 추가정보 입력 후에만 값이 있을 수 있음
		    public String getNickname() {
		        return oauthUser.getNickname();
		    }

		    public Long getUserId() {
		        return oauthUser.getUserId();
		    }

		    @Override
		    public Map<String, Object> getAttributes() {
		        // 필요하면 OAuth provider attribute를 담을 수 있도록 확장 가능
		        return null;
		    }

		    @Override
		    public String getName() {
		        return oauthUser.getNickname() != null ? dto.getNickname() : dto.getEmail();
		    }
		    
		    @Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return null;
			}

	}


	