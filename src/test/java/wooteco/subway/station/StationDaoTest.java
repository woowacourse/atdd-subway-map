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
import wooteco.subway.exception.station.StationNotExistException;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.Fixture.makeStation;

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
        Long id = stationDao.insert(makeStation("A역"));
        Station station = stationDao.findById(id).orElseThrow(StationNotExistException::new);

        //then
        assertThat(station.getName()).isEqualTo("A역");
    }

    @Test
    @DisplayName("Station 중복된 이름 추가 예외처리 테스트")
    void duplicate_exception() {
        //given
        stationDao.insert(makeStation("A역"));

        //when - then
        assertThatThrownBy(() -> stationDao.insert(makeStation("A역"))).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Station 전체 목록 조회 테스트")
    void findAll() {
        //given
        final Long a역_아이디 = stationDao.insert(makeStation("A역"));
        final Long b역_아이디 = stationDao.insert(makeStation("B역"));
        final Long c역_아이디 = stationDao.insert(makeStation("C역"));

        //when
        final List<Long> stationIds = stationDao.findAll().stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        //then
        assertThat(stationIds).containsExactly(a역_아이디, b역_아이디, c역_아이디);
    }

    @Test
    @DisplayName("삭제 요청 시 테스트")
    void delete() {
        //given
        final Long id = stationDao.insert(makeStation("A역"));
        final Long expected_id = stationDao.insert(makeStation("B역"));

        //when
        stationDao.deleteById(id);

        // then
        assertThat(stationDao.findAll().stream()
                .map(Station::getId)
                .collect(Collectors.toList()))
                .containsExactly(expected_id);
    }
}
