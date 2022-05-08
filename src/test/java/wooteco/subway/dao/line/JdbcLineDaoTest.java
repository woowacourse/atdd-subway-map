package wooteco.subway.dao.line;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
class JdbcLineDaoTest {

    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new JdbcLineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Line을 등록할 수 있다.")
    void save() {
        Line line = new Line("신분당선", "bg-red-600");

        assertThat(lineDao.save(line)).isNotNull();
    }

    @Test
    @DisplayName("Line을 id로 조회할 수 있다.")
    void findById() {
        long id = lineDao.save(new Line("신분당선", "bg-red-600"));
        Line findLine = lineDao.findById(id);

        assertThat(findLine.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Line 전체 조회할 수 있다.")
    void findAll() {
        lineDao.save(new Line("신분당선", "bg-red-600"));
        lineDao.save(new Line("분당선", "bg-green-600"));

        assertThat(lineDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Line 이름이 존재하는지 확인할 수 있다.")
    void existByName() {
        lineDao.save(new Line("신분당선", "bg-red-600"));

        assertThat(lineDao.existByName("신분당선")).isTrue();
    }

    @Test
    @DisplayName("id에 해당하는 Line이 존재하는지 확인할 수 있다.")
    void existById() {
        long id = lineDao.save(new Line("신분당선", "bg-red-600"));

        assertThat(lineDao.existById(id)).isNotNull();
    }

    @Test
    @DisplayName("Line을 수정할 수 있다.")
    void update() {
        long id = lineDao.save(new Line("신분당선", "bg-red-600"));
        int result = lineDao.update(new Line(id, "분당선", "bg-red-600"));

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("Line을 삭제할 수 있다.")
    void delete() {
        long id = lineDao.save(new Line("신분당선", "bg-red-600"));
        int result = lineDao.delete(id);

        assertThat(result).isEqualTo(1);
    }
}
