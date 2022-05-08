package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    @Test
    @DisplayName("역을 생성한다.")
    void create() {
        // given
        given(stationDao.save(any(Station.class))).willReturn(new Station(1L, "선릉역"));

        // when
        StationResponse StationResponse = stationService.create(new StationRequest("선릉역"));

        // then
        assertThat(StationResponse.getId()).isEqualTo(1L);
        assertThat(StationResponse.getName()).isEqualTo("선릉역");
    }

    @Test
    @DisplayName("역 전체를 조회한다.")
    void findAll() {
        // given
        Station Station1 = new Station(1L, "선릉역");
        Station Station2 = new Station(2L, "잠실역");
        given(stationDao.findAll()).willReturn(List.of(Station1, Station2));

        // when
        List<StationResponse> StationResponses = stationService.showAll();

        // then
        assertThat(StationResponses).hasSize(2);
    }

    @Test
    @DisplayName("역을 삭제한다.")
    void delete() {
        // given & when
        stationService.removeById(1L);

        // then
        then(stationDao).should().deleteById(1L);
    }
}
