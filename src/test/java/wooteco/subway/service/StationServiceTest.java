package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new StationDao());
    }

    @Test
    @DisplayName("station 을 저장한다.")
    void save() {
        //given
        Station station = new Station("lala");

        //when
        Station actual = stationService.save(station);

        //then
        assertThat(actual.getName()).isEqualTo(station.getName());
    }
}
