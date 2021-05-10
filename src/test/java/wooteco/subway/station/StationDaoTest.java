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
import wooteco.subway.exception.station.StationNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.fixture.Fixture.makeStation;

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
        Long id = stationDao.insert(makeStation("잠실역"));
        Station station = stationDao.findById(id).orElseThrow(StationNotFoundException::new);

        //then
        assertThat(station.getName()).isEqualTo("잠실역");
    }

    @Test
    @DisplayName("Station 중복된 이름 추가 예외처리 테스트")
    void duplicate_exception() {
        //given
        stationDao.insert(makeStation("잠실역"));

        //when - then
        assertThatThrownBy(() -> stationDao.insert(makeStation("잠실역"))).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Id로 Station을 조회")
    void findById() {
        //given
        final Long id = stationDao.insert(makeStation("잠실역"));

        //when - then
        assertThat(stationDao.findById(id)
                .orElseThrow(StationNotFoundException::new)
                .getId())
                .isEqualTo(id);
    }

    @Test
    @DisplayName("Name으로 Station을 조회")
    void findByName() {
        //given
        final Long id = stationDao.insert(makeStation("잠실역"));

        //when - then
        assertThat(stationDao.findByName("잠실역")
                .orElseThrow(StationNotFoundException::new)
                .getId())
                .isEqualTo(id);
    }

    @Test
    @DisplayName("Station 전체 목록 조회 테스트")
    void findAll() {
        //given
        final Long 잠실역_아이디 = stationDao.insert(makeStation("잠실역"));
        final Long 서울역_아이디 = stationDao.insert(makeStation("서울역"));
        final Long 수서역_아이디 = stationDao.insert(makeStation("수서역"));

        //when
        final List<Long> stationIds = stationDao.findAll().stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        //then
        assertThat(stationIds).containsExactly(잠실역_아이디, 서울역_아이디, 수서역_아이디);
    }

    @Test
    @DisplayName("삭제 요청 시 테스트")
    void delete() {
        //given
        final Long id = stationDao.insert(makeStation("잠실역"));
        final Long expected_id = stationDao.insert(makeStation("서울역"));

        //when
        stationDao.deleteById(id);

        // then
        assertThat(stationDao.findAll().stream()
                .map(Station::getId)
                .collect(Collectors.toList()))
                .containsExactly(expected_id);
    }

    @Test
    @DisplayName("아이디로 역의 이름을 조회")
    void findNameById() {
        //given
        final Long id = stationDao.insert(makeStation("잠실역"));

        //when
        final String name = stationDao.findNameById(id);

        //then
        assertThat(name).isEqualTo("잠실역");
    }

    @Test
    @DisplayName("같은 이름을 가진 역이 존재하지 않을 때 개수 조회")
    void countsBy1() {
        //given - when
        final int cnt = stationDao.countsByName("테스트역");

        //then
        assertThat(cnt).isEqualTo(0);
    }

    @Test
    @DisplayName("같은 이름을 가진 역이 존재할 때 개수 조회")
    void countsBy2() {
        //given
        final Long id = stationDao.insert(makeStation("잠실역"));
        final String name = stationDao.findNameById(id);

        //when
        final int cnt = stationDao.countsByName(name);

        //then
        assertThat(cnt).isEqualTo(1);
    }
}
