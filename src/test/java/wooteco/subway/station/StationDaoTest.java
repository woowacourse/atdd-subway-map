package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationDaoTest {
    @Test
    @DisplayName("역 생성 저장 확인")
    void save() {
        Station station = new Station("서울역");
        StationDao stationDao = new StationDao();
        stationDao.save(station);
        assertTrue(stationDao.findAll().contains(station));
    }
}