package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Transactional
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class LineDaoTest {

    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;

    public LineDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void set() {
        lineDao = new LineDao(jdbcTemplate.getDataSource());
    }


    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);

        Line line = lineDao.save(lineRequest);
        String actualName = line.getName();
        String actualColor = line.getColor();

        assertThat(actualName).isEqualTo("2호선");
        assertThat(actualColor).isEqualTo("green");
    }

    @Test
    @DisplayName("중복된 노선을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        lineDao.save(lineRequest);

        assertThatThrownBy(() -> lineDao.save(lineRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 노선입니다.");
    }

    @Test
    @DisplayName("모든 노선을 조회한다")
    void findAll() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        lineDao.save(lineRequest);
        LineRequest lineRequest2 = new LineRequest("1호선", "green", 3L, 4L, 10);
        lineDao.save(lineRequest2);

        List<Line> lines = lineDao.findAll();

        assertThat(lines).hasSize(2);
    }

    @Test
    @DisplayName("입력된 id의 노선을 삭제한다")
    void deleteById() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        Line line = lineDao.save(lineRequest);
        lineDao.deleteById(line.getId());

        assertThat(lineDao.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("입력된 id의 노선을 수정한다.")
    void update() {
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        Line line = lineDao.save(lineRequest);
        Line expected = new Line(line.getId(), "분당선", "green");

        lineDao.update(expected);

        assertThat(lineDao.findById(line.getId()).orElseThrow().getName()).isEqualTo("분당선");
    }
}
