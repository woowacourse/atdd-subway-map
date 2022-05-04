package wooteco.subway.domain;

public class Station {

    private static final long TEMPORARY_ID = 0L;

    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this.id = TEMPORARY_ID;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

