package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;

import wooteco.subway.domain.Station;

@JdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class StationDaoTest {

    private JdbcTemplate jdbcTemplate;
    private StationDao stationDao;

    public StationDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void set(){
        stationDao = new StationDao(jdbcTemplate.getDataSource());
    }

    @Test
    @DisplayName("지하철역을 저장한다.")
    void save() {
        String expected = "강남역";

        Station station = stationDao.save(expected);
        String actual = station.getName();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("중복된 역을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        stationDao.save("선릉역");

        String expected = "선릉역";

        assertThatThrownBy(() -> stationDao.save(expected))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 지하철역 이름입니다.");
    }

    @Test
    @DisplayName("모든 지하철 역을 조회한다")
    void findAll() {
        stationDao.save("선릉역");
        stationDao.save("잠실역");

        List<Station> stations = stationDao.findAll();

        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("입력된 id의 지하철 역을 삭제한다")
    void deleteById() {
        Station station1 = stationDao.save("선릉역");

        stationDao.deleteById(station1.getId());

        assertThat(stationDao.findAll()).hasSize(0);
    }
}
