package wooteco.subway.domain;

public class Station {

    private static final int MAX_NAME_LENGTH = 15;

    private Long id;
    private final String name;

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Station(final String name) {
        validateName(name);
        this.name = name;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("역의 이름이 공백이 되어서는 안됩니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("역의 이름이 " + MAX_NAME_LENGTH + "자를 넘어서는 안됩니다.");
        }
    }

    public boolean isSameName(final Station station) {
        return name.equals(station.name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

