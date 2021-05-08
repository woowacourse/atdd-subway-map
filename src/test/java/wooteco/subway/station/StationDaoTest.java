package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
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
        //when
        Station expected = stationDao.insert("A역");

        //then
        assertThat(expected.getName()).isEqualTo("A역");
    }

    @Test
    @DisplayName("Station 중복된 이름 추가 예외처리 테스트")
    void duplicate_exception() {
        //given
        stationDao.insert("A역");

        //when - then
        assertThatThrownBy(() -> stationDao.insert("A역")).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Station 전체 목록 조회 테스트")
    void findAll() {
        //given
        stationDao.insert("A역");
        stationDao.insert("B역");
        stationDao.insert("C역");

        //when
        List<Station> stations = stationDao.findAll();

        //then
        assertThat(stations).hasSize(3);
    }

    @Test
    @DisplayName("삭제 요청 시 테스트")
    void delete() {
        //given
        Station station = stationDao.insert("A역");
        stationDao.insert("B역");

        //when
        stationDao.deleteById(station.getId());

        // then
        assertThat(stationDao.findAll()).hasSize(1);
    }
}
