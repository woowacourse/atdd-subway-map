package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.notRemovableException.NotRemovableStationException;
import wooteco.subway.repository.SectionDao;
import wooteco.subway.repository.StationDao;

@ExtendWith(MockitoExtension.class)
@DisplayName("역 서비스 레이어 테스트")
public class StationServiceTest {

    @Mock
    private StationDao stationDao;
    @Mock
    private SectionDao sectionDao;
    @InjectMocks
    private StationService stationService;

    @Test
    @DisplayName("새로운 역을 생성한다.")
    void createStation() {
        // given
        Station station = new Station(1L, "잠실역");
        StationRequest stationRequest = new StationRequest("잠실역");
        StationResponse stationResponse = StationResponse.of(station);
        given(stationDao.save(any())).willReturn(1L);

        // when
        StationResponse savedStationResponse = stationService.createStation(stationRequest);

        // then
        assertThat(savedStationResponse.getId()).isEqualTo(stationResponse.getId());
    }

    @Test
    @DisplayName("생성된 역들을 불러온다.")
    void findAll() {
        // given
        Station station1 = new Station(1L, "잠실역");
        Station station2 = new Station(2L, "역삼역");
        StationResponse stationResponse1 = StationResponse.of(station1);
        StationResponse stationResponse2 = StationResponse.of(station2);

        given(stationDao.findAll()).willReturn(Arrays.asList(
            station1, station2)
        );

        // when
        List<StationResponse> stationResponses = stationService.findAll();

        // then
        assertThat(stationResponses.get(0).getId()).isEqualTo(stationResponse1.getId());
        assertThat(stationResponses.get(0).getName()).isEqualTo(stationResponse1.getName());
        assertThat(stationResponses.get(1).getId()).isEqualTo(stationResponse2.getId());
        assertThat(stationResponses.get(1).getName()).isEqualTo(stationResponse2.getName());
    }

    @Test
    @DisplayName("생성된 역을 삭제한다.")
    void delete() {
        // given
        given(sectionDao.findByStation(1L)).willReturn(0);

        // when
        stationService.deleteById(1L);

        // then
        verify(stationDao, times(1))
            .deleteById(1L);
    }

    @Test
    @DisplayName("생성된 역을 삭제한다. - 등록된 구간이 있어 실패")
    void deleteFail() {
        // given
        given(sectionDao.findByStation(1L)).willReturn(2);

        // when
        assertThatThrownBy(() ->
            stationService.deleteById(1L)
        ).isInstanceOf(NotRemovableStationException.class);
    }
}
