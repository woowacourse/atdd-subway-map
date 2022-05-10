package wooteco.subway.domain;

public class Station {
    private final Long id;
    private final Name name;

    public Station(Long id, Name name) {
        this.id = id;
        this.name = name;
    }

    public Station(Long id, String name) {
        this(id, new Name(name));
    }

    public Station(String name) {
        this(null, new Name(name));
    }

    public Station() {
        this(null);
    }

    public boolean hasSameNameWith(Station otherStation) {
        return this.name.equals(otherStation.name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }
}

