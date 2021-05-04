package wooteco.subway;

import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationRepository;
import wooteco.subway.station.StationService;

public class AppConfig {
    private static StationRepository stationRepository = new StationDao();
    private static StationService stationService = new StationService(stationRepository);

    public static StationRepository stationRepository() {
        return stationRepository;
    }

    public static StationService stationService() {
        return stationService;
    }
}
