package kr.co.petdiary.owner.model;

public enum LoginType {
    NATIVE,
    GOOGLE,
    NAVER;

    public static LoginType getLoginType(String registrationId) {
        if (registrationId.equals("naver")) {
            return NAVER;
        }

        if (registrationId.equals("google")) {
            return GOOGLE;
        }

        return NATIVE;
    }
}
