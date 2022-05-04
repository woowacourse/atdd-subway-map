package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(lineDao.find("신분당선").getName())
                .isEqualTo("신분당선");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findAll() {
        Line line = new Line("신분당선", "red");
        Line line1 = new Line("분당선", "green");

        lineDao.save(line);
        lineDao.save(line1);

        assertThat(lineDao.findAll())
                .hasSize(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        Line line = new Line("신분당선", "red");
        Line line2 = new Line("분당선", "green");

        lineDao.save(line);
        lineDao.update(lineDao.find("신분당선").getId(), line2);

        assertThatThrownBy(() -> lineDao.find("신분당선"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 아이디의 노선이 없습니다.");

        assertThat(lineDao.find("분당선").getName())
                .isEqualTo("분당선");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        Line line = new Line("신분당선", "red");

        lineDao.save(line);
        lineDao.delete(1L);

        assertThat(lineDao.findAll()).hasSize(0);
    }
}
