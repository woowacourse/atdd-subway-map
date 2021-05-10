package wooteco.util;

import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationId;
import wooteco.subway.domain.station.StationName;

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
