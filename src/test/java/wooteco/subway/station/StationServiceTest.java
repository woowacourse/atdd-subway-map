package wooteco.subway.station;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.station.StationDaoH2;
import wooteco.subway.domain.Station;
import wooteco.subway.service.StationService;
import wooteco.subway.service.dto.StationServiceDto;

class StationServiceTest {

    private StationDaoH2 mockStationDao;
    private StationService stationService;

    @BeforeEach
    void setUp() {
        mockStationDao = mock(StationDaoH2.class);
        stationService = new StationService(mockStationDao);
    }


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

    @DisplayName("서비스에서 전체 역 호출")
    @Test
    void load() {
        // given
        List<Station> stations = Arrays.asList(
            new Station((long) 1, "성서공단역"),
            new Station((long) 2, "이곡역"),
            new Station((long) 3, "용산역")
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