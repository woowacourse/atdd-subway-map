package wooteco.subway.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철역 관련 가짜 DAO 테스트")
class StationMockDaoTest {

    private static final Station STATION = new Station("강남역");

    private final StationMockDao stationMockDao = new StationMockDao();

    @AfterEach
    void afterEach() {
        stationMockDao.clear();
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void save() {
        stationMockDao.save(STATION);

        assertThat(stationMockDao.findAll()).hasSize(1);
    }

    @DisplayName("중복된 아이디의 지하철역이 있다면 true 를 반환한다.")
    @Test
    void existStationById() {
        long stationId = stationMockDao.save(STATION);

        assertThat(stationMockDao.existStationById(stationId)).isTrue();
    }

    @DisplayName("중복된 이름의 지하철역이 있다면 true 를 반환한다.")
    @Test
    void existStationByName() {
        stationMockDao.save(STATION);

        assertThat(stationMockDao.existStationByName("강남역")).isTrue();
    }

    @DisplayName("지하철역의 전체 목록을 조회한다.")
    @Test
    void findAll() {
        stationMockDao.save(STATION);

        assertThat(stationMockDao.findAll()).hasSize(1);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        long stationId = stationMockDao.save(STATION);

        stationMockDao.delete(stationId);

        assertThat(stationMockDao.findAll()).hasSize(0);
    }

    @DisplayName("모든 지하철역 정보를 삭제한다.")
    @Test
    void clear() {
        stationMockDao.save(STATION);

        stationMockDao.clear();

        assertThat(stationMockDao.findAll()).hasSize(0);
    }
}
