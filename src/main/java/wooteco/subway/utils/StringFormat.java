package wooteco.subway.utils;

public class StringFormat {

    public static final String ERROR_MESSAGE_FORM = "%s : %s";

    private StringFormat() {
    }

    public static String errorMessage(String target, String message) {
        return String.format(ERROR_MESSAGE_FORM, target, message);
    }

    public static String errorMessage(Long target, String message) {
        return errorMessage(target.toString(), message);
    }
}
