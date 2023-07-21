package kr.co.petdiary.global.auth.oauth2.userdetail;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public abstract class OAuth2UserInfo {
    protected static final String NAME_KEY = "name";

    protected Map<String, Object> attributes;

    //소셜 식별 값 : 구글 - "sub", 카카오 - "id", 네이버 - "id"
    public abstract String getSocialId();

    public abstract String getName();
}
