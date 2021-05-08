package wooteco.subway.assembler;

import wooteco.subway.line.LineService;
import wooteco.subway.line.dao.LineDaoMemory;
import wooteco.subway.station.StationService;
import wooteco.subway.station.dao.StationDaoMemory;

public class Assembler {

    private final StationService stationService;
    private final LineService lineService;

    public Assembler() {
        this.stationService = new StationService(new StationDaoMemory());
        this.lineService = new LineService(new LineDaoMemory());
    }

    public StationService getStationService() {
        return stationService;
    }

    public LineService getLineService() {
        return lineService;
    }
}
