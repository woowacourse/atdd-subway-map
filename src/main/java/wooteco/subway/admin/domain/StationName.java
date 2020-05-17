package wooteco.subway.admin.domain;

import wooteco.subway.admin.exception.InvalidStationNameException;

public class StationName {
    private static final String NUMBER_REGEX = ".*[0-9].*";
    private static final String BLANK = " ";

    private final String name;

    public StationName(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        if (name == null || name.isEmpty()) {
            throw new InvalidStationNameException("이름이 비어있다.");
        }
        if (name.contains(BLANK)) {
            throw new InvalidStationNameException("이름에 공백이 존재한다.");
        }
        if (name.matches(NUMBER_REGEX)) {
            throw new InvalidStationNameException("이름에 숫자가 존재한다.");
        }
    }

    public String getName() {
        return name;
    }
}
