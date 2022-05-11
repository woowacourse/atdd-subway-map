package wooteco.subway.repository.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.dao.entity.EntityAssembler;
import wooteco.subway.repository.dao.entity.StationEntity;

@JdbcTest
class JdbcStationDaoTest {

    @Autowired
    private DataSource dataSource;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        this.stationDao = new JdbcStationDao(dataSource);
    }

    @DisplayName("지하철역을 저장한다.")
    @Test
    void save() {
        StationEntity stationEntity = EntityAssembler.stationEntity(new Station("강남역"));
        assertThat(stationDao.save(stationEntity)).isGreaterThan(0);
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void findAll() {
        List<StationEntity> stationEntities = List.of(
                EntityAssembler.stationEntity(new Station("강남역")),
                EntityAssembler.stationEntity(new Station("역삼역")),
                EntityAssembler.stationEntity(new Station("선릉역"))
        );
        stationEntities.forEach(stationDao::save);
        assertThat(stationDao.findAll()).hasSize(3);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void findById() {
        StationEntity expected = EntityAssembler.stationEntity(new Station("강남역"));
        Long stationId = stationDao.save(expected);
        Optional<StationEntity> actual = stationDao.findById(stationId);
        assertAll(
                () -> assertThat(actual.isPresent()).isTrue(),
                () -> assertThat(actual.get()).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(expected)
        );
    }

    @DisplayName("존재하지 않는 지하철역을 조회한다.")
    @Test
    void findWithNonexistentId() {
        Optional<StationEntity> stationEntity = stationDao.findById(1L);
        assertThat(stationEntity.isEmpty()).isTrue();
    }

    @DisplayName("해당 이름의 지하철역이 존재하는지 확인한다.")
    @Test
    void existsByName() {
        StationEntity stationEntity = EntityAssembler.stationEntity(new Station("강남역"));
        stationDao.save(stationEntity);
        assertThat(stationDao.existsByName(stationEntity.getName())).isTrue();
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void remove() {
        Long stationId = stationDao.save(EntityAssembler.stationEntity(new Station("강남역")));
        stationDao.remove(stationId);
        assertThat(stationDao.findAll()).isEmpty();
    }
}
