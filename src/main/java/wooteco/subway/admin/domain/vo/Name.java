package wooteco.subway.admin.domain.vo;

import java.util.Objects;

public class Name {
    private String name;

    public Name(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("name은 null이 올 수 없습니다.");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name은 빈 값이 올 수 없습니다.");
        }
        if (name.matches(".*[ ].*")) {
            throw new IllegalArgumentException("name은 공백이 포함 될 수 없습니다.");
        }
    }

    public String getName() {
        return name;
    }
}
