package wooteco.subway.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.LineStationAddRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private Line line;
    private List<Station> stations;

    @InjectMocks
    private LineService lineService;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "2호선", "bg-green-500", LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        lineService = new LineService(lineRepository, stationRepository);

        stations = Arrays.asList(new Station(1L, "잠실역"),
                new Station(2L, "잠실나루역"),
                new Station(3L, "강변역"),
                new Station(4L, "구의역"));

        lenient().when(lineRepository.save(line)).thenReturn(line);
        lenient().when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        lenient().when(stationRepository.findIdByName(null)).thenReturn(null);
        for (Station station : stations) {
            lenient().when(stationRepository.findIdByName(station.getName())).thenReturn(station.getId());
            lenient().when(stationRepository.findById(station.getId())).thenReturn(Optional.of(station));
        }

        line.addLineStation(new LineStation(null, stations.get(0).getId(), 10, 10));
        line.addLineStation(new LineStation(stations.get(0).getId(), stations.get(1).getId(), 10, 10));
        line.addLineStation(new LineStation(stations.get(1).getId(), stations.get(2).getId(), 10, 10));
    }

    @Test
    void addLineStationAtTheFirstOfLine() {
        Station newFirstStation = stations.get(3);
        LineStationAddRequest request = new LineStationAddRequest(null, newFirstStation.getName(), 10, 10);

        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(newFirstStation.getId());
        assertThat(line.findLineStationsId().get(1)).isEqualTo(stations.get(0).getId());
        assertThat(line.findLineStationsId().get(2)).isEqualTo(stations.get(1).getId());
        ;
        assertThat(line.findLineStationsId().get(3)).isEqualTo(stations.get(2).getId());
    }

    @Test
    void addLineStationBetweenTwo() {
        Station originStation = stations.get(0);
        Station newAddStation = stations.get(3);
        LineStationAddRequest request = new LineStationAddRequest(originStation.getName(), newAddStation.getName(), 10, 10);

        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(originStation.getId());
        assertThat(line.findLineStationsId().get(1)).isEqualTo(newAddStation.getId());
        assertThat(line.findLineStationsId().get(2)).isEqualTo(stations.get(1).getId());
        assertThat(line.findLineStationsId().get(3)).isEqualTo(stations.get(2).getId());
    }

    @Test
    void addLineStationAtTheEndOfLine() {
        Station originLastStation = stations.get(2);
        Station newLastStation = stations.get(3);
        LineStationAddRequest request = new LineStationAddRequest(originLastStation.getName(), newLastStation.getName(), 10, 10);

        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(stations.get(0).getId());
        assertThat(line.findLineStationsId().get(1)).isEqualTo(stations.get(1).getId());
        assertThat(line.findLineStationsId().get(2)).isEqualTo(originLastStation.getId());
        assertThat(line.findLineStationsId().get(3)).isEqualTo(newLastStation.getId());
    }

    @Test
    void removeLineStationAtTheFirstOfLine() {
        Station firstStation = stations.get(0);
        lineService.removeLineStation(line.getId(), firstStation.getId());

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(stations.get(1).getId());
        assertThat(line.findLineStationsId().get(1)).isEqualTo(stations.get(2).getId());
    }

    @Test
    void removeLineStationBetweenTwo() {
        Station secondStation = stations.get(1);
        lineService.removeLineStation(line.getId(), secondStation.getId());

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(stations.get(0).getId());
        assertThat(line.findLineStationsId().get(1)).isEqualTo(stations.get(2).getId());
    }

    @Test
    void removeLineStationAtTheEndOfLine() {
        Station lastStation = stations.get(2);
        lineService.removeLineStation(line.getId(), lastStation.getId());

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(stations.get(0).getId());
        assertThat(line.findLineStationsId().get(1)).isEqualTo(stations.get(1).getId());
    }

    @Test
    void findLineWithStationsById() {
        List<Station> stations = Arrays.asList(new Station("강남역"), new Station("역삼역"), new Station("삼성역"));
        lenient().when(stationRepository.findAllById(anyList())).thenReturn(stations);

        List<Station> stationsAtLine = lineService.findStationsAtLine(line);
        assertThat(stationsAtLine).hasSize(3);
    }
}