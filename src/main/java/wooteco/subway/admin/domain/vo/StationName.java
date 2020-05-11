package wooteco.subway.admin.domain.vo;

import java.util.Objects;

public class StationName {

    private String name;

    public StationName(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("Station.name은 null이 올 수 없습니다.");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Station.name은 빈 값이 올 수 없습니다.");
        }
        if (name.matches(".*[ ].*")) {
            throw new IllegalArgumentException("Station.name은 공백이 포함 될 수 없습니다.");
        }
        if (name.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Station.name은 숫자가 포함 될 수 없습니다.");
        }
    }

    public String getName() {
        return name;
    }
}
