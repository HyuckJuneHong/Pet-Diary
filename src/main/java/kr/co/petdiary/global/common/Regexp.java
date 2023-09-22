package kr.co.petdiary.global.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Regexp {
    public static final String PHONE_PATTERN = "^\\d{3}-\\d{4}-\\d{4}$";
}
