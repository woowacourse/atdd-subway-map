package wooteco.subway.service;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.station.StationResponse;

@JdbcTest
class StationServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new StationDao(jdbcTemplate));
    }

    @Test
    @DisplayName("지하철 역을 저장할 수 있다.")
    void saveStation() {
        var stationResponse = stationService.createStation("강남역");

        Assertions.assertThat(stationResponse.getName()).isEqualTo("강남역");
    }

    @Test
    @DisplayName("중복된 지하철 역을 저장할 수 없다.")
    void NonSaveDuplicateStation() {
        stationService.createStation("역삼역");

        Assertions.assertThatThrownBy(() -> stationService.createStation("역삼역"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("기존에 존재하는 역 삭제 성공")
    void deleteStation() {
        var stationResponse = stationService.createStation("용문역");
        var id = stationResponse.getId();

        stationService.deleteStation(id);
        var ids = stationService.findAll()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        Assertions.assertThat(ids).doesNotContain(id);
    }

    @Test
    @DisplayName("역 삭제 실패")
    void failDeleteStation() {
        Assertions.assertThatThrownBy(() -> stationService.deleteStation(1919L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
