package wooteco.subway.dao2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
@Transactional
@Sql("classpath:dao_test_db.sql")
class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LineDao dao;

    @Test
    void findAll_메서드는_모든_데이터를_조회한다() {
        List<Line> actual = dao.findAll();

        List<Line> expected = List.of(
                new Line(1L, "분당선", "노란색"),
                new Line(2L, "신분당선", "빨간색"),
                new Line(3L, "2호선", "초록색")
        );

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("findById 메서드는 단건의 데이터를 조회한다.")
    @Nested
    class FindByIdTest {

        @Test
        void 존재하는_노선의_id가_입력된_경우_성공() {
            Line actual = dao.findById(1L);
            Line excepted = new Line(1L, "분당선", "노란색");

            assertThat(actual).isEqualTo(excepted);
        }

        @Test
        void 존재하지_않는_역의_id가_입력된_경우_예외발생() {
            assertThatThrownBy(() -> dao.findById(99999L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        @Test
        void 중복되지_않는_이름인_경우_성공() {
            Line actual = dao.save(new Line("8호선", "분홍색"));

            Line expected = new Line(4L, "8호선", "분홍색");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 중복되는_이름인_경우_예외발생() {
            assertThatThrownBy(() -> dao.save(new Line("분당선", "노란색")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("update 메서드는 데이터를 수정한다")
    @Nested
    class UpdateTest {

        @Test
        void 유효한_입력값인_경우_성공() {
            dao.update(new Line(1L,"8호선", "노란색"));

            String actual = jdbcTemplate.queryForObject("SELECT name FROM line WHERE id = 1", String.class);
            String expected = "8호선";

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 중복되는_이름으로_수정하려는_경우_예외발생() {
            assertThatThrownBy(() -> dao.update(new Line(1L,"2호선", "노란색")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 존재하지_않는_노선을_수정하려는_경우_예외발생() {
            assertThatThrownBy(() -> dao.update(new Line(999999999L,"10호선", "노란색")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("deleteById 메서드는 데이터를 삭제한다")
    @Nested
    class DeleteByIdTest {

        @Test
        void 존재하는_역의_id가_입력된_경우_성공() {
            dao.deleteById(1L);

            boolean exists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM line WHERE id = 1", Integer.class) > 0;

            assertThat(exists).isFalse();
        }

        @Test
        void 존재하지_않는_역의_id가_입력된_경우_예외발생() {
            assertThatThrownBy(() -> dao.deleteById(99999L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}