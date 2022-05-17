package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.domain.StationException;

public class Station {

    private static final int MAX_NAME_LENGTH = 15;

    private final Long id;
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
            throw new StationException(ExceptionMessage.BLANK_STATION_NAME.getContent());
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new StationException(
                    String.format(ExceptionMessage.OVER_MAX_LENGTH_STATION_NAME.getContent(), MAX_NAME_LENGTH));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
