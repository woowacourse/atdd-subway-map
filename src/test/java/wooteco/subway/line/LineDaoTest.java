package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@JdbcTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        this.lineDao = new LineDao(jdbcTemplate);
    }

    @DisplayName("노선 추가 성공 테스트")
    @Test
    void successSaveTest() {
        assertDoesNotThrow(() ->
            lineDao.save(new Line("신분당선", "black"))
        );
    }

    @DisplayName("노선 추가 실패 테스트")
    @Test
    void failSaveTest() {
        lineDao.save(new Line("신분당선", "black"));

        assertThatThrownBy(() -> {
            lineDao.save(new Line("신분당선", "red"));
        }).isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("line 비어있는 리스트 전체 조회 테스트")
    @Test
    void findAllTestWhenEmpty() {
        List<Line> lines = lineDao.findAll();

        assertThat(lines.isEmpty()).isTrue();
    }

    @DisplayName("line 전체 조회 테스트")
    @Test
    void findAllLineTest() {
        lineDao.save(new Line("신분당선", "black"));
        lineDao.save(new Line("2호선", "white"));
        List<Line> lines = lineDao.findAll();
        assertThat(lines.size()).isEqualTo(2);
        assertThat(lines.get(0).getId()).isEqualTo(1L);
        assertThat(lines.get(1).getId()).isEqualTo(2L);
    }

}
