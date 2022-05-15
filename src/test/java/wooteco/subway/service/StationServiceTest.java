package wooteco.subway.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.jdbc.JdbcStationDao;
import wooteco.subway.service.dto.station.StationResponseDto;

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
        //given
        //when
        StationResponseDto stationResponseDto = stationService.createStation("낙성대");
        //then
        Assertions.assertThat(stationResponseDto.getName()).isEqualTo("낙성대");
    }

    @Test
    @DisplayName("중복된 지하철 역을 저장할 수 없다.")
    void NonSaveDuplicateStation() {
        //given
        //when
        stationService.createStation("낙성대");
        //then
        Assertions.assertThatThrownBy(() -> stationService.createStation("낙성대"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("역 삭제 성공")
    void deleteStation() {
        //given
        Long id = stationService.createStation("낙성대").getId();
        //when
        stationService.delete(id);
        //then
        List<Long> ids = stationService.findStations()
                .stream()
                .map(StationResponseDto::getId)
                .collect(Collectors.toList());
        Assertions.assertThat(ids).doesNotContain(id);
    }

    @Test
    @DisplayName("역 삭제 실패")
    void failDeleteStation() {
        //given
        //when
        //then
        Assertions.assertThatThrownBy(() -> stationService.delete(-1L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
