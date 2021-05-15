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
import wooteco.subway.station.dto.StationRequest;
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
        // given, when
        Station stationA = stationService.createStation(new StationRequest("A"));

        // then
        assertThat(stationA.getId()).isEqualTo(1L);
        assertThat(stationA.getName()).isEqualTo("A");
    }

    @DisplayName("createStation 메서드는 중복된 역을 생성할 경우 예외를 던진다.")
    @Test
    void createLineException() {
        // given
        stationService.createStation(new StationRequest("A"));

        // when, then
        assertThatThrownBy(() -> {
            stationService.createStation(new StationRequest("A"));
        }).isInstanceOf(DuplicateStationNameException.class);
    }

    @DisplayName("showStations 메서드는 역 리스트를 반환한다.")
    @Test
    void showStations() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));
        Station stationC = stationService.createStation(new StationRequest("C"));

        // when
        List<Station> stations = stationService.findStations();

        // then
        assertThat(stations).containsExactly(stationA, stationB, stationC);
    }

    @DisplayName("showStations 메서드는 저장된 역이 없을 경우, 예외를 던진다.")
    @Test
    void showStationsException() {
        assertThatThrownBy(stationService::findStations)
            .isInstanceOf(NotExistStationException.class);
    }

    @DisplayName("showStations 메서드는 삭제하려는 역이 있다면, 역을 삭제한다.")
    @Test
    void delete() {
        // given
        stationService.createStation(new StationRequest("A"));

        // when
        stationService.deleteStation(1L);

        // then
        assertThatThrownBy(stationService::findStations)
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
