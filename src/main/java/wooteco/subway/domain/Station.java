package wooteco.subway.domain;

import wooteco.subway.exception.ExceptionMessage;

public class Station {

    private static final int MAX_NAME_LENGTH = 15;

    private Long id;
    private final String name;

    public Station(Long id, String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException(ExceptionMessage.BLANK_STATION_NAME.getContent());
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format(ExceptionMessage.OVER_MAX_LENGTH_STATION_NAME.getContent(), MAX_NAME_LENGTH));
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
