package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.LinkedList;
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
import wooteco.subway.dao.StationDao;
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
    private final StationDao stationDao;

    @Autowired
    public LineRepositoryTest(JdbcTemplate jdbcTemplate, StationDao stationDao) {
        this.lineDao = new JdbcLineDao(jdbcTemplate);
        this.sectionDao = new JdbcSectionDao(jdbcTemplate);
        this.stationDao = stationDao;
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

    @DisplayName("노선 찾기")
    @Test
    void 노선_조회() {
        Station A = new Station(1L, "A");
        Station B = new Station(2L, "B");
        Station C = new Station(3L, "C");
        stationDao.save(A);
        stationDao.save(B);
        stationDao.save(C);
        Section AtoB = new Section(1L, A, B, 1);
        Section BtoC = new Section(2L, B, C, 1);

        Line line = new Line("1호선", "bg-blue-500", new Sections(new LinkedList<>(List.of(AtoB, BtoC))));
        Long lineId = lineRepository.save(line);
        Line result = lineRepository.findById(lineId);

        assertThat(result).isEqualTo(line);
    }
}