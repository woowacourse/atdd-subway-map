package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.SectionDto;

@SpringBootTest
@Sql("/testSchema.sql")
public class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    @Autowired
    public LineRepositoryTest(JdbcTemplate jdbcTemplate) {
        this.lineDao = new JdbcLineDao(jdbcTemplate);
        this.sectionDao = new JdbcSectionDao(jdbcTemplate);
        this.stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @DisplayName("노선 저장")
    @Test
    void 노선_저장() {
        Station A = stationDao.save(new Station("A"));
        Station B = stationDao.save(new Station("B"));
        Section section = new Section(A, B, 10);
        Line line = new Line("A호선", "yellow", new Sections(section));

        Long lineId = lineRepository.save(line);
        LineDto result = lineDao.findById(lineId);

        assertAll(
                () -> assertThat(result.getName()).isEqualTo("A호선"),
                () -> assertThat(result.getColor()).isEqualTo("yellow")
        );
    }

    @DisplayName("단일 노선 조회")
    @Test
    void 노선_조회() {
        Station A = stationDao.save(new Station("A"));
        Station B = stationDao.save(new Station("B"));
        Section section = new Section(A, B, 10);
        Line line = new Line("A호선", "yellow", new Sections(section));

        LineDto savedLine = lineDao.save(LineDto.from(line));
        sectionDao.save(SectionDto.of(section, savedLine.getId()));

        Line result = lineRepository.findById(savedLine.getId());

        assertAll(
                () -> assertThat(result.getName()).isEqualTo("A호선"),
                () -> assertThat(result.getColor()).isEqualTo("yellow"),
                () -> assertThat(result.getSections()).isEqualTo(line.getSections())
        );
    }
}
