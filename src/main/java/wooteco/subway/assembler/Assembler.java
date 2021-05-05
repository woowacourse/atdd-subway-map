package wooteco.subway.assembler;

import wooteco.subway.station.StationDaoCache;
import wooteco.subway.station.StationService;

public class Assembler {

    private final StationService stationService;

    public Assembler() {
        this.stationService = new StationService(new StationDaoCache());
    }

    public StationService getStationService() {
        return stationService;
    }
}
