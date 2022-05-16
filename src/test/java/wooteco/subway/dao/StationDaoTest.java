package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
        StationEntity stationEntity = new StationEntity.Builder("강남역")
                .build();
        StationEntity savedStationEntity = stationDao.save(stationEntity);

        assertAll(
                () -> assertThat(savedStationEntity.getId()).isNotZero(),
                () -> assertThat(savedStationEntity.getName()).isEqualTo("강남역")
        );
    }

    @DisplayName("특정 지하철역을 이름으로 조회한다.")
    @Test
    void findByName() {
        StationEntity stationEntity = new StationEntity.Builder("강남역")
                .build();
        stationDao.save(stationEntity);
        Optional<StationEntity> wrappedStationEntity = stationDao.findByName("강남역");
        assert (wrappedStationEntity).isPresent();

        assertAll(
                () -> assertThat(wrappedStationEntity.get().getId()).isNotZero(),
                () -> assertThat(wrappedStationEntity.get().getName()).isEqualTo("강남역")
        );
    }

    @DisplayName("특정 지하철역을 삭제한다.")
    @Test
    void deleteById() {
        StationEntity stationEntity = new StationEntity.Builder("강남역")
                .build();
        StationEntity savedStationEntity = stationDao.save(stationEntity);
        stationDao.deleteById(savedStationEntity.getId());

        Optional<StationEntity> wrappedStationEntity = stationDao.findByName("강남역");
        assertThat(wrappedStationEntity).isEmpty();
    }

    @DisplayName("특정 지하철역을 아이디로 조회한다.")
    @Test
    void findById() {
        StationEntity stationEntity = new StationEntity.Builder("강남역")
                .build();
        StationEntity savedStationEntity = stationDao.save(stationEntity);
        Optional<StationEntity> wrappedStationEntity = stationDao.findById(savedStationEntity.getId());
        assert (wrappedStationEntity).isPresent();

        assertAll(
                () -> assertThat(wrappedStationEntity.get().getId()).isEqualTo(savedStationEntity.getId()),
                () -> assertThat(wrappedStationEntity.get().getName()).isEqualTo(savedStationEntity.getName())
        );
    }
}
