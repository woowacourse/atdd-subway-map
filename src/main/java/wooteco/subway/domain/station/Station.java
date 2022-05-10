package wooteco.subway.domain.station;

public class Station {

    private static final long TEMPORARY_ID = 0L;

    private final Long id;
    private final StationName name;

    public Station(Long id, String name) {
        this.id = id;
        this.name = new StationName(name);
    }

    public Station(String name) {
        this(TEMPORARY_ID, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
