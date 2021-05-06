package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Rollback
public class StationDaoTest {
    StationDao stationDao;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Station 추가 테스트")
    void insert() {
        //given
        String name = "봉천역";

        //when
        Station expected = stationDao.insert(name);

        //then
        assertThat(expected.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Station 중복된 이름 추가 예외처리 테스트")
    void duplicate_exception() {
        //given
        String name = "봉천역";
        stationDao.insert(name);

        //when - then
        assertThatThrownBy(() -> stationDao.insert(name)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Station 전체 목록 조회 테스트")
    void findAll() {
        //given
        stationDao.insert("크로플역");
        stationDao.insert("조앤역");
        stationDao.insert("루트역");

        //when
        List<Station> stations = stationDao.findAll();

        //then
        assertThat(stations).hasSize(3);
    }

    @Test
    @DisplayName("삭제 요청 시 테스트")
    void delete() {
        //given
        Station station = stationDao.insert("크로플역");
        stationDao.insert("조앤역");

        //when
        stationDao.deleteById(station.getId());

        // then
        assertThat(stationDao.findAll()).hasSize(1);
    }
}
