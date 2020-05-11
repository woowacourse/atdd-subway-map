package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import wooteco.subway.admin.domain.vo.StationName;

public class Station {

    @Id
    private Long id;
    @Embedded.Nullable
    private StationName stationName;

    public Station() {
    }

    public Station(StationName stationName) {
        this.stationName = stationName;
    }

    public Station(String stationName) {
        this(new StationName(stationName));
    }

    public Long getId() {
        return id;
    }

    public StationName getStationName() {
        return stationName;
    }
}
