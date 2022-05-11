package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.repository.SubwayRepository;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.dao.jdbc.JdbcLineDao;
import wooteco.subway.repository.dao.jdbc.JdbcStationDao;
import wooteco.subway.repository.exception.DuplicateLineColorException;
import wooteco.subway.repository.exception.DuplicateLineNameException;
import wooteco.subway.service.dto.line.LineRequest;
import wooteco.subway.service.dto.line.LineResponse;

@JdbcTest
class LineServiceTest {

    @Autowired
    private DataSource dataSource;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        LineDao lineDao = new JdbcLineDao(dataSource);
        StationDao stationDao = new JdbcStationDao(dataSource);
        LineRepository lineRepository = new SubwayRepository(lineDao, stationDao);
        this.lineService = new LineService(lineRepository);
    }

    @DisplayName("지하철노선을 저장한다.")
    @Test
    void create() {
        LineResponse line = lineService.create("신분당선", "bg-red-600");
        assertThat(line.getId()).isGreaterThan(0);
        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("bg-red-600");
    }

    @DisplayName("이미 존재하는 이름으로 지하철노선을 생성할 수 없다.")
    @Test
    void duplicateNameException() {
        String name = "신분당선";
        lineService.create(name, "bg-red-600");
        assertThatThrownBy(() -> lineService.create(name, "bg-blue-600"))
                .isInstanceOf(DuplicateLineNameException.class)
                .hasMessageContaining("해당 이름의 지하철노선은 이미 존재합니다");
    }

    @DisplayName("이미 존재하는 색상으로 지하철노선을 생성할 수 없다.")
    @Test
    void duplicateColorException() {
        String color = "color";
        lineService.create("신분당선", color);
        assertThatThrownBy(() -> lineService.create("분당선", color))
                .isInstanceOf(DuplicateLineColorException.class)
                .hasMessageContaining("해당 색상의 지하철노선은 이미 존재합니다");
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void findAll() {
        lineService.create("신분당선", "bg-red-600");
        lineService.create("2호선", "bg-red-601");
        lineService.create("분당선", "bg-red-602");
        List<LineResponse> lines = lineService.findAll();
        assertThat(lines).hasSize(3);
    }

    @DisplayName("지하철노선을 조회한다.")
    @Test
    void findOne() {
        LineResponse line = lineService.create("신분당선", "bg-red-600");
        LineResponse foundLine = lineService.findOne(line.getId());
        assertThat(foundLine.getId()).isEqualTo(line.getId());
        assertThat(foundLine.getName()).isEqualTo(line.getName());
        assertThat(foundLine.getColor()).isEqualTo(line.getColor());
    }

    @DisplayName("지하철노선을 수정한다.")
    @Test
    void update() {
        LineResponse line = lineService.create("신분당선", "bg-red-600");
        lineService.update(line.getId(),
                new LineRequest("분당선", "bg-blue-600", 1L, 2L, 10));

        LineResponse updatedLine = lineService.findOne(line.getId());
        assertThat(updatedLine.getId()).isEqualTo(line.getId());
        assertThat(updatedLine.getName()).isEqualTo("분당선");
        assertThat(updatedLine.getColor()).isEqualTo("bg-blue-600");
    }

    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void remove() {
        LineResponse line = lineService.create("신분당선", "bg-red-600");
        lineService.remove(line.getId());
        assertThat(lineService.findAll()).isEmpty();
    }
}
