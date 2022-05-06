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
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.LineDuplicateException;

@JdbcTest
public class LineDaoTest {

    private static final Line LINE_1호선_BLUE = new Line("1호선", "blue");
    private static final Line LINE_2호선_GREEN = new Line("2호선", "green");

    @Autowired
    private DataSource dataSource;
    private LineDao lineDao;


    @BeforeEach
    void set() {
        lineDao = new JdbcLineDao(dataSource);
    }

    @AfterEach
    void reset() {
        for (final Line line : lineDao.findAll()) {
            lineDao.delete(line);
        }
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        final Line actual = lineDao.save(LINE_2호선_GREEN);

        String actualName = actual.getName();

        assertThat(actualName).isEqualTo(LINE_2호선_GREEN.getName());
    }

    @Test
    @DisplayName("중복된 노선을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        lineDao.save(LINE_2호선_GREEN);

        assertThatThrownBy(() -> lineDao.save(LINE_2호선_GREEN))
            .isInstanceOf(LineDuplicateException.class)
            .hasMessage("이미 존재하는 노선입니다.");
    }

    @Test
    @DisplayName("모든 노선을 조회한다")
    void findAll() {
        lineDao.save(LINE_2호선_GREEN);
        lineDao.save(LINE_1호선_BLUE);

        List<Line> lines = lineDao.findAll();

        assertThat(lines).hasSize(2);
    }

    @Test
    @DisplayName("입력된 id의 노선을 삭제한다")
    void deleteById() {
        final Line created = lineDao.save(LINE_2호선_GREEN);

        lineDao.delete(created);

        assertThat(lineDao.findAll()).isEmpty();
    }

    @Test
    @DisplayName("입력된 id의 노선을 수정한다.")
    void update() {
        final Line created = lineDao.save(LINE_2호선_GREEN);
        final LineRequest lineRequest = new LineRequest("1호선", "green");
        final Line updated = lineRequest.toEntity(created.getId());

        lineDao.update(updated);

        assertThat(lineDao.findById(created.getId()).orElseThrow().getName()).isEqualTo(updated.getName());
    }
}
