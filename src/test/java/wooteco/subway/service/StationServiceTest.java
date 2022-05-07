package wooteco.subway.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.service.dto.station.StationResponseDTO;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@JdbcTest
class StationServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new JdbcStationDao(jdbcTemplate));
    }

    @Test
    @DisplayName("지하철 역을 저장할 수 있다.")
    void saveStation() {
        StationResponseDTO stationResponseDTO = stationService.createStation("강남역");

        Assertions.assertThat(stationResponseDTO.getName()).isEqualTo("강남역");
    }

    @Test
    @DisplayName("중복된 지하철 역을 저장할 수 없다.")
    void NonSaveDuplicateStation() {
        stationService.createStation("역삼역");

        Assertions.assertThatThrownBy(() -> stationService.createStation("역삼역"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("역 삭제 성공")
    void deleteStation() {
        StationResponseDTO stationResponseDTO = stationService.createStation("용문역");
        Long id = stationResponseDTO.getId();

        stationService.delete(id);
        List<Long> ids = stationService.showStations()
                .stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        Assertions.assertThat(ids).doesNotContain(id);
    }

    @Test
    @DisplayName("역 삭제 실패")
    void failDeleteStation() {
        Assertions.assertThatThrownBy(() -> stationService.delete(1919L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
