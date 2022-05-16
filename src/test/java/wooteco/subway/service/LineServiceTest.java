package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.line.JdbcLineDao;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dao.section.JdbcSectionDao;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.JdbcStationDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.DataNotExistException;
import wooteco.subway.exception.SubwayException;

@JdbcTest
class LineServiceTest {

    private static final LineRequest LINE_REQUEST =
            new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

    private LineService lineService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        LineDao lineDao = new JdbcLineDao(jdbcTemplate);
        SectionDao sectionDao = new JdbcSectionDao(jdbcTemplate);
        StationDao stationDao = new JdbcStationDao(jdbcTemplate);
        StationService stationService = new StationService(stationDao);
        SectionService sectionService = new SectionService(sectionDao, stationService);
        lineService = new LineService(lineDao, sectionService);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        lineService.save(LINE_REQUEST);

        assertThat(lineService.findAll()).hasSize(1);
    }

    @DisplayName("중복된 이름의 지하철 노선을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedName() {
        lineService.save(LINE_REQUEST);

        LineRequest sameNameLine = new LineRequest("신분당선", "bg-green-600", 1L, 2L, 10);
        assertThatThrownBy(() -> lineService.save(sameNameLine))
                .isInstanceOf(SubwayException.class)
                .hasMessage("지하철 노선 이름이 중복됩니다.");
    }

    @DisplayName("중복된 색상의 지하철 노선을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedColor() {
        lineService.save(LINE_REQUEST);

        LineRequest sameColorLine = new LineRequest("다른분당선", "bg-red-600", 1L, 2L, 10);
        assertThatThrownBy(() -> lineService.save(sameColorLine))
                .isInstanceOf(SubwayException.class)
                .hasMessage("지하철 노선 색상이 중복됩니다.");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findById() {
        long lineId = lineService.save(LINE_REQUEST);

        assertThatCode(() -> lineService.findById(lineId))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회할 경우 예외를 발생시킨다.")
    @Test
    void findByIdNotExistLine() {
        assertThatThrownBy(() -> lineService.findById(1L))
                .isInstanceOf(DataNotExistException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        long lineId = lineService.save(LINE_REQUEST);

        assertThatCode(() -> lineService.update(new Line(lineId, "다른분당선", "bg-green-600")))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정할 경우 예외를 발생시킨다.")
    @Test
    void updateNotExistLine() {
        assertThatThrownBy(() -> lineService.update(new Line(1L, "다른분당선", "bg-green-600")))
                .isInstanceOf(DataNotExistException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }

    @DisplayName("중복된 이름으로 지하철 노선을 수정할 경우 예외를 발생시킨다.")
    @Test
    void updateDuplicatedName() {
        long lineId = lineService.save(LINE_REQUEST);
        lineService.save(new LineRequest("다른분당선", "bg-green-600", 1L, 2L, 10));

        assertThatThrownBy(() -> lineService.update(new Line(lineId, "다른분당선", "bg-green-600")))
                .isInstanceOf(SubwayException.class)
                .hasMessage("지하철 노선 이름이 중복됩니다.");
    }

    @DisplayName("중복된 색상으로 지하철 노선을 수정할 경우 예외를 발생시킨다.")
    @Test
    void updateDuplicatedColor() {
        long lineId = lineService.save(LINE_REQUEST);
        lineService.save(new LineRequest("다른분당선", "bg-green-600", 1L, 2L, 10));

        assertThatThrownBy(() -> lineService.update(new Line(lineId, "신분당선", "bg-green-600")))
                .isInstanceOf(SubwayException.class)
                .hasMessage("지하철 노선 색상이 중복됩니다.");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        long lineId = lineService.save(LINE_REQUEST);

        assertThatCode(() -> lineService.delete(lineId))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철 노선을 삭제할 경우 예외를 발생시킨다.")
    @Test
    void deleteNotExistLine() {
        assertThatThrownBy(() -> lineService.delete(1L))
                .isInstanceOf(DataNotExistException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }
}
