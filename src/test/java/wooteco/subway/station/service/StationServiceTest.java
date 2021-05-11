package wooteco.subway.station.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.exception.station.StationDuplicatedNameException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.section.dto.response.SectionResponse;
import wooteco.subway.station.Station;
import wooteco.subway.station.dao.JdbcStationDao;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


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
        given(stationDao.save(any(Station.class)))
                .willReturn(new Station(1L, "왕십리"));

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
        given(stationDao.findByName(any(String.class)))
                .willThrow(StationDuplicatedNameException.class);

        // when & then
        assertThatThrownBy(() -> stationService.save(왕십리역))
                .isInstanceOf(StationDuplicatedNameException.class);
        verify(stationDao).findByName(any(String.class));
    }

    @DisplayName("모든 지하철 역 조회")
    @Test
    void findAll() {
        // given
        given(stationDao.findAll())
                .willReturn(Arrays.asList(
                        new Station("왕십리"),
                        new Station("잠실"),
                        new Station("강남")
                ));

        // when
        List<StationResponse> results = stationService.findAll();
        List<Station> stations = results.stream()
                .map(response -> new Station(response.getName()))
                .collect(Collectors.toList());

        // then
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

    @DisplayName("같은 역을 구간으로 등록하려는 경우")
    @Test
    void validatesSameStation() {
        // given
        Station 잠실역 = new Station(1L, "잠실역");
        given(stationDao.findById(잠실역.getId()))
                .willReturn(Optional.of(잠실역));

        // when & then
        assertThatThrownBy(() -> stationService.checkValidStation(잠실역.getId(), 잠실역.getId()))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("존재하지 않는 id로 역을 찾는 경우")
    @Test
    void validatesExistStation() {
        // given
        given(stationDao.findById(any(Long.class)))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stationService.checkValidStation(1L, 2L))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("구간 리스트를 통해 포함된 모든 역 조회")
    @Test
    void findStations() {
        // given
        List<SectionResponse> sections = Arrays.asList(
                new SectionResponse(1L, 2L),
                new SectionResponse(2L, 3L)
        );
        given(stationDao.findById(1L))
                .willReturn(Optional.of(new Station(1L, "강남역")));
        given(stationDao.findById(2L))
                .willReturn(Optional.of(new Station(2L, "잠실역")));
        given(stationDao.findById(3L))
                .willReturn(Optional.of(new Station(3L, "왕십리역")));

        // when
        List<StationResponse> stations = stationService.findStations(sections);

        // then
        assertThat(stations).hasSize(3);
        assertThat(stations).usingRecursiveFieldByFieldElementComparator()
                .containsAll(Arrays.asList(
                        new StationResponse(1L, "강남역"),
                        new StationResponse(2L, "잠실역"),
                        new StationResponse(3L, "왕십리역")
                ));
    }

    @DisplayName("id로 지하철 역 조회")
    @Test
    void findById() {
        // given
        given(stationDao.findById(1L))
                .willReturn(Optional.of(new Station(1L, "강남역")));

        // when
        StationResponse response = stationService.findById(1L);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(new StationResponse(1L, "강남역"));
    }

    @DisplayName("존재하지 않는 id로 지하철 역 조회")
    @Test
    void findByIdWithNotExistId() {
        // given
        given(stationDao.findById(1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stationService.findById(1L))
                .isInstanceOf(StationNotFoundException.class);
    }
}