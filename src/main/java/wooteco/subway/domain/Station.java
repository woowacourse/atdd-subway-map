package wooteco.subway.domain;

public class Station {

    private Id id;
    private Name name;

    public Station() {
    }

    public Station(final String name) {
        this(null, new Name(name));
    }

    public Station(final Long id, final String name) {
        this(new Id(id), new Name(name));
    }

    public Station(final Id id, final Name name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id.getValue();
    }

    public String getName() {
        return name.getValue();
    }
}
