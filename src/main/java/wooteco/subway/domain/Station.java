package wooteco.subway.domain;

public class Station {

    private Id id;
    private Name name;

    public Station() {
    }

    public Station(String name) {
        this(null, new Name(name));
    }

    public Station(Long id, String name) {
        this(new Id(id), new Name(name));
    }

    public Station(Id id, Name name) {
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
