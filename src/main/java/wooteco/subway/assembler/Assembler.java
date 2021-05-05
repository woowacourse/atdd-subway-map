package wooteco.subway.assembler;

import wooteco.subway.line.dao.LineDaoCache;
import wooteco.subway.line.LineService;
import wooteco.subway.station.dao.StationDaoCache;
import wooteco.subway.station.StationService;

public class Assembler {

    private final StationService stationService;
    private final LineService lineService;

    public Assembler() {
        this.stationService = new StationService(new StationDaoCache());
        this.lineService = new LineService(new LineDaoCache());
    }

    public StationService getStationService() {
        return stationService;
    }

    public LineService getLineService() {
        return lineService;
    }
}
