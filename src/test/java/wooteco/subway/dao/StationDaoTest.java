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
import wooteco.subway.dto.StationResponse;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@JdbcTest
@Transactional
class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        lineRepository = new LineRepository(jdbcTemplate);
        sectionRepository = new SectionRepository(jdbcTemplate);
        stationRepository = new StationRepository(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    void queryByLineId() {
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        Station station1 = stationRepository.save(new Station("강남역"));
        Station station2 = stationRepository.save(new Station("역삼역"));

        sectionRepository.save(new Section(line, station1, station2, 10));

        List<StationResponse> response = stationDao.queryByLineId(line.getId());
        List<StationResponse> expected = Stream.of(station1, station2)
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());

        assertThat(response).isEqualTo(expected);
    }


}