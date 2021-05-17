package wooteco.subway.station.domain;

import wooteco.subway.station.exception.InvalidStationNameException;

import java.util.Objects;
import java.util.regex.Pattern;

public class StationName {
    private static final int MAX_NAME_LENGTH = 20;
    private static final Pattern ONLY_CAN_COMPLETE_KOREAN_AND_MIDDLE_POINT_DOT_AND_ENGLISH_AND_NUMBERS_AND_PARENTHESES_AND_BLANK_PATTERN
            = Pattern.compile("^[·가-힣a-zA-Z0-9\\(\\)\\s]*$");

    private final String name;

    public StationName(final String name) {
        String trimAndRemoveDuplicateBlankName = name.trim().replaceAll(" +", " ");
        validateNameLength(trimAndRemoveDuplicateBlankName);
        validateInvalidName(trimAndRemoveDuplicateBlankName);
        this.name = trimAndRemoveDuplicateBlankName;
    }

    private void validateNameLength(String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidStationNameException(String.format("역 이름은 %d자를 초과할 수 없습니다. 이름의 길이 : %d", MAX_NAME_LENGTH, name.length()));
        }
    }

    private void validateInvalidName(String name) {
        if (!ONLY_CAN_COMPLETE_KOREAN_AND_MIDDLE_POINT_DOT_AND_ENGLISH_AND_NUMBERS_AND_PARENTHESES_AND_BLANK_PATTERN.matcher(name).matches()) {
            throw new InvalidStationNameException(String.format("역 이름에 유효하지 않은 문자가 있습니다. 역 이름 : %s", name));
        }
    }

    public String text() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationName that = (StationName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
