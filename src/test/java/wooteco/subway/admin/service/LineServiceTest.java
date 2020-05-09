package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private Line line;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "", new HashSet<>());
        lineService = new LineService(lineRepository, stationRepository);

        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));
    }

    @DisplayName("동일한 이름을 가진 노선이 입력되면 에러 발생")
    @Test
    void saveLinesWhenHaveSameNames() {
        Line duplicatedLine = new Line(2L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "", new HashSet<>());
        when(lineRepository.findLineWithStationsByName(line.getName())).thenReturn(Optional.ofNullable(line));

        assertThatThrownBy(() -> lineService.save(duplicatedLine))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("중복된 지하철 역입니다. name = " + duplicatedLine.getName());
    }

    @DisplayName("수정된 노선 이름이 이미 존재한다면 에러 발생")
    @Test
    void updateLineWhenExistSameName() {
        Line initial = new Line(2L, "3호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "", new HashSet<>());
        Line updated = new Line(2L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "", new HashSet<>());

        when(lineRepository.findById(initial.getId())).thenReturn(Optional.of(initial));

        when(lineRepository.findLineWithStationsByName(line.getName())).thenReturn(Optional.of(line));

        assertThatThrownBy(() -> lineService.updateLine(initial.getId(), updated))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("중복된 지하철 역입니다. name = " + updated.getName());
    }

    @DisplayName("노선의 시작역을 맨 앞에 추가")
    @Test
    void addLineStationAtTheFirstOfLine() {
        // given
        LineStationCreateRequest request = new LineStationCreateRequest(null, 4L, 10, 10);
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));

        // when
        lineService.addLineStation(line.getId(), request);

        // then
        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getStationsId().get(0)).isEqualTo(4L);
        assertThat(line.getStationsId().get(1)).isEqualTo(1L);
        assertThat(line.getStationsId().get(2)).isEqualTo(2L);
        assertThat(line.getStationsId().get(3)).isEqualTo(3L);
    }

    @DisplayName("노선의 시작역을 중간에 추가")
    @Test
    void addLineStationBetweenTwo() {
        LineStationCreateRequest request = new LineStationCreateRequest(1L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getStationsId().get(1)).isEqualTo(4L);
        assertThat(line.getStationsId().get(2)).isEqualTo(2L);
        assertThat(line.getStationsId().get(3)).isEqualTo(3L);
    }

    @DisplayName("노선의 시작역을 마지막에 추가")
    @Test
    void addLineStationAtTheEndOfLine() {
        LineStationCreateRequest request = new LineStationCreateRequest(3L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getStationsId().get(1)).isEqualTo(2L);
        assertThat(line.getStationsId().get(2)).isEqualTo(3L);
        assertThat(line.getStationsId().get(3)).isEqualTo(4L);
    }

    @DisplayName("노선의 시작역을 노선에서 제외")
    @Test
    void removeLineStationAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 1L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getStationsId().get(0)).isEqualTo(2L);
        assertThat(line.getStationsId().get(1)).isEqualTo(3L);
    }

    @DisplayName("노선의 중간역을 노선에서 제외")
    @Test
    void removeLineStationBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 2L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getStationsId().get(1)).isEqualTo(3L);
    }

    @DisplayName("노선의 마지막 역을 노선에서 제외")
    @Test
    void removeLineStationAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 3L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getStationsId().get(1)).isEqualTo(2L);
    }

    @DisplayName("노선과 노선의 지하철역을 조회")
    @Test
    void findLineWithStationsById() {
        Set<Station> stations = Sets.newLinkedHashSet(new Station("강남역"), new Station("역삼역"), new Station("삼성역"));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findAllById(anyList())).thenReturn(stations);

        LineResponse lineResponse = lineService.findLineWithStationsById(1L);

        assertThat(lineResponse.getStations()).hasSize(3);
    }
}
