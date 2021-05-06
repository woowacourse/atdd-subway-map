package wooteco.subway.station;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.dao.StationDaoCache;
import wooteco.subway.station.dto.StationDto;

class StationServiceTest {

    @DisplayName("서비스에서 저장 테스트")
    @Test
    void save() {
        // given
        StationDto requestStationDto = new StationDto("스타벅스 선정릉역");
        Station savedStation = new Station((long) 2, "스타벅스 선정릉역");
        Station station = new Station(requestStationDto.getName());

        StationDaoCache mockDao = mock(StationDaoCache.class);
        when(mockDao.save(any())).thenReturn(savedStation);

        StationService stationService = new StationService(mockDao);

        // when
        StationDto savedStationDto = stationService.save(requestStationDto);

        // then
        assertThat(savedStationDto.getId()).isEqualTo(savedStation.getId());
        assertThat(savedStationDto.getName()).isEqualTo(savedStation.getName());
    }

    @DisplayName("서비스에서 전체 역 호출")
    @Test
    void load() {
        // given
        List<Station> stations = Arrays.asList(
            new Station((long) 1, "성서공단역"),
            new Station((long) 2, "이곡역"),
            new Station((long) 3, "용산역")
        );

        StationDaoCache mockDao = mock(StationDaoCache.class);
        when(mockDao.showAll()).thenReturn(stations);
        StationService stationService = new StationService(mockDao);

        List<StationDto> expectedDtos = Arrays.asList(
            new StationDto((long) 1, "성서공단역"),
            new StationDto((long) 2, "이곡역"),
            new StationDto((long) 3, "용산역")
        );

        // when
        List<StationDto> requestedDtos = stationService.showStations();

        // then
        assertThat(requestedDtos.get(0).getId()).isEqualTo(expectedDtos.get(0).getId());
        assertThat(requestedDtos.get(0).getName()).isEqualTo(expectedDtos.get(0).getName());
        assertThat(requestedDtos.get(1).getId()).isEqualTo(expectedDtos.get(1).getId());
        assertThat(requestedDtos.get(1).getName()).isEqualTo(expectedDtos.get(1).getName());
        assertThat(requestedDtos.get(2).getId()).isEqualTo(expectedDtos.get(2).getId());
        assertThat(requestedDtos.get(2).getName()).isEqualTo(expectedDtos.get(2).getName());
    }


}