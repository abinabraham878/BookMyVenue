package com.bookmyvenue.backend.common.constants;

public final class RegexConstants {
    private RegexConstants() {
    }
    public static final String PASSWORD =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s]).{8,64}$";

    public static final String COUNTRY_CODE =
            "^\\+[1-9]\\d{0,4}$";

    public static final String PHONE_NUMBER =
            "^\\d{5,20}$";
}
