package wooteco.subway.station;

import wooteco.subway.exception.station.StationNamePatternException;

import java.util.regex.Pattern;

public class StationName {

    private static final Pattern PATTERN = Pattern.compile("^[가-힣|A-Z|a-z| 0-9]*역$");

    private final String name;

    public StationName(String name) {
        validate(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private void validate(String name) {
        if (!PATTERN.matcher(name).matches()) {
            throw new StationNamePatternException();
        }
    }
}
