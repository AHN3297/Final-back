package com.kh.replay.auth.oauth.model.vo;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
		  public String getMemberId() {
		        return oauthUser.getMemberId();
		    }
		  public String getEmail() {
		        return oauthUser.getEmail();
		    }
		  
		    public String getProvider() {
		        return oauthUser.getProvider();
		    }

		    public String getProviderId() {
		        return oauthUser.getProviderId();
		    } 
		    public boolean isNewUser() {
		        return oauthUser.isNewUser();
		    }

		
		    @Override
		    public Map<String, Object> getAttributes() {
		        return null;
		    }

		 
		    @Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				 return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
			}

			@Override
			public String getName() {
				return oauthUser.getName();
			}

	}


	