package wooteco.subway.station.domain;

import java.util.regex.Pattern;

public class StationName {
    private final static Pattern STATION_NAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]*역$");

    private String value;

    public StationName(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (!STATION_NAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("지하철 역 이름이 잘못되었습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
