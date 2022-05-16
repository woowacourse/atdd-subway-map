package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Line;

@SuppressWarnings("NonAsciiCharacters")
@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("classpath:schema-test.sql")
class LineDaoTest {

    private LineDao dao;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        dao = new LineDao(jdbcTemplate);
    }

    @Test
    void findAll_메서드는_모든_데이터를_조회한다() {
        LineFixtures.setUp(jdbcTemplate, new Line("분당선", "노란색"), new Line("신분당선", "빨간색"),
            new Line("2호선", "초록색"));
        List<Line> actual = dao.findAll();

        List<Line> expected = List.of(
            new Line(1L, "분당선", "노란색"),
            new Line(2L, "신분당선", "빨간색"),
            new Line(3L, "2호선", "초록색")
        );

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findById는_단건의_데이터를_조회한다() {
        LineFixtures.setUp(jdbcTemplate, new Line("분당선", "노란색"));
        Line actual = dao.findById(1L);
        Line excepted = new Line(1L, "분당선", "노란색");

        assertThat(actual).isEqualTo(excepted);
    }

    @Test
    void save_메서드는_데이터를_저장한다() {
        Line actual = dao.save(new Line("8호선", "분홍색"));

        Line expected = new Line(1L, "8호선", "분홍색");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void update_메서드는_데이터를_수정한다() {
        LineFixtures.setUp(jdbcTemplate, new Line("분당선", "노란색"));
        dao.update(new Line(1L, "8호선", "노란색"));

        String actual = jdbcTemplate.queryForObject("SELECT name FROM line WHERE id = 1",
            new EmptySqlParameterSource(), String.class);
        String expected = "8호선";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void existById_메서드는_해당_id로_존재_하는지_확인() {
        dao.save(new Line("3호선", "주황색"));

        assertThat(dao.existById(1L)).isTrue();
    }

    @Test
    void existById_메서드는_해당_Name으로_존재_하는지_확인() {
        dao.save(new Line("3호선", "주황색"));

        assertThat(dao.existByName("3호선")).isTrue();
    }

    @Test
    void delete_메서드는_데이터를_삭제한다() {
        LineFixtures.setUp(jdbcTemplate, new Line("분당선", "노란색"));
        dao.deleteById(1L);

        assertThat(dao.existById(1L)).isFalse();
    }
}
