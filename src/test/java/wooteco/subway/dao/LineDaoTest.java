package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
public class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;
    private LineDao lineDao;

    private Line line = new Line("신분당선", "red");

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate, dataSource);
    }

    @DisplayName("노선을 등록한다.")
    @Test
    void save() {
        Line actual = lineDao.save(line);

        assertThat(actual).isEqualTo(line);
    }

    @DisplayName("모든 노선 목록을 조회한다.")
    @Test
    void findAll() {
        lineDao.save(line);
        lineDao.save(new Line("1호선", "blue"));

        List<Line> lines = lineDao.findAll();

        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("id에 맞는 노선을 조회한다.")
    @Test
    void findById() {
        Line expected = lineDao.save(line);

        assertThat(lineDao.findById(expected.getId()).get()).isEqualTo(expected);
    }

    @DisplayName("id에 맞는 노선이 없을 경우 빈 Optional을 반환한다.")
    @Test
    void findByIdException() {
        assertThat(lineDao.findById(1L).isEmpty()).isTrue();
    }

    @DisplayName("노선의 이름과 색깔을 수정한다.")
    @Test
    void update() {
        Line saveLine = lineDao.save(line);
        Line expected = new Line(saveLine.getId(), "다른 분당선", "green");

        lineDao.update(expected);
        Line actual = lineDao.findById(saveLine.getId()).get();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void delete() {
        Line saveLine = lineDao.save(line);

        lineDao.delete(saveLine.getId());

        assertThat(lineDao.findById(saveLine.getId()).isEmpty()).isTrue();
    }
}
