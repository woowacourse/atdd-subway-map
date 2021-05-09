package wooteco.subway.line.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.line.domain.Line;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JDBCLineDaoTest {

    private final JDBCLineDao jdbcLineDao;

    public JDBCLineDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcLineDao = new JDBCLineDao(jdbcTemplate);
    }

    @Test
    void save() {
        Line line = new Line("2호선", "bg-red-600");

        Line savedLine = jdbcLineDao.save(line);

        assertThat(savedLine.getName()).isEqualTo(line.getName());
        assertThat(savedLine.getColor()).isEqualTo(line.getColor());
    }

    @Test
    void findAll() {
        Line line1 = new Line("2호선", "bg-red-600");
        Line line2 = new Line("3호선", "bg-red-600");

        Line savedLine1 = jdbcLineDao.save(line1);
        Line savedLine2 = jdbcLineDao.save(line2);

        List<Line> lines = jdbcLineDao.findAll();

        assertThat(lines).hasSize(2);
        assertThat(lines).containsExactly(savedLine1, savedLine2);
    }

    @Test
    void findById() {
        Line line = new Line("2호선", "bg-red-600");

        Line savedLine = jdbcLineDao.save(line);
        Line findByIdLine = jdbcLineDao.findById(savedLine.getId());

        assertThat(findByIdLine).isEqualTo(savedLine);
    }

    @Test
    void delete() {
        Line line1 = new Line("2호선", "bg-red-600");
        Line line2 = new Line("3호선", "bg-red-600");
        Line savedLine1 = jdbcLineDao.save(line1);
        Line savedLine2 = jdbcLineDao.save(line2);

        jdbcLineDao.delete(savedLine2.getId());

        List<Line> lines = jdbcLineDao.findAll();

        assertThat(lines).hasSize(1);
        assertThat(lines).containsExactly(savedLine1);
    }

    @Test
    void update() {
        Line line1 = new Line("2호선", "bg-red-600");
        Line line2 = new Line("3호선", "bg-red-600");
        jdbcLineDao.save(line1);
        Line savedLine = jdbcLineDao.save(line2);
        Long targetId = savedLine.getId();

        Line updateLine = new Line(targetId, "4호선", "bg-green-600", new ArrayList<>());
        jdbcLineDao.update(updateLine, targetId);

        Line findByIdLine = jdbcLineDao.findById(targetId);

        assertThat(findByIdLine.getName()).isEqualTo(updateLine.getName());
        assertThat(findByIdLine.getColor()).isEqualTo(updateLine.getColor());
    }

    @Test
    void deleteAll() {
        Line line1 = new Line("2호선", "bg-red-600");
        Line line2 = new Line("3호선", "bg-red-600");
        jdbcLineDao.save(line1);
        jdbcLineDao.save(line2);

        jdbcLineDao.deleteAll();

        List<Line> lines = jdbcLineDao.findAll();

        assertThat(lines).hasSize(0);
    }
}