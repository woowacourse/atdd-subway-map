package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class StationServiceTest {

    private StationService stationService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        StationDao stationDao = new StationDao(jdbcTemplate);
        stationService = new StationService(stationDao);
    }

    @DisplayName("station 을 저장한다.")
    @Test
    void save() {
        //given
        String name = "선릉역";
        //when
        StationResponse response = stationService.save(name);
        //then
        Station station = new Station(response.getId(), response.getName());
        assertThat(station).isEqualTo(new Station(1L, name));
    }

    @DisplayName("중복된 name 를 저장한다.")
    @Test
    void duplicatedNameException() {
        //given
        String name = "선릉역";
        //when
        stationService.save(name);
        //then
        assertThatThrownBy(() -> stationService.save(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 Station 이 존재합니다.");
    }

    @DisplayName("stations 를 조회한다.")
    @Test
    void findStations() {
        //given
        stationService.save("선릉역");
        stationService.save("강남역");
        //when
        List<StationResponse> stationResponses = stationService.findStations();
        //then
        List<Station> stations = stationResponses.stream()
                .map(it -> new Station(it.getId(), it.getName()))
                .collect(Collectors.toList());
        assertThat(stations).isEqualTo(List.of(new Station(1L, "선릉역"), new Station(2L, "강남역")));
    }

    @DisplayName("id 를 이용하여 station 을 삭제하고, 삭제 되었는지 확인한다.")
    @Test
    void deleteById() {
        //given
        stationService.save("선릉역");
        stationService.save("강남역");
        //when
        stationService.deleteById(1L);
        //then
        List<StationResponse> stationResponses = stationService.findStations();
        List<Station> stations = stationResponses.stream()
                .map(it -> new Station(it.getId(), it.getName()))
                .collect(Collectors.toList());

        assertThat(stations).isEqualTo(List.of(new Station(2L, "강남역")));
    }
}