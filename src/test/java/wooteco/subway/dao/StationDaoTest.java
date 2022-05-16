package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Station;

@SuppressWarnings("NonAsciiCharacters")
@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("classpath:schema-test.sql")
class StationDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private StationDao dao;

    @BeforeEach
    void setUp() {
        dao = new StationDao(jdbcTemplate);
    }

    @Test
    void findAll_메서드는_모든_데이터를_조회한다() {
        StationFixtures.setUp(jdbcTemplate, "중복되는 역 이름", "선릉역", "잠실역");
        List<Station> actual = dao.findAll();

        List<Station> expected = List.of(
                new Station(1L, "중복되는 역 이름"),
                new Station(2L, "선릉역"),
                new Station(3L, "잠실역"));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void save_메서드는_데이터를_저장한다() {
        Station actual = dao.save(new Station("청계산입구역"));

        Station expected = new Station(1L, "청계산입구역");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void existById_메서드는_해당_id로_존재_하는지_확인() {
        dao.save(new Station("수서역"));

        assertThat(dao.existById(1L)).isTrue();
    }

    @Test
    void existByName_메서드는_해당_Name으로_존재_하는지_확인() {
        dao.save(new Station("선릉역"));

        assertThat(dao.existByName("선릉역")).isTrue();
    }

    @Test
    void deleteById_메서드는_데이터를_삭제한다() {
        StationFixtures.setUp(jdbcTemplate, "테스트 역");
        dao.deleteById(1L);

        assertThat(dao.existById(1L)).isFalse();
    }

    @Test
    void findById_메서드는_해당_id로_해당_데이터를_조회한다() {
        StationFixtures.setUp(jdbcTemplate, "테스트 역");

        Station expected = new Station("테스트 역");

        assertThat(dao.findById(1L)).isEqualTo(expected);
    }
}
