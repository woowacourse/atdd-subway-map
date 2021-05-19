package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.exception.DataNotFoundException;


@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    private Station stationA;
    private Station stationB;

    @BeforeEach
    void setUp() {
        stationA = new Station(1L, "역A");
        stationB = new Station(2L, "역B");
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        final StationRequest stationRequest = new StationRequest("역A");
        given(stationDao.save(stationRequest.toEntity())).willReturn(stationA);

        // when
        final StationResponse createdStation = stationService.createStation(stationRequest);

        // then
        then(stationDao).should(times(1)).save(any(Station.class));
        assertThat(createdStation.getId()).isEqualTo(stationA.getId());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void findStations() {
        // given
        final List<Station> stations = Arrays.asList(stationA, stationB);
        given(stationDao.findAll()).willReturn(stations);

        // when
        final List<StationResponse> stationResponses = stationService.findStations();

        // then
        then(stationDao).should(times(1)).findAll();
        assertThat(stationResponses).hasSize(stations.size());
    }

    @DisplayName("존재하지 않는 id가 있어서 지하철역을 조회에 실패한다.")
    @Test
    void findByIds_fail_notExistentId() {
        // given
        final List<Long> ids = Arrays.asList(1L, 3L);
        given(stationDao.findByIds(ids)).willReturn(Collections.singletonList(stationA));

        // when, then
        assertThatThrownBy(() -> stationService.findByIds(ids))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessage("존재하지 않는 ID의 지하철역이 있습니다.");

        then(stationDao).should(times(1)).findByIds(anyList());
    }

    @DisplayName("여러 id의 지하철역을 조회한다.")
    @Test
    void findByIds() {
        // given
        final List<Long> ids = Arrays.asList(1L, 2L);
        given(stationDao.findByIds(ids)).willReturn(Arrays.asList(stationA, stationB));

        // when
        final List<Station> stations = stationService.findByIds(ids)
            .toStream()
            .collect(Collectors.toList());

        // then
        then(stationDao).should(times(1)).findByIds(anyList());

        assertThat(stations).extracting("id")
            .containsExactlyInAnyOrderElementsOf(ids);
    }
}
