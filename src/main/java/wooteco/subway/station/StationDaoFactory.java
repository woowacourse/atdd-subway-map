package wooteco.subway.station;

public class StationDaoFactory {

    private static final StationDao stationDao = new StationDao();

    private StationDaoFactory() {}

    public static StationDao makeStationDao() {
        return stationDao;
    }

}
