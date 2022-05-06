package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wooteco.subway.domain.Line;

@JdbcTest
public class LineDaoImplTest {

    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SqlGroup({
            @Sql(
                    scripts = {"/schema.sql"},
                    config = @SqlConfig(
                            dataSource = "dataSource",
                            transactionManager = "transactionManager"
                    ),
                    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
            ),
            @Sql(
                    scripts = {"/delete.sql"},
                    config = @SqlConfig(
                            dataSource = "dataSource",
                            transactionManager = "transactionManager"
                    ),
                    executionPhase = ExecutionPhase.AFTER_TEST_METHOD
            ),
    })

    @BeforeEach
    void setUp() {
        lineDao = new LineDaoImpl(jdbcTemplate);
    }

    @DisplayName("노선정보를 저장한다.")
    @Test
    void save() {
        Line line = new Line("분당선", "green");
        Line newLine = lineDao.save(line);

        assertThat(newLine.getName()).isEqualTo("분당선");
        assertThat(newLine.getColor()).isEqualTo("green");
    }

    @DisplayName("노선정보들을 가져온다.")
    @Test
    void findAll() {
        List<Line> lines = lineDao.findAll();

        assertThat(lines.size()).isEqualTo(3);
    }

    @DisplayName("노선 정보를 삭제한다.")
    @Test
    void delete() {
        Line line = new Line("4호선", "blue");
        Line newLine = lineDao.save(line);

        assertThat(lineDao.delete(newLine.getId())).isOne();
    }

    @DisplayName("노선 정보를 조회한다.")
    @Test
    void find() {
        Line line = new Line("5호선", "blue");
        Line newLine = lineDao.save(line);

        assertThat(lineDao.find(newLine.getId()).getName()).isEqualTo("5호선");
        assertThat(lineDao.find(newLine.getId()).getColor()).isEqualTo("blue");
    }

    @DisplayName("노선 정보를 변경한다.")
    @Test
    void update() {
        Line line = new Line("7호선", "blue");
        Line newLine = lineDao.save(line);

        assertThat(lineDao.update(newLine.getId(), line)).isOne();
    }
}
