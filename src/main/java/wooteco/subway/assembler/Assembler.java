package wooteco.subway.assembler;

import wooteco.subway.service.LineService;
import wooteco.subway.dao.line.LineDaoCache;
import wooteco.subway.service.StationService;
import wooteco.subway.dao.station.StationDaoCache;

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
