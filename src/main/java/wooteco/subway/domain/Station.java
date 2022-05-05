package wooteco.subway.domain;

public class Station {

    private static final long TEMPORARY_ID = 0L;

    private final Long id;
    private final Name name;

    public Station(String name) {
        this(TEMPORARY_ID, name);
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = new Name(name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isSameName(String name) {
        return this.name.isSame(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }
}

