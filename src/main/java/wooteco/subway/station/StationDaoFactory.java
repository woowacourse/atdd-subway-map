package wooteco.subway.station;

public class StationDaoFactory {

    private static final StationDao stationDao = new StationDao();

    public static StationDao makeStationDao() {
        return stationDao;
    }

}
