package wooteco.subway.reopository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import wooteco.subway.domain.Line;
import wooteco.subway.reopository.dao.JdbcLineDao;

@JdbcTest
@Import(JdbcLineDao.class)
public class JdbcLineDaoTest {

    @Autowired
    private JdbcLineDao jdbcLineDao;

    @Test
    @DisplayName("노선 저장")
    void save() {
        Line line = new Line("1호선", "blue");
        Line savedLine = jdbcLineDao.save(line);
        assertThat(savedLine.getId()).isNotNull();
        assertThat(savedLine.getName()).isEqualTo("1호선");
    }

    @Test
    @DisplayName("지하철 역 이름 중복 여부 조회")
    void duplicateName() {
        Line line = new Line("1호선", "blue");
        jdbcLineDao.save(line);
        assertThat(jdbcLineDao.existByNameAndColor("1호선", "blue")).isTrue();
    }

    @Test
    @DisplayName("id로 노선 조회")
    void findById() {
        Line line = jdbcLineDao.save(new Line("1호선", "blue"));
        Line findLine = jdbcLineDao.findById(line.getId()).get();
        assertThat(findLine.getId()).isNotNull();
        assertThat(findLine.getName()).isEqualTo("1호선");
    }

    @Test
    @DisplayName("노선 전체 조회")
    void findAll() {
        Line line1 = new Line("1호선", "blue");
        Line line2 = new Line("2호선", "red");
        jdbcLineDao.save(line1);
        jdbcLineDao.save(line2);
        List<Line> liens = jdbcLineDao.findAll();
        assertThat(liens).hasSize(2);
    }

    @Test
    @DisplayName("id로 노선 수정")
    void modifyById() {
        Line savedLine = jdbcLineDao.save(new Line("1호선", "blue"));
        jdbcLineDao.modifyById(savedLine.getId(), new Line("2호선", "red"));
        Line updateLine = jdbcLineDao.findById(savedLine.getId()).get();
        assertThat(updateLine.getName()).isEqualTo("2호선");
        assertThat(updateLine.getColor()).isEqualTo("red");
    }

    @Test
    @DisplayName("id로 노선 삭제")
    void deleteById() {
        Line savedLine = jdbcLineDao.save(new Line("1호선", "blue"));
        jdbcLineDao.deleteById(savedLine.getId());
        assertThat(jdbcLineDao.findAll()).hasSize(0);
    }

}
