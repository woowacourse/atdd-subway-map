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
import wooteco.subway.domain.Station;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
@Sql("classpath:dao_test_db.sql")
class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StationDao dao;

    @Test
    void findAll_메서드는_모든_데이터를_조회() {
        List<Station> actual = dao.findAll();

        List<Station> expected = List.of(
                new Station(1L, "이미 존재하는 역 이름"),
                new Station(2L, "선릉역"),
                new Station(3L, "잠실역"));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("findById 메서드는 id에 해당하는 데이터를 조회한다")
    @Nested
    class FindByIdTest {

        @Test
        void 존재하는_데이터의_id인_경우_해당_데이터가_담긴_Optional_반환() {
            Station actual = dao.findById(1L).get();

            Station expected = new Station(1L, "이미 존재하는 역 이름");

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
            Station actual = dao.findByName("이미 존재하는 역 이름").get();

            Station expected = new Station(1L, "이미 존재하는 역 이름");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 저장되지_않는_name인_경우_비어있는_Optional_반환() {
            boolean dataFound = dao.findByName("존재하지 않는 역 이름").isPresent();

            assertThat(dataFound).isFalse();
        }
    }

    @DisplayName("save 메서드는 데이터를 저장하고 생성된 데이터를 반환한다")
    @Nested
    class SaveTest {

        @Test
        void 중복되지_않는_이름인_경우_저장_성공() {
            Station actual = dao.save(new Station("새로운 지하철역"));

            Station expected = new Station(4L, "새로운 지하철역");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 중복되는_이름을_입력한_경우_예외발생() {
            assertThatThrownBy(() -> dao.save(new Station("이미 존재하는 역 이름")))
                    .isInstanceOf(DataAccessException.class);
        }
    }

    @DisplayName("deleteById 메서드는 데이터를 삭제한다")
    @Nested
    class DeleteByIdTest {

        @Test
        void 존재하는_데이터의_id가_입력된_경우_삭제성공() {
            dao.deleteById(1L);

            boolean exists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM station WHERE id = 1", Integer.class) > 0;

            assertThat(exists).isFalse();
        }

        @Test
        void 존재하지_않는_데이터의_id가_입력되더라도_결과는_동일하므로_예외_미발생() {
            assertThatNoException()
                    .isThrownBy(() -> dao.deleteById(99999L));
        }
    }
}
