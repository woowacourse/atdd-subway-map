package wooteco.subway.line;

import java.util.regex.Pattern;

public class LineName {
    private static final int MAX_NAME_LENGTH = 20;
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[·가-힣a-zA-Z0-9\\(\\)\\s]*$");

    private final String name;

    public LineName(final String name) {
        String trimAndRemoveDuplicateBlankName = name.trim().replaceAll(" +", " ");
        validateNameLength(trimAndRemoveDuplicateBlankName);
        validateInvalidName(trimAndRemoveDuplicateBlankName);
        this.name = trimAndRemoveDuplicateBlankName;
    }

    private void validateNameLength(String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(String.format("노선 이름은 %d자를 초과할 수 없습니다. 이름의 길이 : %d", MAX_NAME_LENGTH, name.length()));
        }
    }

    private void validateInvalidName(String name) {
        if (!VALID_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException(String.format("노선 이름에 유효하지 않은 문자가 있습니다. 노선 이름 : %s", name));
        }
    }

    public String getName() {
        return name;
    }
}
