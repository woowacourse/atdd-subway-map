package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.repository.SubwayRepository;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.dao.jdbc.JdbcLineDao;
import wooteco.subway.repository.dao.jdbc.JdbcSectionDao;
import wooteco.subway.repository.dao.jdbc.JdbcStationDao;
import wooteco.subway.repository.exception.line.DuplicateLineColorException;
import wooteco.subway.repository.exception.line.DuplicateLineNameException;
import wooteco.subway.service.dto.line.LineRequest;
import wooteco.subway.service.dto.line.LineResponse;
import wooteco.subway.service.dto.line.LineUpdateRequest;

@JdbcTest
class LineServiceTest {

    @Autowired
    private DataSource dataSource;
    private LineService lineService;
    private Long stationId1;
    private Long stationId2;

    @BeforeEach
    void setUp() {
        LineDao lineDao = new JdbcLineDao(dataSource);
        SectionDao sectionDao = new JdbcSectionDao(dataSource);
        StationDao stationDao = new JdbcStationDao(dataSource);

        LineRepository lineRepository = new SubwayRepository(lineDao, sectionDao, stationDao);
        this.lineService = new LineService(lineRepository);

        StationService stationService = new StationService(new SubwayRepository(lineDao, sectionDao, stationDao));
        this.stationId1 = stationService.create("강남역").getId();
        this.stationId2 = stationService.create("광교역").getId();
    }

    @DisplayName("지하철노선을 저장한다.")
    @Test
    void create() {
        LineRequest lineRequest = new LineRequest("신분당선", "color", stationId1, stationId2, 10);
        LineResponse lineResponse = lineService.create(lineRequest);
        assertAll(
                () -> assertThat(lineResponse.getId()).isGreaterThan(0),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("color")
        );
    }

    @DisplayName("이미 존재하는 이름으로 지하철노선을 생성할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"신분당선"})
    void duplicateNameException(String name) {
        LineRequest lineRequest1 = new LineRequest(name, "color1", stationId1, stationId2, 10);
        LineRequest lineRequest2 = new LineRequest(name, "color2", stationId1, stationId2, 10);

        lineService.create(lineRequest1);
        assertThatThrownBy(() -> lineService.create(lineRequest2))
                .isInstanceOf(DuplicateLineNameException.class)
                .hasMessageContaining("해당 이름의 지하철노선은 이미 존재합니다");
    }

    @DisplayName("이미 존재하는 색상으로 지하철노선을 생성할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"color"})
    void duplicateColorException(String color) {
        LineRequest lineRequest1 = new LineRequest("신분당선", color, stationId1, stationId2, 10);
        LineRequest lineRequest2 = new LineRequest("분당선", color, stationId1, stationId2, 10);

        lineService.create(lineRequest1);
        assertThatThrownBy(() -> lineService.create(lineRequest2))
                .isInstanceOf(DuplicateLineColorException.class)
                .hasMessageContaining("해당 색상의 지하철노선은 이미 존재합니다");
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void findAll() {
        lineService.create(new LineRequest("1호선", "color1", stationId1, stationId2, 10));
        lineService.create(new LineRequest("2호선", "color2", stationId1, stationId2, 10));
        lineService.create(new LineRequest("3호선", "color3", stationId1, stationId2, 10));
        List<LineResponse> lines = lineService.findAll();
        assertThat(lines).hasSize(3);
    }

    @DisplayName("지하철노선을 조회한다.")
    @Test
    void findOne() {
        LineResponse expected = lineService.create(new LineRequest("1호선", "color1", stationId1, stationId2, 10));
        LineResponse actual = lineService.findOne(expected.getId());
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(actual);
    }

    @DisplayName("지하철노선을 수정한다.")
    @Test
    void update() {
        LineResponse line = lineService.create(new LineRequest("1호선", "color1", stationId1, stationId2, 10));
        lineService.update(line.getId(), new LineUpdateRequest("분당선", "color2"));

        LineResponse updatedLine = lineService.findOne(line.getId());
        assertAll(
                () -> assertThat(updatedLine.getId()).isEqualTo(line.getId()),
                () -> assertThat(updatedLine.getName()).isEqualTo("분당선"),
                () -> assertThat(updatedLine.getColor()).isEqualTo("color2")
        );
    }

    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void remove() {
        LineResponse line = lineService.create(new LineRequest("1호선", "color1", stationId1, stationId2, 10));
        lineService.remove(line.getId());
        assertThat(lineService.findAll()).isEmpty();
    }
}
