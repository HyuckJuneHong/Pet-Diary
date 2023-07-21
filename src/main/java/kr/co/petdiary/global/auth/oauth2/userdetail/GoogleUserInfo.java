package kr.co.petdiary.global.auth.oauth2.userdetail;

import java.util.Map;

public class GoogleUserInfo extends OAuth2UserInfo {
    private static final String SOCIAL_ID_KEY = "sub";

    public GoogleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getSocialId() {
        return (String) attributes.get(SOCIAL_ID_KEY);
    }

    @Override
    public String getName() {
        return (String) attributes.get(NAME_KEY);
    }
}
