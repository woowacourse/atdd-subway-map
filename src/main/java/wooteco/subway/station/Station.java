package wooteco.subway.station;

public class Station {
    private static final Long NOT_EXIST_ID = -1L;

    private Long id;
    private String name;

    public Station(String name) {
        this(NOT_EXIST_ID, name);
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

