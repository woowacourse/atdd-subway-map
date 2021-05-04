package wooteco.subway.station;

import wooteco.subway.exception.StationSuffixException;

public class Station {
    private static final String SUFFIX = "역";

    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        validateSuffix(name);
        this.name = name;
    }

    private void validateSuffix(String name) {
        if (isNotEndsWithStation(name)) {
            throw new StationSuffixException();
        }
    }

    private boolean isNotEndsWithStation(String name) {
        return !name.endsWith(SUFFIX);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

