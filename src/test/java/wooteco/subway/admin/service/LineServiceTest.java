package wooteco.subway.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.LineStationAddRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private Line line;
    private Map<String, Long> stations = new LinkedHashMap<>();

    private LineService lineService;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "2호선", "bg-green-500", LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        lineService = new LineService(lineRepository, stationRepository);

        Station station1 = new Station(1L, "잠실역");
        Station station2 = new Station(2L, "잠실나루역");
        Station station3 = new Station(3L, "강변역");
        Station station4 = new Station(4L, "구의역");
        stations.put(station1.getName(), station1.getId());
        stations.put(station2.getName(), station2.getId());
        stations.put(station3.getName(), station3.getId());
        stations.put(station4.getName(), station4.getId());

        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(stationRepository.findIdByName(null)).thenReturn(null);
        when(stationRepository.findIdByName("잠실역")).thenReturn(1L);
        when(stationRepository.findIdByName("잠실나루역")).thenReturn(2L);
        when(stationRepository.findIdByName("강변역")).thenReturn(3L);
        when(stationRepository.findIdByName("구의역")).thenReturn(4L);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station1));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(station2));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(station3));
        when(stationRepository.findById(4L)).thenReturn(Optional.of(station4));

        when(lineRepository.save(line)).thenReturn(line);
    }

    @Test
    void addLineStationAtTheFirstOfLine() {
//        LineStationCreateRequest request = new LineStationCreateRequest(null, 4L, 10, 10);
        LineStationAddRequest request = new LineStationAddRequest(null, "구의역", 10, 10);

        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(4L);
        assertThat(line.findLineStationsId().get(1)).isEqualTo(1L);
        assertThat(line.findLineStationsId().get(2)).isEqualTo(2L);
        assertThat(line.findLineStationsId().get(3)).isEqualTo(3L);
    }
//
//    @Test
//    void addLineStationBetweenTwo() {
//        LineStationCreateRequest request = new LineStationCreateRequest(1L, 4L, 10, 10);
//
//        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
//        lineService.addLineStation(line.getId(), request);
//
//        assertThat(line.getStations()).hasSize(4);
//        assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
//        assertThat(line.findLineStationsId().get(1)).isEqualTo(4L);
//        assertThat(line.findLineStationsId().get(2)).isEqualTo(2L);
//        assertThat(line.findLineStationsId().get(3)).isEqualTo(3L);
//    }
//
//    @Test
//    void addLineStationAtTheEndOfLine() {
//        LineStationCreateRequest request = new LineStationCreateRequest(3L, 4L, 10, 10);
//
//        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
//        lineService.addLineStation(line.getId(), request);
//
//        assertThat(line.getStations()).hasSize(4);
//        assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
//        assertThat(line.findLineStationsId().get(1)).isEqualTo(2L);
//        assertThat(line.findLineStationsId().get(2)).isEqualTo(3L);
//        assertThat(line.findLineStationsId().get(3)).isEqualTo(4L);
//    }
//
//    @Test
//    void removeLineStationAtTheFirstOfLine() {
//        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
//        lineService.removeLineStation(line.getId(), 1L);
//
//        assertThat(line.getStations()).hasSize(2);
//        assertThat(line.findLineStationsId().get(0)).isEqualTo(2L);
//        assertThat(line.findLineStationsId().get(1)).isEqualTo(3L);
//    }
//
//    @Test
//    void removeLineStationBetweenTwo() {
//        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
//        lineService.removeLineStation(line.getId(), 2L);
//
//        assertThat(line.getStations()).hasSize(2);
//        assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
//        assertThat(line.findLineStationsId().get(1)).isEqualTo(3L);
//    }
//
//    @Test
//    void removeLineStationAtTheEndOfLine() {
//        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
//        lineService.removeLineStation(line.getId(), 3L);
//
//        assertThat(line.getStations()).hasSize(2);
//        assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
//        assertThat(line.findLineStationsId().get(1)).isEqualTo(2L);
//    }
//
//    @Test
//    void findLineWithStationsById() {
//        Set<Station> stations = Sets.newLinkedHashSet(new Station("강남역"), new Station("역삼역"), new Station("삼성역"));
//        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
//        when(stationRepository.findAllById(anyList())).thenReturn(stations);
//
//        LineResponse lineResponse = lineService.findLineWithStationsById(1L);
//
//        assertThat(lineResponse.getStations()).hasSize(3);
//    }
}