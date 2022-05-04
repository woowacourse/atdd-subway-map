package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
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

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        lineDao.save(new Line("신분당선", "bg-red-600"));

        Integer count = jdbcTemplate.queryForObject("select count(*) from LINE", Integer.class);

        assertThat(count).isEqualTo(1);
    }

    @DisplayName("중복된 이름의 지하철 노선이 있다면 true를 반환한다.")
    @Test
    void existLineByName() {
        lineDao.save(new Line("신분당선", "bg-red-600"));

        assertThat(lineDao.existLineByName("신분당선")).isTrue();
    }

    @DisplayName("중복된 색상의 지하철 노선이 있다면 true를 반환한다.")
    @Test
    void existLineByColor() {
        lineDao.save(new Line("신분당선", "bg-red-600"));

        assertThat(lineDao.existLineByColor("bg-red-600")).isTrue();
    }

    @DisplayName("지하철 노선의 전체 목록을 조회한다.")
    @Test
    void findAll() {
        lineDao.save(new Line("신분당선", "bg-red-600"));
        lineDao.save(new Line("다른분당선", "bg-green-600"));

        List<Line> lines = lineDao.findAll();

        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void find() {
        long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));

        Optional<Line> line = lineDao.find(lineId);

        assertThat(line).isNotNull();
    }
}
