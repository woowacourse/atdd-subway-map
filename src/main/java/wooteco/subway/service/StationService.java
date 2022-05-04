//package wooteco.subway.service;
//
//import org.springframework.stereotype.Service;
//import wooteco.subway.dao.StationDao;
//import wooteco.subway.domain.Station;
//import wooteco.subway.dto.station.StationResponse;
//
//@Service
//public class StationService {
//
//    private final StationDao stationDao;
//
//    public StationService(StationDao stationDao) {
//        this.stationDao = stationDao;
//    }
//
//    public static StationResponse createStation(String name) {
//        if (StationDao.existStationByName(name)) {
//            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
//        }
//        Station station = new Station(name);
//        Station newStation = StationDao.save(station);
//        return new StationResponse(newStation.getId(), newStation.getName());
//    }
//
//    public static void deleteStation(String name) {
//        StationDao.deleteByName(name);
//    }
//}
