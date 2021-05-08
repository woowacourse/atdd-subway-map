package wooteco.subway.station.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.exception.station.StationDuplicatedNameException;
import wooteco.subway.station.Station;
import wooteco.subway.station.dao.JdbcStationDao;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@DisplayName("지하철 역 비즈니스 로직 테스트")
@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    private final StationDao stationDao = Mockito.mock(JdbcStationDao.class);

    @InjectMocks
    private StationService stationService;

    @DisplayName("새로운 지하철 역 생성")
    @Test
    void save() {
        // given
        StationRequest 왕십리역 = new StationRequest("왕십리");
        when(stationDao.save(any(Station.class)))
                .thenReturn(new Station(1L, "왕십리"));

        // when
        StationResponse stationResponse = stationService.save(왕십리역);

        // then
        assertThat(stationResponse).usingRecursiveComparison()
                .isEqualTo(new StationResponse(1L, "왕십리"));
        verify(stationDao).save(any(Station.class));
    }

    @DisplayName("중복된 지하철 역 생성")
    @Test
    void StationDuplicatedNameException() {
        // given
        StationRequest 왕십리역 = new StationRequest("왕십리");
        when(stationDao.findByName(any(String.class)))
                .thenThrow(StationDuplicatedNameException.class);

        // when & then
        assertThatThrownBy(() -> stationService.save(왕십리역))
                .isInstanceOf(StationDuplicatedNameException.class);
        verify(stationDao).findByName(any(String.class));
    }

    @DisplayName("모든 지하철 역 조회")
    @Test
    void findAll() {
        // given
        when(stationDao.findAll())
                .thenReturn(Arrays.asList(
                        new Station("왕십리"),
                        new Station("잠실"),
                        new Station("강남")
                ));

        // when
        List<StationResponse> results = stationService.findAll();

        // then
        List<Station> stations = results.stream()
                .map(response -> new Station(response.getName()))
                .collect(Collectors.toList());

        assertThat(stations).usingRecursiveFieldByFieldElementComparator()
                .containsAll(Arrays.asList(
                        new Station("왕십리"),
                        new Station("잠실"),
                        new Station("강남")
                ));
        verify(stationDao).findAll();
    }

    @DisplayName("Id를 통해 지하철 역 삭제")
    @Test
    void delete() {
        // given
        Long id = 1L;

        // when
        stationService.delete(id);

        // then
        verify(stationDao).delete(any(Long.class));
    }
}