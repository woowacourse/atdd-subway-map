package wooteco.subway.assembler;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.service.LineService;
import wooteco.subway.service.StationService;

public class Assembler {

    private static final StationDao stationDao = new StationDao();
    private static final LineDao lineDao = new LineDao();
    private static final StationService stationService = new StationService(stationDao);
    private static final LineService lineService = new LineService(lineDao);

    public static StationDao getStationDao() {
        return stationDao;
    }

    public static StationService getStationService() {
        return stationService;
    }

    public static LineDao getLineDao() {
        return lineDao;
    }

    public static LineService getLineService() {
        return lineService;
    }
}
