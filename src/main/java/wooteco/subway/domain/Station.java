package wooteco.subway.domain;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(final String name) {
        this(0L, name);
    }

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean sameId(final Long id) {
        return this.id.equals(id);
    }

    public boolean sameName(final String name) {
        return this.name.equals(name);
    }
}

