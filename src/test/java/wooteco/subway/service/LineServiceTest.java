package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.LineDaoImpl;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.SectionDaoImpl;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.StationDaoImpl;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.line.LineFindResponse;
import wooteco.subway.service.dto.line.LineSaveRequest;
import wooteco.subway.service.dto.line.LineSaveResponse;

@JdbcTest
class LineServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;
    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineDao = new LineDaoImpl(jdbcTemplate);
        sectionDao = new SectionDaoImpl(jdbcTemplate);
        stationDao = new StationDaoImpl(jdbcTemplate);
        lineService = new LineService(lineDao, sectionDao, stationDao);

        List<Line> lines = lineDao.findAll();
        List<Long> lineIds = lines.stream()
            .map(Line::getId)
            .collect(Collectors.toList());

        for (Long lineId : lineIds) {
            lineDao.deleteById(lineId);
        }
    }

    @Test
    void save() {
        // given
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("역삼역"));
        LineSaveRequest line = new LineSaveRequest("1호선", "bg-red-600", station1.getId(),
            station2.getId(), 5);

        // when
        LineSaveResponse savedLine = lineService.save(line);
        Line result = lineDao.findById(savedLine.getId()).get();

        // then
        assertThat(line.getName()).isEqualTo(result.getName());
    }

    @Test
    void validateDuplication() {
        // given
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("역삼역"));
        LineSaveRequest line1 = new LineSaveRequest("1호선", "bg-red-600", station1.getId(),
            station2.getId(), 5);
        LineSaveRequest line2 = new LineSaveRequest("1호선", "bg-red-600", station1.getId(),
            station2.getId(), 5);

        // when
        lineService.save(line1);

        // then
        assertThatThrownBy(() -> lineService.save(line2))
            .hasMessage("중복된 이름이 존재합니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAll() {
        // given
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("역삼역"));
        LineSaveRequest line1 = new LineSaveRequest("1호선", "bg-red-600", station1.getId(),
            station2.getId(), 5);
        LineSaveRequest line2 = new LineSaveRequest("2호선", "bg-green-600", station1.getId(),
            station2.getId(), 5);

        // when
        lineService.save(line1);
        lineService.save(line2);

        // then
        List<String> names = lineService.findAll()
            .stream()
            .map(LineFindResponse::getName)
            .collect(Collectors.toList());

        assertThat(names)
            .hasSize(2)
            .contains(line1.getName(), line2.getName());
    }

    @Test
    void delete() {
        // given
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("역삼역"));
        LineSaveRequest line = new LineSaveRequest("1호선", "bg-red-600", station1.getId(),
            station2.getId(), 5);
        LineSaveResponse savedLine = lineService.save(line);

        // when
        lineService.deleteById(savedLine.getId());

        // then
        List<Long> lineIds = lineService.findAll()
            .stream()
            .map(LineFindResponse::getId)
            .collect(Collectors.toList());

        assertThat(lineIds)
            .hasSize(0)
            .doesNotContain(savedLine.getId());
    }

    @Test
    void update() {
        // given
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("역삼역"));
        LineSaveRequest originLine = new LineSaveRequest("1호선", "bg-red-600", station1.getId(),
            station2.getId(), 5);
        LineSaveResponse savedLine = lineService.save(originLine);

        // when
        Line newLineEntity = new Line("2호선", "bg-green-600");
        lineService.updateById(savedLine.getId(), newLineEntity);
        Line lineEntity = lineDao.findById(savedLine.getId()).get();

        // then
        assertThat(lineEntity.getName()).isEqualTo(newLineEntity.getName());
    }
}
