package wooteco.subway.line.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.exception.notfoundexception.NotFoundLineException;
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
    @DisplayName("노선 추가 테스트")
    void save() {
        Line line = new Line("2호선", "bg-red-600");

        Line savedLine = jdbcLineDao.save(line);

        assertThat(savedLine.getName()).isEqualTo(line.getName());
        assertThat(savedLine.getColor()).isEqualTo(line.getColor());
    }

    @Test
    @DisplayName("노선 조회 테스트")
    void findAll() {
        Line line1 = new Line("2호선", "bg-red-600");
        Line line2 = new Line("3호선", "bg-red-600");

        Line savedLine1 = jdbcLineDao.save(line1);
        Line savedLine2 = jdbcLineDao.save(line2);

        List<Line> lines = jdbcLineDao.findAll();

        assertThat(lines)
                .hasSize(2)
                .containsExactly(savedLine1, savedLine2);
    }

    @Test
    @DisplayName("노선 아이디로 조회 테스트")
    void findById() {
        Line line = new Line("2호선", "bg-red-600");

        Line savedLine = jdbcLineDao.save(line);
        Line findByIdLine = jdbcLineDao.findById(savedLine.getId()).orElseThrow(NotFoundLineException::new);

        assertThat(findByIdLine).isEqualTo(savedLine);
    }

    @Test
    @DisplayName("노선 삭제 테스트")
    void delete() {
        Line line1 = new Line("2호선", "bg-red-600");
        Line line2 = new Line("3호선", "bg-red-600");
        Line savedLine1 = jdbcLineDao.save(line1);
        Line savedLine2 = jdbcLineDao.save(line2);

        jdbcLineDao.delete(savedLine2.getId());

        List<Line> lines = jdbcLineDao.findAll();

        assertThat(lines)
                .hasSize(1)
                .containsExactly(savedLine1);
    }

    @Test
    @DisplayName("노선 정보 수정 테스트")
    void update() {
        Line line1 = new Line("2호선", "bg-red-600");
        Line line2 = new Line("3호선", "bg-red-600");
        jdbcLineDao.save(line1);
        Line savedLine = jdbcLineDao.save(line2);
        Long targetId = savedLine.getId();

        Line updateLine = new Line(targetId, "4호선", "bg-green-600", new ArrayList<>());
        jdbcLineDao.update(updateLine, targetId);

        Line findByIdLine = jdbcLineDao.findById(targetId).orElseThrow(NotFoundLineException::new);

        assertThat(findByIdLine.getName()).isEqualTo(updateLine.getName());
        assertThat(findByIdLine.getColor()).isEqualTo(updateLine.getColor());
    }

    @Test
    @DisplayName("모든 노선 삭제 테스트")
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