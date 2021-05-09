package wooteco.util;

import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationId;
import wooteco.subway.station.domain.StationName;

public class StationFactory {

    public static Station create(Long id, String name) {
        return new Station(
                new StationId(id),
                new StationName(name)
        );
    }

    public static Station create(String name) {
        return new Station(
                new StationName(name)
        );
    }

}
