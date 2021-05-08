package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.StationDao;

@ExtendWith(MockitoExtension.class)
@DisplayName("역 서비스 레이어 테스트")
public class StationServiceTest {

    @Mock
    private StationDao stationDao;
    @InjectMocks
    private StationService stationService;

    @Test
    @DisplayName("새로운 역을 생성한다.")
    void createStation() {
        Station station = new Station(1L, "잠실역");
        given(stationDao.save(any())).willReturn(1L);
        given(stationDao.findById(1L)).willReturn(Optional.of(station));

        Station station2 = stationService.createStation("잠실역");
        assertThat(station2).isEqualTo(station);
        assertThat(station2.getId()).isEqualTo(station.getId());
    }

    @Test
    @DisplayName("생성된 역들을 불러온다.")
    void findAll() {
        Station station1 = new Station(1L, "잠실역");
        Station station2 = new Station(2L, "역삼역");

        given(stationDao.findAll()).willReturn(Arrays.asList(
            station1, station2)
        );
        assertThat(stationService.findAll())
            .contains(station1)
            .contains(station2);
    }

    @Test
    @DisplayName("생성된 역을 삭제한다.")
    void delete() {
        stationService.deleteById(1L);
        verify(stationDao, times(1))
            .deleteById(1L);
    }
}
