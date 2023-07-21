package kr.co.petdiary.global.auth.oauth2.userdetail;

import java.util.Map;

public class NaverUserInfo extends OAuth2UserInfo {
    //네이버의 경우에는, attributes를 받았을 때 바로 유저 정보가 있는 것이 아니고 'response' Key로 한 번 감싸져 있음
    private static final String NAVER_ATTRIBUTE_KEY = "response";
    private static final String SOCIAL_ID_KEY = "id";

    public NaverUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getSocialId() {
        final Map<String, Object> response = (Map<String, Object>) attributes.get(NAVER_ATTRIBUTE_KEY);

        if (response == null) {
            return null;
        }

        return (String) response.get(SOCIAL_ID_KEY);
    }

    @Override
    public String getName() {
        final Map<String, Object> response = (Map<String, Object>) attributes.get(NAVER_ATTRIBUTE_KEY);

        if (response == null) {
            return null;
        }

        return (String) response.get(NAME_KEY);
    }
}
