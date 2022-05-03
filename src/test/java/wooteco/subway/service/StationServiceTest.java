package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

class StationServiceTest {

    private StationService stationService = new StationService();

    @BeforeEach
    public void setUp() {
        StationDao.clear();
    }

    @DisplayName("지하철역을 저장한다.")
    @Test
    void save() {
        Station station = new Station("hunch");
        stationService.save(station);
        assertThatThrownBy(() -> stationService.save(station))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 이름의 역이 있습니다.");
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void findAll() {
        Station station = new Station("강남역");
        Station station1 = new Station("선릉역");
        stationService.save(station);
        stationService.save(station1);

        assertThat(stationService.findAll())
                .containsOnly(station,station1);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        Station station = new Station("강남역");

        stationService.save(station);
        stationService.delete(1L);

        List<Station> stations = stationService.findAll();

        assertThat(stations).hasSize(0);
    }
}
