package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.repository.StationRepository;
import wooteco.subway.repository.dao.FakeStationDao;
import wooteco.subway.repository.exception.DuplicateStationNameException;
import wooteco.subway.service.dto.station.StationResponseDto;

class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        StationRepository stationRepository = new StationRepository(new FakeStationDao());
        this.stationService = new StationService(stationRepository);
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
                .isInstanceOf(DuplicateStationNameException.class)
                .hasMessageContaining("해당 이름의 지하철역은 이미 존재합니다.");
    }

    @DisplayName("지하철 역 목록을 조회한다.")
    @Test
    void findAll() {
        List<String> names = List.of("강남역", "역삼역", "선릉역");
        names.forEach(stationService::create);
        assertThat(stationService.findAll()).hasSize(3);
    }

    @DisplayName("지하철 역을 삭제한다.")
    @Test
    void delete() {
        StationResponseDto station = stationService.create("강남역");
        stationService.remove(station.getId());
        assertThat(stationService.findAll()).isEmpty();
    }
}
