package wooteco.subway.admin.service;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private LineService lineService;

    private Line line;
    private Line line2;


    @BeforeEach
    void setUp() {
        line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-orange-700");
        line2 = new Line(2L, "1호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-orange-700");
        lineService = new LineService(lineRepository, stationRepository);

        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));
    }

    @Test
    @DisplayName("null->역 이 아닌 역->역 이 먼저 등록되는 경우 테스트")
    void addLineStationWhenStationsEmpty() {
        when(lineRepository.findById(line2.getId())).thenReturn(Optional.of(line2));
        when(lineRepository.save(line2)).thenReturn(line2);

        LineStationCreateRequest request = new LineStationCreateRequest(1L, 4L, 10, 10);
        lineService.addLineStation(line2.getId(), request);

        assertThat(line2.getStations()).hasSize(2);
        assertThat(line2.findLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line2.findLineStationsId().get(1)).isEqualTo(4L);
    }

    @Test
    @DisplayName("null->시작역 이 있는데, 새역->시작역 을 등록하려는 경우 테스트")
    void addLineStationAtFirstOfLineWhenPreStationIdIsNotNull() {
        LineStationCreateRequest request = new LineStationCreateRequest(4L, 1L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(lineRepository.save(line)).thenReturn(line);
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(4L);
        assertThat(line.findLineStationsId().get(1)).isEqualTo(1L);
        assertThat(line.findLineStationsId().get(2)).isEqualTo(2L);
        assertThat(line.findLineStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationAtTheFirstOfLine() {
        LineStationCreateRequest request = new LineStationCreateRequest(null, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(lineRepository.save(line)).thenReturn(line);
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(4L);
        assertThat(line.findLineStationsId().get(1)).isEqualTo(1L);
        assertThat(line.findLineStationsId().get(2)).isEqualTo(2L);
        assertThat(line.findLineStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    @DisplayName("station(대상역) 에 새로운 역을 넣는 경우")
    void addLineStationBetweenTwoWhenStationNew() {
        LineStationCreateRequest request = new LineStationCreateRequest(1L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(lineRepository.save(line)).thenReturn(line);
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.findLineStationsId().get(1)).isEqualTo(4L);
        assertThat(line.findLineStationsId().get(2)).isEqualTo(2L);
        assertThat(line.findLineStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    @DisplayName("preStation(이전역) 에 새로운 역을 넣는 경우")
    void addLineStationBetweenTwoWhenPreStationNew() {
        LineStationCreateRequest request = new LineStationCreateRequest(4L, 2L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(lineRepository.save(line)).thenReturn(line);
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.findLineStationsId().get(1)).isEqualTo(4L);
        assertThat(line.findLineStationsId().get(2)).isEqualTo(2L);
        assertThat(line.findLineStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationAtTheEndOfLine() {
        LineStationCreateRequest request = new LineStationCreateRequest(3L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(lineRepository.save(line)).thenReturn(line);
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.findLineStationsId().get(1)).isEqualTo(2L);
        assertThat(line.findLineStationsId().get(2)).isEqualTo(3L);
        assertThat(line.findLineStationsId().get(3)).isEqualTo(4L);
    }

    @Test
    @DisplayName("역이 등록되어 있는 경우 도착역, 출발역이 연결되어 있지 않을 때 등록 실패 테스트")
    void addLineStationFail() {
        LineStationCreateRequest request = new LineStationCreateRequest(5L, 7L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(lineRepository.save(line)).thenReturn(line);
        lineService.addLineStation(line.getId(), request);
        assertThat(line.getStations()).hasSize(3);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.findLineStationsId().get(1)).isEqualTo(2L);
        assertThat(line.findLineStationsId().get(2)).isEqualTo(3L);

    }

    @Test
    void removeLineStationAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 1L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(2L);
        assertThat(line.findLineStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 2L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.findLineStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 3L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.findLineStationsId().get(1)).isEqualTo(2L);
    }

    @Test
    void findLineWithStationsById() {
        Set<Station> stations = Sets.newLinkedHashSet(new Station("강남역"), new Station("역삼역"), new Station("삼성역"));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findAllById(anyList())).thenReturn(stations);

        LineResponse lineResponse = lineService.findLineWithStationsById(1L);

        assertThat(lineResponse.getStations()).hasSize(3);
    }
}
