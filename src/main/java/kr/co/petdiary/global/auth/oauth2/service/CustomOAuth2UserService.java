package kr.co.petdiary.global.auth.oauth2.service;

import kr.co.petdiary.global.auth.oauth2.userdetail.CustomOAuth2User;
import kr.co.petdiary.global.auth.oauth2.userdetail.OAuthAttributes;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.model.LoginType;
import kr.co.petdiary.owner.repository.OwnerRepository;
import kr.co.petdiary.owner.repository.OwnerSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final OwnerRepository ownerRepository;
    private final OwnerSearchRepository ownerSearchRepository;

    /**
     * (1) DefaultOAuth2UserService.loadUser() <br />
     * - Social Login API의 사용자 정보 제공 URI로 사용자 정보 요청 <br />
     * - 얻은 사용자 정보를 통해 DefaultOAuth2User 생성 <br />
     * - 반환된 OAuth2User - OAuth2 Service에서 가져온 유저 정보 <br />
     * <br />
     * <br />
     * (2) OAuth2UserRequest.getAttributes() <br />
     * - "Social Login"에서 API가 제공하는 JSON 형태의 유저 정보 <br />
     * <br />
     * <br />
     * (3) OAuth2UserRequest.getClientRegistration().getRegistrationId() <br />
     * - 추후 LoginType에 저장할 값 <br />
     * - Ex - "kakao", "naver" 등 <br />
     * - URI Ex - "http://localhost:8080/oauth2/authorization/kakao" <br />
     * <br />
     * <br />
     * (4) OAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName() <br />
     * - OAuth2 로그인 진행 시, 키가 되는 필드 값 <br />
     * - Ex Google - "sub" <br />
     * - Ex Naver & Kakao - 기본 지원 X <br />
     * - 추후 "nameAttributeKey"로 설정 <br />
     * <br />
     * <br />
     * (5) OAuthAttributes.ofOAuthAttributes(loginType, userNameAttributeName, attributes); <br />
     * - LoginType 별로 OAuthAttributes DTO 생성 <br />
     * <br />
     * <br />
     * (8) CustomOAuth2User.builder()...build()
     * - "DefaultOAuth2User"를 구현한 "CustomOAuth2User" 객체 반환
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 Login Request Start");

        //(1)
        final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        final OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //(2)
        final Map<String, Object> attributes = oAuth2User.getAttributes();

        //(3)
        final String registrationId = userRequest.getClientRegistration().getRegistrationId();
        final LoginType loginType = LoginType.getLoginType(registrationId);

        //(4)
        final String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        //(5)
        final OAuthAttributes extractAttributes
                = OAuthAttributes.ofOAuthAttributes(loginType, userNameAttributeName, attributes);

        //(6)
        final Owner owner = getOwnerBySocialIdAndLoginType(extractAttributes, loginType);

        //(8)
        return CustomOAuth2User.builder()
                .authorities(Collections.singleton(new SimpleGrantedAuthority(owner.getRoleKey())))
                .attributes(attributes)
                .nameAttributeKey(extractAttributes.getNameAttributeKey())
                .email(owner.getEmail())
                .role(owner.getRole())
                .build();
    }

    /**
     * (6) OAuthAttributes To Owner (DTO -> Entity) <br/>
     * SocialType과 attributes에 들어있는 소셜 로그인의 식별값 id를 통해 회원을 찾아 반환하는 메소드
     * 만약 찾은 회원이 있다면, 그대로 반환하고 없다면 createOwner()를 호출하여 회원을 저장한다.
     */
    private Owner getOwnerBySocialIdAndLoginType(OAuthAttributes attributes, LoginType loginType) {
        final String socialId = attributes.getOauth2UserInfo().getSocialId();

        //(7)
        return ownerSearchRepository.searchBySocialIdAndLoginType(socialId, loginType)
                .orElse(createOwner(attributes, loginType));
    }

    /**
     * (7) 찾는 회원이 없다면 회원 저장 후 반환
     * OAuthAttributes의 toOwner() 메소드를 통해 빌더로 Owner 객체 생성 후 반환
     */
    private Owner createOwner(OAuthAttributes attributes, LoginType loginType) {
        final Owner owner = attributes.toOwner(loginType, attributes.getOauth2UserInfo());
        return ownerRepository.save(owner);
    }
}
