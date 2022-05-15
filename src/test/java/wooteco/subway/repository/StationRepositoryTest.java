package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("delete from station");
    }

    @Test
    @DisplayName("역을 생성한다.")
    void create() {
        Station 성수역 = stationRepository.save(new Station("성수역"));

        assertThat(성수역.getId()).isNotNull();
    }

    @Test
    @DisplayName("역을 id로 조회한다")
    void findById() {
        Station 성수역 = stationRepository.save(new Station("성수역"));

        assertThat(stationRepository.findById(성수역.getId())).isEqualTo(성수역);
    }

    @Test
    @DisplayName("저장된 역들을 조회한다.")
    void findAll() {
        stationRepository.save(new Station("성수역"));
        stationRepository.save(new Station("창동역"));
        stationRepository.save(new Station("건대입구"));

        assertThat(stationRepository.findAll()).hasSize(3);
    }

    @Test
    @DisplayName("id로 역을 삭제한다.")
    void deleteById() {
        Station 성수역 = stationRepository.save(new Station("성수역"));

        stationRepository.deleteById(성수역.getId());

        assertThat(stationRepository.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("역의 이름이 존재하는지 확인한다.")
    void existsStation() {
        stationRepository.save(new Station("성수역"));

        assertThat(stationRepository.existByName("성수역")).isTrue();
    }

}
