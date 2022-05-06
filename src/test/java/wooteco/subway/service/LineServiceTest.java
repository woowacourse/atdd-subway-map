package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@JdbcTest
class LineServiceTest {

    private LineService lineService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        LineDao lineDao = new LineDao(jdbcTemplate);
        lineService = new LineService(lineDao);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        Line line = new Line("신분당선", "red");
        lineService.save(line);

        assertThatThrownBy(() -> lineService.save(line))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 해당 이름의 노선이 있습니다.");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findAll() {
        Line line = new Line("신분당선", "red");
        Line line2 = new Line("분당선", "green");
        lineService.save(line);
        lineService.save(line2);

        assertThat(lineService.findAll())
            .hasSize(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        Line line = new Line("신분당선", "red");
        Line line2 = new Line("분당선", "green");

        Line newLine = lineService.save(line);
        lineService.update(newLine.getId(), line2);

        List<Line> lines = lineService.findAll();

        assertThat(lines.get(0).getName())
            .isEqualTo("분당선");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        Line line = new Line("신분당선", "red");

        Line newLine = lineService.save(line);
        lineService.delete(newLine.getId());

        List<Line> lines = lineService.findAll();

        assertThat(lines).hasSize(0);
    }
}
