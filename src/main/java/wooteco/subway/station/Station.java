package wooteco.subway.station;

public class Station {

    private Long id;
    private String name;

    private Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Station of(Long id, String name) {
        return new Station(id, name);
    }

    public static Station of(String name) {
        return of(null, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }
}

