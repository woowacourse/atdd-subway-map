package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {

    private Long savedId;

    private StationDao stationDao;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        stationDao = new StationDao(jdbcTemplate);
        savedId = stationDao.save("서울역");
        stationDao.save("강남역");
    }

    @Test
    @DisplayName("이름을 이용하여 station 을 저장한다.")
    void save() {
        //given
        String name = "선릉역";
        //when
        Long id = stationDao.save(name);
        //then
        List<Station> stations = stationDao.findAll();
        long count = stations.stream()
                .map(station -> station.getId().equals(id))
                .count();
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("이름을 이용하여 station 이 존재하는지 확인한다.")
    void existByName() {
        //given
        String name = "서울역";
        //when
        Boolean exist = stationDao.existByName(name);
        //then
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("모든 station 을 조회한다.")
    void findAll() {
        //given

        //when
        List<Station> stations = stationDao.findAll();
        //then
        assertThat(stations.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("id 를 이용하여 station 을 삭제한다.")
    void deleteById() {
        //given

        //when
        stationDao.deleteById(savedId);
        //then
        List<Station> stations = stationDao.findAll();
        assertThat(stations.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("id 를 이용하여 station 을 조회한다.")
    void findById() {
        //given

        //when
        Station station = stationDao.findById(savedId);
        //then
        assertThat(station.getId()).isEqualTo(savedId);
    }

    @Test
    @DisplayName("존재하지 않는 id 를 이용하여 station 을 조회한다.")
    void findByIdWhenNotFindStation() {
        //given
        Long id = -1L;
        //when

        //then
        assertThatThrownBy(() -> stationDao.findById(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("지하철 역이 존재하지 않습니다.");
    }

}