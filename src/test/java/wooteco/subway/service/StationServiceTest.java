package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.service.dto.station.StationResponseDto;

class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        this.stationService = new StationService(new FakeStationDao());
    }

    @DisplayName("이름으로 지하철 역을 저장한다.")
    @Test
    void create() {
        StationResponseDto station = stationService.create("강남역");
        assertThat(station.getId()).isGreaterThan(0);
        assertThat(station.getName()).isEqualTo("강남역");
    }

    @DisplayName("이미 존재하는 이름으로 지하철 역을 생성할 수 없다.")
    @Test
    void duplicatedNameException() {
        stationService.create("강남역");
        assertThatThrownBy(() -> stationService.create("강남역"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 이름의 지하철 역이 이미 존재합니다.");
    }

    @DisplayName("지하철 역 목록을 조회한다.")
    @Test
    void listStations() {
        List<String> names = List.of("강남역", "역삼역", "선릉역");
        names.forEach(stationService::create);

        List<StationResponseDto> stations = stationService.listStations();
        assertThat(stations).hasSize(3);
    }

    @DisplayName("지하철 역을 삭제한다.")
    @Test
    void delete() {
        StationResponseDto station = stationService.create("강남역");
        stationService.remove(station.getId());

        assertThat(stationService.listStations()).isEmpty();
    }
}
