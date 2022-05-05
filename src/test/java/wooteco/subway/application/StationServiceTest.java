package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    @Test
    @DisplayName("중복되지 않은 역 이름을 기입하면 Station 객체 생성")
    void createStation() {
        //given
        final String name = "석촌고분역";
        final Station station = new Station(1L, name);
        given(stationDao.existsByName(name)).willReturn(false);
        given(stationDao.save(new Station(name))).willReturn(station);
        //when
        final Station newStation = stationService.createStation(name);
        //then
        assertThat(newStation.getId()).isEqualTo(station.getId());
    }

    @Test
    @DisplayName("중복된 역 이름으로 생성 요청하면 예외 발생")
    void createExistsStationName() {
        //given
        given(stationDao.existsByName("석촌고분역")).willReturn(true);
        //then
        assertThatThrownBy(() -> stationService.createStation("석촌고분역"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void showStations() {
        //given
        List<Station> stations = List.of(new Station("석촌고분역"), new Station("삼전역"), new Station("석촌역"));
        given(stationDao.findAll()).willReturn(stations);
        //when
        final List<Station> foundStations = stationService.showStations();
        //then
        assertThat(foundStations.size()).isEqualTo(3);
    }
}
