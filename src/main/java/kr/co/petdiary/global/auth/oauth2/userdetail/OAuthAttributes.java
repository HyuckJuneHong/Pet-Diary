package kr.co.petdiary.global.auth.oauth2.userdetail;

import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.model.LoginType;
import kr.co.petdiary.owner.model.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {

    //OAuth2 로그인 진행 시, 키가 되는 필드 값
    private String nameAttributeKey;
    //소셜 타입별 로그인 유저 정보
    private OAuth2UserInfo oauth2UserInfo;

    @Builder
    private OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    /**
     * <div>로그인 타입에 맞는 메소드 호출하여 OAuthAttributes 객체 반환</div>
     * <div>CustomOAuth2UserService에서 파라미터들을 주입해서 분기별로 OAuthAttributes 객체를 생성하는 메소드</div>
     *
     * @param loginType        로그인 타입
     * @param nameAttributeKey OAuth2 로그인 시, 키가 되는 값 (PK)
     * @param attributes       OAuth 서비스의 유저 정보
     * @return 회원 PK, attributes, nameAttributesKey
     */
    public static OAuthAttributes ofOAuthAttributes(LoginType loginType,
                                                    String nameAttributeKey, Map<String, Object> attributes) {
        if (loginType == LoginType.NAVER) {
            return ofNaverAttributes(nameAttributeKey, attributes);
        }

        return ofGoogleAttributes(nameAttributeKey, attributes);
    }

    private static OAuthAttributes ofNaverAttributes(String nameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(nameAttributeKey)
                .oauth2UserInfo(new NaverUserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofGoogleAttributes(String nameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(nameAttributeKey)
                .oauth2UserInfo(new GoogleUserInfo(attributes))
                .build();
    }

    /**
     * <div>ofOAuthAttributes(...)로 객체가 생성되어, 유저 정보들이 담긴 OAuth2UserInfo가 소셜 타입별로 주입된 상태</div>
     * <div>OAuth2UserInfo에서 socialId(PK), name을 가져와서 build</div>
     * <div>email과 password에는 UUID로 랜덤 값 생성</div>
     * <div>role는 GUEST로 설정</div>
     */
    public Owner toOwner(LoginType loginType, OAuth2UserInfo oauth2UserInfo) {
        return Owner.builder()
                .email(UUID.randomUUID() + "@social.com")
                .password(UUID.randomUUID().toString())
                .name(oauth2UserInfo.getName())
                .role(Role.GUEST)
                .loginType(loginType)
                .socialId(oauth2UserInfo.getSocialId())
                .build();
    }
}
