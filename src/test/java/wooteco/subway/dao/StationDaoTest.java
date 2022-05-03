package wooteco.subway.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class StationDaoTest {

    @AfterEach
    void tearDown() {
        List<Long> stationIds = StationDao.findAll().stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        for (Long stationId : stationIds) {
            StationDao.deleteById(stationId);
        }
    }

    @Test
    @DisplayName("새로운 지하철 역을 등록할 수 있다.")
    void save() {
        Station station = new Station("선릉역");
        Station savedStation = StationDao.save(station);

        assertThat(savedStation).isNotNull();
    }

    @Test
    @DisplayName("등록된 지하철 역들을 반환한다.")
    void findAll() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        StationDao.save(station1);
        StationDao.save(station2);
        StationDao.save(station3);

        assertThat(StationDao.findAll()).containsAll(List.of(station1, station2, station3));
    }

    @Test
    @DisplayName("등록된 지하철을 삭제한다.")
    void deleteById() {
        Station station = new Station("선릉역");
        Station savedStation = StationDao.save(station);
        Long id = savedStation.getId();

        StationDao.deleteById(id);

        assertThat(StationDao.findAll()).hasSize(0);
    }
}
