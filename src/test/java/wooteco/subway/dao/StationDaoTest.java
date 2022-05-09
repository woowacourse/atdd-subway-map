package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Station;
import wooteco.subway.entity.StationEntity;

@JdbcTest
class StationDaoTest {

    private final StationDao stationDao;

    @Autowired
    StationDaoTest(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.stationDao = new StationDao(namedParameterJdbcTemplate);
    }

    @DisplayName("지하철역을 저장한다.")
    @Test
    void saveStation() {
        StationEntity stationEntity = StationEntity.of("강남역");
        Station savedStation = stationDao.save(stationEntity);

        assertAll(
                () -> assertThat(savedStation.getId()).isNotZero(),
                () -> assertThat(savedStation.getName()).isEqualTo("강남역")
        );
    }

    @DisplayName("특정 지하철역을 이름으로 조회한다.")
    @Test
    void findByName() {
        StationEntity stationEntity = StationEntity.of("강남역");
        stationDao.save(stationEntity);
        Optional<Station> wrappedStation = stationDao.findByName("강남역");
        assert (wrappedStation).isPresent();

        assertAll(
                () -> assertThat(wrappedStation.get().getId()).isNotZero(),
                () -> assertThat(wrappedStation.get().getName()).isEqualTo("강남역")
        );
    }

    @DisplayName("특정 지하철역을 삭제한다.")
    @Test
    void deleteById() {
        StationEntity stationEntity = StationEntity.of("강남역");
        Station savedStation = stationDao.save(stationEntity);
        stationDao.deleteById(savedStation.getId());

        Optional<Station> wrappedStation = stationDao.findByName("강남역");
        assertThat(wrappedStation).isEmpty();
    }

    @DisplayName("특정 지하철역을 아이디로 조회한다.")
    @Test
    void findById() {
        StationEntity stationEntity = StationEntity.of("강남역");
        Station savedStation = stationDao.save(stationEntity);
        Optional<Station> wrappedStation = stationDao.findById(savedStation.getId());
        assert (wrappedStation).isPresent();

        assertAll(
                () -> assertThat(wrappedStation.get().getId()).isEqualTo(savedStation.getId()),
                () -> assertThat(wrappedStation.get().getName()).isEqualTo(savedStation.getName())
        );
    }
}
