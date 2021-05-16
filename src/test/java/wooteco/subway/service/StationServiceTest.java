package wooteco.subway.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.StationServiceDto;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationDao mockStationDao;
    @InjectMocks
    private StationService stationService;

    @DisplayName("서비스에서 저장 테스트")
    @Test
    void save() {
        // given
        long id = 1;
        String name = "스타벅스 선정릉역";

        when(mockStationDao.save(any(Station.class))).thenReturn(new Station(id, name));

        // when
        StationServiceDto stationServiceDto = stationService.save(new StationServiceDto(name));

        // then
        assertThat(stationServiceDto.getId()).isEqualTo(id);
        assertThat(stationServiceDto.getName()).isEqualTo(name);
    }

    @DisplayName("서비스에서 전체 역 DTO 호출")
    @Test
    void showAllDtos() {
        // given
        List<Station> stations = Arrays.asList(
            new Station(1L, "성서공단역"),
            new Station(2L, "이곡역"),
            new Station(3L, "용산역")
        );

        when(mockStationDao.showAll()).thenReturn(stations);

        // when
        List<StationServiceDto> requestedDtos = stationService.showStations();

        // then
        assertThat(requestedDtos.get(0).getId()).isEqualTo(stations.get(0).getId());
        assertThat(requestedDtos.get(0).getName()).isEqualTo(stations.get(0).getName());
        assertThat(requestedDtos.get(1).getId()).isEqualTo(stations.get(1).getId());
        assertThat(requestedDtos.get(1).getName()).isEqualTo(stations.get(1).getName());
        assertThat(requestedDtos.get(2).getId()).isEqualTo(stations.get(2).getId());
        assertThat(requestedDtos.get(2).getName()).isEqualTo(stations.get(2).getName());
    }
}