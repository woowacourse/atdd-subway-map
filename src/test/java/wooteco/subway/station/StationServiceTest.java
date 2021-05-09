package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.DuplicateStationNameException;
import wooteco.subway.exception.NotExistStationException;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

@DisplayName("Station Service")
@Sql("classpath:tableInit.sql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
public class StationServiceTest {

    private final StationService stationService;

    public StationServiceTest(StationService stationService) {
        this.stationService = stationService;
    }

    @DisplayName("createStation 메서드는 역을 생성하고 생성한 역을 반환한다.")
    @Test
    void createLine() {
        Station station = new Station("쌍문역");
        Station result = stationService.createStation(station);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(station.getName());
    }

    @DisplayName("createStation 메서드는 중복된 역을 생성할 경우 예외를 던진다.")
    @Test
    void createLineException() {
        Station station = new Station("쌍문역");
        stationService.createStation(station);

        assertThatThrownBy(() -> {
            stationService.createStation(station);
        }).isInstanceOf(DuplicateStationNameException.class);
    }

    @DisplayName("showStations 메서드는 역 리스트를 반환한다.")
    @Test
    void showStations() {
        Station station1 = new Station("쌍문역");
        Station station2 = new Station("방학역");
        Station station3 = new Station("도봉역");

        stationService.createStation(station1);
        stationService.createStation(station2);
        stationService.createStation(station3);
        List<Station> stations = stationService.showStations();

        assertThat(stations).containsExactly(station1, station2, station3);
    }

    @DisplayName("showStations 메서드는 저장된 역이 없을 경우, 예외를 던진다.")
    @Test
    void showStationsException() {
        assertThatThrownBy(stationService::showStations)
            .isInstanceOf(NotExistStationException.class);
    }

    @DisplayName("showStations 메서드는 삭제하려는 역이 있다면, 역을 삭제한다.")
    @Test
    void delete() {
        Station station = new Station("쌍문역");

        stationService.createStation(station);
        stationService.deleteStation(1L);
        assertThatThrownBy(stationService::showStations)
            .isInstanceOf(NotExistStationException.class);
    }

    @DisplayName("showStations 메서드는 삭제하려는 역이 없을경우, 예외를 던진다.")
    @Test
    void deleteException() {
        assertThatThrownBy(() -> {
            stationService.deleteStation(1L);
        }).isInstanceOf(NotExistStationException.class);
    }

}
