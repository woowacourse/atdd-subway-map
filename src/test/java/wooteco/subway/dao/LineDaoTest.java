package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Line;

@JdbcTest
class LineDaoTest {

    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @DisplayName("지하철 노선을 저장하고 찾는다.")
    @Test
    void saveAndFind() {
        Line line = new Line("신분당선", "red");
        lineDao.save(line);
        assertThat(lineDao.findByName("신분당선").isPresent()).isTrue();
    }

    @DisplayName("지하철 노선을 id로 조회한다.")
    @Test
    void findById() {
        Line line = new Line("신분당선", "red");
        Line savedLine = lineDao.save(line);

        assertThat(lineDao.findById(savedLine.getId()).isPresent()).isTrue();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findAll() {
        Line line = new Line("신분당선", "red");
        Line line1 = new Line("분당선", "green");

        lineDao.save(line);
        lineDao.save(line1);

        assertThat(lineDao.findAll()).hasSize(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        Line line = new Line("신분당선", "red");
        Line line2 = new Line("분당선", "green");

        Line savedLine = lineDao.save(line);
        lineDao.update(savedLine.getId(), line2);

        assertThat(lineDao.findByName("신분당선").isEmpty()).isTrue();
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        Line line = new Line("신분당선", "red");

        Line savedLine = lineDao.save(line);
        lineDao.delete(savedLine.getId());

        assertThat(lineDao.findAll()).hasSize(0);
    }
}
