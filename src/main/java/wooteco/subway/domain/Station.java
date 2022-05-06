package wooteco.subway.domain;

public class Station {

    private final Long id;
    private final String name;

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Station createWithoutId(final String name) {
        return new Station(null, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

