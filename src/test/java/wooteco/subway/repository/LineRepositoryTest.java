package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.SectionDto;

@SpringBootTest
class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    @Autowired
    public LineRepositoryTest(JdbcTemplate jdbcTemplate) {
        this.lineDao = new JdbcLineDao(jdbcTemplate);
        this.sectionDao = new JdbcSectionDao(jdbcTemplate);
    }

    @DisplayName("새로운 노선 저장")
    @Test
    void 새로운_노선_저장() {
        Section section = new Section(new Station(1L, "홍대입구역"), new Station(2L, "성수역"), 10);
        Sections sections = new Sections(section);
        Line line = new Line("2호선", "bg-green-400", sections);

        Long lineId = lineRepository.save(line);

        LineDto resultLine = lineDao.findById(lineId);
        List<SectionDto> resultSections = sectionDao.findByLineId(lineId);
        assertAll(
                () -> assertThat(resultLine.getName()).isEqualTo(line.getName()),
                () -> assertThat(resultLine.getColor()).isEqualTo(line.getColor()),
                () -> assertThat(resultSections.size()).isEqualTo(1)
        );
    }
}