package kr.co.petdiary.global.auth.oauth2.userdetail;

import kr.co.petdiary.owner.model.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {
    private String email;
    private Role role;

    @Builder
    private CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
                             String nameAttributeKey, String email, Role role) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.role = role;
    }
}
