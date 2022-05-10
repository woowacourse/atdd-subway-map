package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.testutils.Fixture.LINE_1_BLUE;
import static wooteco.subway.testutils.Fixture.LINE_2_GREEN;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequest;

@JdbcTest
public class LineDaoTest {

    @Autowired
    private DataSource dataSource;
    private LineDao lineDao;


    @BeforeEach
    void set() {
        lineDao = new JdbcLineDao(dataSource);
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        final Line actual = lineDao.save(LINE_2_GREEN);

        String actualName = actual.getName();

        assertThat(actualName).isEqualTo(LINE_2_GREEN.getName());

        lineDao.deleteById(actual.getId());
    }

    @Test
    @DisplayName("중복된 노선을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        final Line saved = lineDao.save(LINE_2_GREEN);

        assertThatThrownBy(() -> lineDao.save(LINE_2_GREEN))
            .isInstanceOf(DuplicateKeyException.class);

        lineDao.deleteById(saved.getId());
    }

    @Test
    @DisplayName("모든 노선을 조회한다")
    void findAll() {
        final Line line1 = lineDao.save(LINE_2_GREEN);
        final Line line2 = lineDao.save(LINE_1_BLUE);

        List<Line> lines = lineDao.findAll();

        assertThat(lines).hasSize(2);

        lineDao.deleteById(line1.getId());
        lineDao.deleteById(line2.getId());
    }

    @Test
    @DisplayName("입력된 id의 노선을 삭제한다")
    void deleteById() {
        final Line created = lineDao.save(LINE_2_GREEN);

        lineDao.deleteById(created.getId());

        assertThat(lineDao.findAll()).isEmpty();
    }

    @Test
    @DisplayName("입력된 id의 노선을 수정한다.")
    void update() {
        final Line created = lineDao.save(LINE_2_GREEN);
        final LineRequest lineRequest = new LineRequest("1호선", "green", 1L, 2L, 10);
        final Line updated = lineRequest.toEntity(created.getId());

        lineDao.update(updated);

        final Line updateLine = lineDao.findById(created.getId())
            .orElseThrow();
        assertThat(updateLine.getName()).isEqualTo(updateLine.getName());

        lineDao.deleteById(updateLine.getId());
    }
}
