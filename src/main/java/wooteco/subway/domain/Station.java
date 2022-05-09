package wooteco.subway.domain;

public class Station {
    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        validateNameLength(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    private void validateNameLength(String name) {
        if (name.isBlank() || name.length() > 20) {
            throw new IllegalArgumentException("역 이름은 최소 1글자이상 20글자 이하여야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
