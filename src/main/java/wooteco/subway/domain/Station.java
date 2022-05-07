package wooteco.subway.domain;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        validateNameNotEmpty(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        validateNameNotEmpty(name);
        this.name = name;
    }

    private void validateNameNotEmpty(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("이름은 비워둘 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

