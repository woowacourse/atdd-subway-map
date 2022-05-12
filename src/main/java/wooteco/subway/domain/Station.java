package wooteco.subway.domain;

public class Station {
    private Long id;
    private final String name;


    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
        validateNullOrEmpty(id, name);
    }

    private void validateNullOrEmpty(final Long id, final String name) {
        if (id == null) {
            throw new NullPointerException();
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public Station(final String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

