package wooteco.subway;

import wooteco.subway.line.LineDao;
import wooteco.subway.line.LineRepository;
import wooteco.subway.line.LineService;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationRepository;
import wooteco.subway.station.StationService;

public class AppConfig {
    private static StationRepository stationRepository = new StationDao();
    private static StationService stationService = new StationService(stationRepository);
    private static LineRepository lineRepository = new LineDao();
    private static LineService lineService = new LineService(lineRepository);

    public static StationRepository stationRepository() {
        return stationRepository;
    }

    public static StationService stationService() {
        return stationService;
    }

    public static LineRepository lineRepository() {
        return lineRepository;
    }

    public static LineService lineService() {
        return lineService;
    }
}
