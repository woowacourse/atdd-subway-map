package wooteco.subway.domain;

public class Station {

    private Long id;
    private String name;

    public Station(String name) {
        this(null, name);
    }

    public Station(final Long id, final String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("역 이름은 공백일 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
