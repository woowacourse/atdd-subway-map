package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private static final long TEMPORARY_ID = 0L;
    private static final String NULL_ERROR_MESSAGE = "빈 값이 들어올 수 없습니다.";

    private final Long id;
    private final String name;

    public Station(String name) {
        this(TEMPORARY_ID, name);
    }

    public Station(Long id, String name) {
        validateNotNull(id);
        validateNotNull(name);
        this.id = id;
        this.name = name;
    }

    private <T> void validateNotNull(T object) {
        try {
            Objects.requireNonNull(object);
        } catch (NullPointerException exception) {
            throw new IllegalArgumentException(NULL_ERROR_MESSAGE);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

