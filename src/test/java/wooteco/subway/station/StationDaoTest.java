package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StationDaoTest {

    private StationDao stationDao;
    private Station station;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao();
        station = new Station("서울역");
    }

    @Test
    @DisplayName("역 생성 저장 확인")
    void save() {
        stationDao.save(station);
        assertTrue(stationDao.findAll()
                             .contains(station));
    }

    @Test
    @DisplayName("역 삭제 확인")
    void delete() {
        Long savedStationId = stationDao.save(station)
                                        .getId();
        assertThat(stationDao.findById(savedStationId)).isNotNull();
        stationDao.delete(savedStationId);
        assertThatThrownBy(() -> stationDao.findById(savedStationId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}