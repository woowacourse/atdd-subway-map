package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.StationRequest;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {

    @InjectMocks
    private StationService stationService;
    @Mock
    private StationDao stationDao;
    private StationRequest request;
    private String name;

    @BeforeEach
    void setUp() {
        request = new StationRequest(name);

    }

    // @Test
    // @DisplayName("정상적으로 지하철역을 등록한다.")
    // void createStation() {
    //     String name = "선릉역";
    //     Station station = new Station(name);
    //     given(stationDao.save(station)).willReturn(new Station(1L, name));
    //
    //     StationResponse actual = stationService.createStation(new StationRequest(station.getName()));
    //
    //     assertAll(
    //         () -> assertThat(actual.getId()).isEqualTo(1L),
    //         () -> assertThat(actual.getName()).isEqualTo(request.getName())
    //     );
    // }
}
