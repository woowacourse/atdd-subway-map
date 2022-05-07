package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;

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
    void findAll_메서드는_모든_데이터를_조회() {
        List<Line> actual = dao.findAll();

        List<Line> expected = List.of(
                new Line(1L, "이미 존재하는 노선 이름", "노란색"),
                new Line(2L, "신분당선", "빨간색"),
                new Line(3L, "2호선", "초록색")
        );

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("findById 메서드는 특정 id의 데이터를 조회한다.")
    @Nested
    class FindByIdTest {

        @Test
        void 존재하는_데이터의_id인_경우_해당_데이터가_담긴_Optional_반환() {
            Line actual = dao.findById(1L).get();

            Line expected = new Line(1L, "이미 존재하는 노선 이름", "노란색");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 존재하지_않는_데이터의_id인_경우_비어있는_Optional_반환() {
            boolean dataFound = dao.findById(9999L).isPresent();

            assertThat(dataFound).isFalse();
        }
    }


    @DisplayName("findByName 메서드는 name에 해당하는 데이터를 조회한다")
    @Nested
    class FindByNameTest {

        @Test
        void 저장된_name인_경우_해당_데이터가_담긴_Optional_반환() {
            Line actual = dao.findByName("이미 존재하는 노선 이름").get();

            Line expected = new Line(1L, "이미 존재하는 노선 이름", "노란색");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 저장되지_않는_name인_경우_비어있는_Optional_반환() {
            boolean dataFound = dao.findByName("존재하지 않는 역 이름").isPresent();

            assertThat(dataFound).isFalse();
        }
    }

    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        @Test
        void 중복되지_않는_이름인_경우_성공() {
            Line actual = dao.save(new Line("새로운 노선", "분홍색"));

            Line expected = new Line(4L, "새로운 노선", "분홍색");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 중복되는_이름인_경우_예외발생() {
            assertThatThrownBy(() -> dao.save(new Line("이미 존재하는 노선 이름", "노란색")))
                    .isInstanceOf(DataAccessException.class);
        }
    }

    @DisplayName("update 메서드는 데이터를 수정한다")
    @Nested
    class UpdateTest {

        @Test
        void 중복되지_않는_이름으로_수정_가능() {
            dao.update(new Line(1L, "새로운 노선 이름", "노란색"));

            String actual = jdbcTemplate.queryForObject("SELECT name FROM line WHERE id = 1", String.class);
            String expected = "새로운 노선 이름";

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 색상은_자유롭게_수정_가능() {
            dao.update(new Line(1L, "이미 존재하는 노선 이름", "새로운 색상"));

            String actual = jdbcTemplate.queryForObject("SELECT color FROM line WHERE id = 1", String.class);
            String expected = "새로운 색상";

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 중복되는_이름으로_수정하려는_경우_예외발생() {
            assertThatThrownBy(() -> dao.update(new Line(2L, "이미 존재하는 노선 이름", "노란색")))
                    .isInstanceOf(DataAccessException.class);
        }

        @Test
        void 존재하지_않는_노선을_수정하려는_경우_예외_미발생() {
            assertThatNoException()
                    .isThrownBy(() -> dao.update(new Line(999999999L, "새로운 노선 이름", "노란색")));
        }
    }

    @DisplayName("deleteById 메서드는 특정 데이터를 삭제한다")
    @Nested
    class DeleteByIdTest {

        @Test
        void 존재하는_데이터의_id가_입력된_경우_삭제성공() {
            dao.deleteById(1L);

            boolean exists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM line WHERE id = 1", Integer.class) > 0;

            assertThat(exists).isFalse();
        }

        @Test
        void 존재하지_않는_데이터의_id가_입력되더라도_결과는_동일하므로_예외_미발생() {
            assertThatNoException()
                    .isThrownBy(() -> dao.deleteById(99999L));
        }
    }
}
