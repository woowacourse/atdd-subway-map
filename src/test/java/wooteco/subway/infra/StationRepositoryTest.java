package wooteco.subway.infra;

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
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Station;
import wooteco.subway.infra.dao.StationDao;
import wooteco.subway.infra.repository.JdbcStationRepository;
import wooteco.subway.infra.repository.StationRepository;

@JdbcTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@DisplayName("Station 레포지토리")
@Sql("classpath:/schema.sql")
class StationRepositoryTest {

    private final StationRepository stationRepository;

    public StationRepositoryTest(JdbcTemplate jdbcTemplate, DataSource dataSource,
                                 NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        final StationDao stationDao = new StationDao(jdbcTemplate, dataSource, namedParameterJdbcTemplate);
        this.stationRepository = new JdbcStationRepository(stationDao);
    }

    @Test
    @DisplayName("Station 저장")
    void save() {
        // given
        final Station newStation = new Station("선릉역");

        // when
        final Station saved = stationRepository.save(newStation);

        // then
        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(newStation.getName())
        );
    }

    @Test
    @DisplayName("Station 전체 조회")
    void findAll() {
        // given
        final Station station1 = new Station("선릉역");
        final Station station2 = new Station("삼성역");
        stationRepository.save(station1);
        stationRepository.save(station2);

        // when
        final List<Station> stations = stationRepository.findAll();

        // then
        assertThat(stations).extracting("name").containsAll(List.of(station1.getName(), station2.getName()));
    }

    @Test
    @DisplayName("Station 단 건 조회")
    void findById() {
        // given
        final Station station = new Station("선릉역");
        final Station saved = stationRepository.save(station);

        // when
        final Long savedId = saved.getId();
        final Optional<Station> foundOptional = stationRepository.findById(savedId);
        final Station found = foundOptional.orElseThrow(NoSuchElementException::new);

        // then
        assertAll(
                () -> assertThat(found.getId()).isEqualTo(savedId),
                () -> assertThat(found.getName()).isEqualTo(station.getName()),
                () -> assertThat(found.getName()).isEqualTo(saved.getName())
        );
    }

    @Test
    @DisplayName("Station 단 건 삭제")
    void deleteById() {
        // given
        final Station station = new Station("선릉역");
        final Station saved = stationRepository.save(station);
        final Long id = saved.getId();

        // when
        final Optional<Station> before = stationRepository.findById(id);
        stationRepository.deleteById(id);
        final Optional<Station> after = stationRepository.findById(id);

        // then
        assertAll(
                () -> assertThat(before).isPresent(),
                () -> assertThat(after).isNotPresent()
        );
    }
}
