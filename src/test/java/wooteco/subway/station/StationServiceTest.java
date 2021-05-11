package wooteco.subway.station;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.station.dao.StationDaoH2;
import wooteco.subway.station.dto.NonIdStationDto;
import wooteco.subway.station.dto.StationDto;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationDaoH2 mockDao;

    @InjectMocks
    private StationService stationService;


    @DisplayName("서비스에서 저장 테스트")
    @Test
    void save() {
        //given
        NonIdStationDto requestStationDto = new NonIdStationDto("스타벅스 선정릉역");
        Station savedStation = new Station(2L, "스타벅스 선정릉역");
        Station station = new Station("스타벅스 선정릉역");

        when(mockDao.save(station)).thenReturn(savedStation);


        //when
        StationDto savedStationDto = stationService.save(requestStationDto);

        //then
        assertThat(savedStationDto.getId()).isNotNull();
        assertThat(savedStationDto.getName()).isEqualTo(savedStation.getName());
    }

    @DisplayName("서비스에서 전체 역 호출")
    @Test
    void load() {
        //given
        List<Station> stations = Arrays.asList(
            new Station(1L, "성서공단역"),
            new Station(2L, "이곡역"),
            new Station(3L, "용산역")
        );

        when(mockDao.showAll()).thenReturn(stations);

        List<StationDto> expectedDtos = Arrays.asList(
            new StationDto(1L, "성서공단역"),
            new StationDto(2L, "이곡역"),
            new StationDto(3L, "용산역")
        );

        //when
        List<StationDto> requestedDtos = stationService.showStations();

        //then
        assertThat(requestedDtos.get(0).getId()).isEqualTo(expectedDtos.get(0).getId());
        assertThat(requestedDtos.get(0).getName()).isEqualTo(expectedDtos.get(0).getName());
        assertThat(requestedDtos.get(1).getId()).isEqualTo(expectedDtos.get(1).getId());
        assertThat(requestedDtos.get(1).getName()).isEqualTo(expectedDtos.get(1).getName());
        assertThat(requestedDtos.get(2).getId()).isEqualTo(expectedDtos.get(2).getId());
        assertThat(requestedDtos.get(2).getName()).isEqualTo(expectedDtos.get(2).getName());
    }


}