package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;

@SpringBootTest
@Transactional
class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @Test
    @DisplayName("지하철역을 등록할 수 있다.")
    void save() {
        // given
        final Station station = new Station("지하철역이름");

        // when
        final Long savedId = stationDao.save(station);

        // then
        final Station savedStation = stationDao.findById(savedId);
        assertThat(station).isEqualTo(savedStation);
    }

    @Test
    @DisplayName("지하철역 목록을 조회할 수 있다.")
    void findAll() {
        // given
        final Station station1 = new Station("지하철역이름");
        final Station station2 = new Station("새로운지하철역이름");
        final Station station3 = new Station("또다른지하철역이름");

        stationDao.save(station1);
        stationDao.save(station2);
        stationDao.save(station3);

        // when
        final List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations).hasSize(3)
                .extracting("name")
                .contains("지하철역이름", "새로운지하철역이름", "또다른지하철역이름");
    }

    @Test
    @DisplayName("지하철역을 삭제할 수 있다.")
    void deleteById() {
        // given
        final Station station = new Station("지하철역이름");
        final Long savedId = stationDao.save(station);

        // when & then
        assertDoesNotThrow(() -> stationDao.deleteById(savedId));
    }
}
