package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    private void validateName(String name) {
        Objects.requireNonNull(name, "이름은 Null 일 수 없습니다.");
        validateNameLength(name);
    }

    private void validateNameLength(String name) {
        int length = name.length();
        if (length < 1 || length > 30) {
            throw new IllegalArgumentException("이름은 1~30 자 이내여야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

