package wooteco.subway.domain;

public class Station {
    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
