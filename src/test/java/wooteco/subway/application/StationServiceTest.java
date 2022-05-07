package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static wooteco.subway.application.ServiceFixture.강남역;
import static wooteco.subway.application.ServiceFixture.역삼역;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

class StationServiceTest {

    private final StationService stationService;

    @Mock
    private StationDao stationDao;

    public StationServiceTest() {
        MockitoAnnotations.openMocks(this);
        this.stationService = new StationService(stationDao);
    }

    @Test
    @DisplayName("저장에 성공할 시 역 객체를 반환한다.")
    void save() {
        //when
        given(stationDao.save(any(Station.class))).willReturn(강남역);
        Station station = stationService.createStation("강남역");

        //then
        assertAll(
                () -> assertThat(station.getId()).isEqualTo(1L),
                () -> assertThat(station.getName()).isEqualTo("강남역")
        );
    }

    @Test
    @DisplayName("중복된 이름이 있으면 예외를 반환한다.")
    void duplicatedNameSave() {
        //when
        given(stationDao.existsByName(any(String.class))).willReturn(true);

        //then
        assertThatThrownBy(() -> stationService.createStation("강남역"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("중복");
    }


    @Test
    @DisplayName("역들을 조회한다.")
    void showLines() {
        //when
        given(stationDao.findAll()).willReturn(List.of(강남역, 역삼역));
        List<Station> stations = stationService.showStations();
        //then
        assertThat(stations.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("id로 역을 삭제한다.")
    void deleteLine() {
        //when
        given(stationDao.deleteById(any(Long.class))).willReturn(1);
        int affectedQuery = stationService.deleteStation(1L);
        //then
        assertThat(affectedQuery).isEqualTo(1);
    }
}