package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.line.Line;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class LineDaoTest {

    private LineDao lineDao;
    private long testLineId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
        String schemaQuery = "create table if not exists LINE (id bigint auto_increment not null, name varchar(255) " +
                "not null unique, color varchar(20) not null, primary key(id))";
        jdbcTemplate.execute(schemaQuery);
        testLineId = lineDao.save(new Line("testLine", "white"));
    }

    @DisplayName("노선을 등록한다.")
    @Test
    void save() {
        Line line = new Line("testLine2", "black");

        lineDao.save(line);
        List<Line> lines = lineDao.findAll();

        assertThat(lines).hasSize(2);
    }

    @DisplayName("노선을 ID로 조회한다.")
    @Test
    void findById() {
        Line line = lineDao.findById(testLineId);

        assertThat(line).isEqualTo(new Line(testLineId, "testLine", "white"));
    }

    @DisplayName("노선의 정보를 수정한다.")
    @Test
    void update() {
        lineDao.update(testLineId, "changedName", "grey");

        Line line = lineDao.findById(testLineId);

        assertThat(line.getName()).isEqualTo("changedName");
        assertThat(line.getColor()).isEqualTo("grey");
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void delete() {
        long id = lineDao.save(new Line("dummy", "blue"));
        int beforeLineCounts = lineDao.findAll().size();

        lineDao.deleteById(id);
        int afterLineCounts = lineDao.findAll().size();

        assertThat(beforeLineCounts - 1).isEqualTo(afterLineCounts);
    }
}
