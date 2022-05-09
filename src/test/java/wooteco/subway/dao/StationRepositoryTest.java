package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import wooteco.subway.infra.dao.StationDao;
import wooteco.subway.infra.entity.StationEntity;

@JdbcTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@DisplayName("Station 레포지토리")
class StationRepositoryTest {

    private final StationDao stationRepository;

    public StationRepositoryTest(JdbcTemplate jdbcTemplate, DataSource dataSource,
                                 NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.stationRepository = new StationDao(jdbcTemplate, dataSource, namedParameterJdbcTemplate);
    }

    @Test
    @DisplayName("StationEntity 저장")
    void save() {
        final StationEntity stationEntity = new StationEntity(1L, "선릉역");
        final StationEntity saved = stationRepository.save(stationEntity);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(stationEntity.getName()).isEqualTo(saved.getName())
        );
    }

    @Test
    @DisplayName("StationEntity 전체 조회")
    void findAll() {
        final StationEntity stationEntity = new StationEntity(1L, "선릉역");
        final StationEntity stationEntity2 = new StationEntity(2L, "삼성역");
        stationRepository.save(stationEntity);
        stationRepository.save(stationEntity2);

        final List<StationEntity> lines = stationRepository.findAll();

        assertThat(lines).extracting("name").containsAll(List.of(stationEntity.getName(), stationEntity2.getName()));
    }

    @Test
    @DisplayName("StationEntity 단 건 조회")
    void findById() {
        final StationEntity stationEntity = new StationEntity(1L, "선릉역");
        final StationEntity saved = stationRepository.save(stationEntity);

        final Long id = saved.getId();
        final Optional<StationEntity> found = stationRepository.findById(id);
        final StationEntity savedEntity = found.orElseThrow(NoSuchElementException::new);

        assertAll(
                () -> assertThat(savedEntity.getId()).isEqualTo(id),
                () -> assertThat(savedEntity.getName()).isEqualTo(stationEntity.getName())
        );
    }

    @Test
    @DisplayName("StationEntity 업데이트")
    void updateById() {
        final StationEntity stationEntity = new StationEntity("선릉역");
        final StationEntity saved = stationRepository.save(stationEntity);

        final StationEntity newStationEntity = new StationEntity(saved.getId(), "삼성역");
        final long affectedRow = stationRepository.updateById(newStationEntity);
        final Optional<StationEntity> updated = stationRepository.findById(newStationEntity.getId());

        assertAll(
                () -> assertThat(affectedRow).isOne(),
                () -> assertThat(updated).isPresent(),
                () -> assertThat(updated.get().getName()).isEqualTo(newStationEntity.getName())
        );
    }

    @Test
    @DisplayName("LineEntity 단 건 삭제")
    void deleteById() {
        final StationEntity lineEntity = new StationEntity(1L, "선릉역");
        final StationEntity saved = stationRepository.save(lineEntity);
        final Long id = saved.getId();

        final Optional<StationEntity> before = stationRepository.findById(id);
        stationRepository.deleteById(id);
        final Optional<StationEntity> after = stationRepository.findById(id);

        assertAll(
                () -> assertThat(before).isPresent(),
                () -> assertThat(after).isNotPresent()
        );
    }
}
