package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.domain.fixtures.TestFixtures.성수;
import static wooteco.subway.domain.fixtures.TestFixtures.왕십리;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SectionRepositoryTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("delete from section");
        jdbcTemplate.update("delete from line");
        jdbcTemplate.update("delete from station");
    }

    @Test
    @DisplayName("구간을 노선으로 조회한다.")
    void create() {
        Station 성수역 = stationRepository.save(new Station("성수역"));
        Station 건대입구 = stationRepository.save(new Station("건대입구"));
        Line line = lineRepository.save(new Line(1L, "1호선", "blue"));

        sectionRepository.save(new Section(line, 성수역, 건대입구, 10));

        List<Section> section = sectionRepository.findSectionByLine(line);
        assertThat(section).hasSize(1);
        assertThat(section.get(0).getUpStation()).isEqualTo(성수역);
        assertThat(section.get(0).getDownStation()).isEqualTo(건대입구);
    }

}
