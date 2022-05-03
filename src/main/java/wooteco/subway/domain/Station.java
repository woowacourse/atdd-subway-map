package wooteco.subway.domain;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("역의 이름이 공백이 되어서는 안됩니다.");
        }
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

