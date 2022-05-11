package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;
    private final Line line = new Line("2호선", "green");

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철 노선을 저장한다.")
    void save() {
        // given
        // when
        Long savedId = lineDao.save(line);

        // then
        assertThat(savedId).isPositive();
    }

    @Test
    @DisplayName("이름에 해당하는 지하철 노선이 존재하는지 확인한다.")
    void existByName() {
        // given
        lineDao.save(line);

        // when
        boolean actual = lineDao.existByName("2호선");

        // then
        assertTrue(actual);
    }

    @Test
    @DisplayName("저장된 지하철 노선을 모두 조회한다.")
    void findAll() {
        // given
        lineDao.save(line);
        lineDao.save(new Line("1호선", "blue"));

        // when
        List<Line> lines = lineDao.findAll();

        // then
        assertThat(lines).hasSize(2);
    }

    @Test
    @DisplayName("id에 해당하는 지하철 노선을 조회한다.")
    void findById() {
        // given
        Long savedId = lineDao.save(line);

        // when
        Line actual = lineDao.findById(savedId);

        // then
        Line expected = new Line(savedId, line.getName(), line.getColor());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("id에 해당하는 지하철 노선이 존재하는지 확인한다.")
    void existById() {
        // given
        Long savedId = lineDao.save(line);

        // when
        boolean result = lineDao.existById(savedId);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("지하철 노선 정보를 수정한다.")
    void update() {
        // given
        Long savedId = lineDao.save(line);

        // when
        Line blueLine = new Line(savedId, "1호선", "blue");
        lineDao.update(blueLine);

        // then
        Line actual = lineDao.findById(savedId);
        assertThat(actual).isEqualTo(blueLine);
    }

    @Test
    @DisplayName("id에 해당하는 지하철 노선을 삭제한다.")
    void delete() {
        // given
        Long savedId = lineDao.save(line);

        // when
        lineDao.delete(savedId);

        // then
        boolean result = lineDao.existById(savedId);
        assertFalse(result);
    }
}
