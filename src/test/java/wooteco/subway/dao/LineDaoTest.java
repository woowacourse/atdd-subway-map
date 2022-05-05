package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.LineDuplicateException;

@JdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class LineDaoTest {

    @Autowired
    private DataSource dataSource;

    private LineDao lineDao;

    @BeforeEach
    void set() {
        lineDao = new LineDaoImpl(dataSource);
        
        lineDao.save(new Line("2호선", "green"));
    }

    @AfterEach
    void reset() {
        lineDao.deleteAll();
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        String expectedName = "1호선";
        String expectedColor = "blue";

        Line line = lineDao.save(new Line(expectedName, expectedColor));
        String actualName = line.getName();
        String actualColor = line.getColor();

        assertThat(actualName).isEqualTo(expectedName);
        assertThat(actualColor).isEqualTo(expectedColor);
    }

    @Test
    @DisplayName("중복된 노선을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        String name = "2호선";
        String color = "green";

        assertThatThrownBy(() -> lineDao.save(new Line(name, color)))
            .isInstanceOf(LineDuplicateException.class)
            .hasMessage("이미 존재하는 노선입니다.");
    }

    @Test
    @DisplayName("모든 노선을 조회한다")
    void findAll() {
        lineDao.save(new Line("1호선", "blue"));

        List<Line> lines = lineDao.findAll();

        assertThat(lines).hasSize(2);
    }

    @Test
    @DisplayName("입력된 id의 노선을 삭제한다")
    void deleteById() {
        lineDao.delete(new Line(1L, "2호선", "green"));

        assertThat(lineDao.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("입력된 id의 노선을 수정한다.")
    void update() {
        Line expected = new Line(1L, "분당선", "green");

        lineDao.update(expected);

        assertThat(lineDao.findById(1L).orElseThrow()).isEqualTo(expected);
    }
}
