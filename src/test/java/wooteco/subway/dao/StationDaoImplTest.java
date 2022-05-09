package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.entity.StationEntity;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDaoImpl(jdbcTemplate);

        List<StationEntity> stationEntities = stationDao.findAll();
        List<Long> stationIds = stationEntities.stream()
            .map(StationEntity::getId)
            .collect(Collectors.toList());

        for (Long stationId : stationIds) {
            stationDao.deleteById(stationId);
        }
    }

    @Test
    void save() {
        // given
        Station Station = new Station("범고래");

        // when
        StationEntity result = stationDao.save(Station);

        // then
        assertThat(Station.getName()).isEqualTo(result.getName());
    }

    @Test
    void findAll() {
        // given
        StationEntity Station1 = stationDao.save(new Station("범고래"));
        StationEntity Station2 = stationDao.save(new Station("애쉬"));

        // when
        List<StationEntity> stationEntities = stationDao.findAll();

        // then
        assertThat(stationEntities)
            .hasSize(2)
            .contains(Station1, Station2);
    }

    @Test
    void validateDuplication() {
        // given
        Station Station1 = new Station("범고래");
        Station Station2 = new Station("범고래");

        // when
        stationDao.save(Station1);

        // then
        assertThatThrownBy(() -> stationDao.save(Station2))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void delete() {
        // given
        StationEntity Station = stationDao.save(new Station("범고래"));

        // when
        stationDao.deleteById(Station.getId());
        List<StationEntity> stationEntities = stationDao.findAll();

        // then
        assertThat(stationEntities)
            .hasSize(0)
            .doesNotContain(Station);
    }
}
