package wooteco.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Stations {

    private final List<Station> value;

    public Stations(final List<Station> value) {
        this.value = value;
    }

    public int calculateMatchCount(final Long... ids) {
        final List<Long> stationIds = value.stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        return (int) Arrays.stream(ids)
                .filter(stationIds::contains)
                .count();
    }
}
