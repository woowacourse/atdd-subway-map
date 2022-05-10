package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;

@JdbcTest
class LineServiceTest {

    private LineService lineService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        LineDao lineDao = new LineDao(jdbcTemplate);
        SectionDao sectionDao = new SectionDao(jdbcTemplate);
        lineService = new LineService(lineDao, sectionDao);
    }

    @DisplayName("지하철 노선을 중복 생성한다.")
    @Test
    void save() {
        Line line = new Line("신분당선", "red", 1L, 2L, 10);
        lineService.save(line);

        assertThatThrownBy(() -> lineService.save(line))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 해당 이름의 노선이 있습니다.");
    }

    @DisplayName("모든 지하철 노선들을 조회한다.")
    @Test
    void findAll() {
        Line line = new Line("신분당선", "red", 1L, 2L, 10);
        Line line2 = new Line("분당선", "green", 1L, 2L, 10);
        lineService.save(line);
        lineService.save(line2);

        assertThat(lineService.findAll()).hasSize(2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void find() {
        Line line = new Line("신분당선", "red", 1L, 2L, 10);
        Line savedLine = lineService.save(line);

        assertThat(lineService.findById(savedLine.getId()).getName()).isEqualTo("신분당선");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        Line line = new Line("신분당선", "red", 1L, 2L, 10);
        Line line2 = new Line("분당선", "green", 1L, 2L, 10);

        Line newLine = lineService.save(line);
        lineService.update(newLine.getId(), line2);

        List<Line> lines = lineService.findAll();

        assertThat(lines.get(0).getName()).isEqualTo("분당선");
    }

    @DisplayName("없는 지하철 노선을 수정한다.")
    @Test
    void updateNotExistLine() {
        Line line = new Line("신분당선", "red", 1L, 2L, 10);

        assertThatThrownBy(() -> lineService.update(0L, line))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("해당 아이디의 노선이 없습니다.");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        Line line = new Line("신분당선", "red", 1L, 2L, 10);

        Line newLine = lineService.save(line);
        lineService.delete(newLine.getId());

        List<Line> lines = lineService.findAll();

        assertThat(lines).hasSize(0);
    }

    @DisplayName("없는 지하철 노선을 삭제한다.")
    @Test
    void deleteNotExistLine() {
        assertThatThrownBy(() -> lineService.delete(0L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("해당 아이디의 노선이 없습니다.");
    }
}
