package wooteco.subway.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private JdbcStationDao jdbcStationDao;

    @Test
    @DisplayName("Station 객체가 생성됨을 확인한다.")
    void create() {
        given(jdbcStationDao.save(any())).willReturn(new Station("미르역"));

        StationRequest request = new StationRequest("미르역");
        StationResponse response = stationService.create(request);

        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @DisplayName("Station 의 목록을 가져온다.")
    @Test
    void findAll() {
        // given
        given(jdbcStationDao.findAll()).willReturn(List.of(new Station("미르역"), new Station("수달역")));
        // when
        List<StationResponse> stationResponses = stationService.showAll();
        // then
        assertThat(stationResponses.size()).isEqualTo(2);
    }

    @DisplayName("Station 을 삭제한다. ")
    @Test
    void delete() {
        stationService.removeById(1L);
        then(jdbcStationDao).should(times(1)).deleteById(1L);
    }
}
