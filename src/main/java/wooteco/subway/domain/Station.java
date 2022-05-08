package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    private void validateName(String name) {
        Objects.requireNonNull(name);
        validateBlankName(name);
        validateNameLength(name);
    }

    private void validateBlankName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("역 이름은 빈 문자열일 수 없습니다.");
        }
    }

    private void validateNameLength(String name) {
        if (name.length() > 255) {
            throw new IllegalArgumentException("역 이름은 255자를 초과하면 안됩니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

