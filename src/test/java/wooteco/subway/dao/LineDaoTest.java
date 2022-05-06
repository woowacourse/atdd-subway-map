package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@JdbcTest
@Transactional
class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineRepository = new LineRepository(jdbcTemplate);
        sectionRepository = new SectionRepository(jdbcTemplate);
        stationRepository = new StationRepository(jdbcTemplate);
        lineDao = new LineDao(jdbcTemplate, new StationDao(jdbcTemplate));
    }

    @Test
    void queryById() {
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        Station station1 = stationRepository.save(new Station("강남역"));
        Station station2 = stationRepository.save(new Station("역삼역"));

        sectionRepository.save(new Section(line, station1, station2, 10));

        LineResponse response = lineDao.queryById(line.getId()).orElseThrow();
        List<StationResponse> actual = response.getStations();
        List<StationResponse> expected = Stream.of(station1, station2)
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());

        assertThat(response.getId()).isEqualTo(line.getId());
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(response.getColor()).isEqualTo("bg-red-600");
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}