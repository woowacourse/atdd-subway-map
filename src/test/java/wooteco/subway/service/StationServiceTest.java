package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicatedStationException;

class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new FakeStationDao());
    }


    @DisplayName("추가하려는 역의 이름이 이미 존재하면 예외를 발생시킨다.")
    @Test
    void createStation_exception() {
        Station station = stationService.save(new Station("서울역"));

        assertThatThrownBy(() -> stationService.save(new Station("서울역")))
                .isInstanceOf(DuplicatedStationException.class);
    }

    @DisplayName("새로운 역을 추가할 수 있다.")
    @Test
    void createStation_success() {
        Station station = stationService.save(new Station("서울역"));

        assertThat(station.getName()).isEqualTo("서울역");
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    void findAll() {
        stationService.save(new Station("선릉역"));
        stationService.save(new Station("서울역"));

        List<Station> stations = stationService.findAll();

        assertThat(stations).contains(new Station(1L, "선릉역"), new Station(2L, "서울역"));
    }
}