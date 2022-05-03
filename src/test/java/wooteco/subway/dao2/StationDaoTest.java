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
import wooteco.subway.domain.Station;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
@Transactional
@Sql("classpath:dao_test_db.sql")
class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StationDao dao;

    @Test
    void findAll_메서드는_모든_데이터를_조회한다() {
        List<Station> actual = dao.findAll();

        List<Station> expected = List.of(
                new Station(1L, "중복되는 역 이름"),
                new Station(2L, "선릉역"),
                new Station(3L, "잠실역"));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        @Test
        void 중복되지_않는_이름인_경우_성공() {
            Station actual = dao.save(new Station("청계산입구역"));

            Station expected = new Station(4L, "청계산입구역");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 중복되는_이름인_경우_예외발생() {
            assertThatThrownBy(() -> dao.save(new Station("중복되는 역 이름")))
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
                    "SELECT COUNT(*) FROM station WHERE id = 1", Integer.class) > 0;

            assertThat(exists).isFalse();
        }

        @Test
        void 존재하지_않는_역의_id가_입력된_경우_예외발생() {
            assertThatThrownBy(() -> dao.deleteById(99999L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}