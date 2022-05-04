package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Line;

@JdbcTest
@Sql("/schema.sql")
class JdbcLineDaoTest {

    private final LineDao jdbcLineDao;

    @Autowired
    public JdbcLineDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcLineDao = new LineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철 노선을 생성, 조회, 삭제한다.")
    void LineCRDTest() {
        Long lineId = jdbcLineDao.save(new Line("신분당선", "red"));
        Line line = jdbcLineDao.findById(lineId);

        assertThat(line)
                .extracting("name", "color")
                .containsExactly("신분당선", "red");

        jdbcLineDao.deleteById(lineId);
        assertThatThrownBy(() -> jdbcLineDao.findById(lineId))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("지하철 노선을 전체 조회한다.")
    void findAll() {
        Long lineId = jdbcLineDao.save(new Line("신분당선", "red"));
        List<Line> lines = jdbcLineDao.findAll();

        assertThat(lines).hasSize(1)
                .extracting("name", "color")
                .containsExactly(tuple("신분당선", "red"));

        jdbcLineDao.deleteById(lineId);
    }

    @Test
    @DisplayName("지하철 노선을 업데이트 한다.")
    void update() {
        Long lineId = jdbcLineDao.save(new Line("신분당선", "red"));

        jdbcLineDao.update(lineId, new Line("분당선", "yellow"));

        Line newLine = jdbcLineDao.findById(lineId);

        assertThat(newLine)
                .extracting("name", "color")
                .containsExactly("분당선", "yellow");

        jdbcLineDao.deleteById(lineId);
    }
}
