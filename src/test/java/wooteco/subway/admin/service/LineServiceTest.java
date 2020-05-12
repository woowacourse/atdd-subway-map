package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
	private List<Station> stations;
	private LineService lineService;

	@BeforeEach
	void setUp() {
		line = new Line(1L, "초록이", "2호선", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
		lineService = new LineService(lineRepository, stationRepository);

		stations = Arrays
			.asList(new Station(1L, "강남역"), new Station(2L, "역삼역"), new Station(3L, "삼성역"));

		line.addLineStation(new LineStation(null, 1L, 10, 10));
		line.addLineStation(new LineStation(1L, 2L, 10, 10));
		line.addLineStation(new LineStation(2L, 3L, 10, 10));
	}

	@Test
	void addLineStationAtTheFirstOfLine() {
		LineStationCreateRequest request = new LineStationCreateRequest(null, 4L, 10, 10);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.addLineStation(line.getId(), request);

		assertThat(line.getLineStations()).hasSize(4);
		assertThat(line.findLineStationsId().get(0)).isEqualTo(4L);
		assertThat(line.findLineStationsId().get(1)).isEqualTo(1L);
		assertThat(line.findLineStationsId().get(2)).isEqualTo(2L);
		assertThat(line.findLineStationsId().get(3)).isEqualTo(3L);
	}

	@Test
	void addLineStationBetweenTwo() {
		LineStationCreateRequest request = new LineStationCreateRequest(1L, 4L, 10, 10);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.addLineStation(line.getId(), request);

		assertThat(line.getLineStations()).hasSize(4);
		assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.findLineStationsId().get(1)).isEqualTo(4L);
		assertThat(line.findLineStationsId().get(2)).isEqualTo(2L);
		assertThat(line.findLineStationsId().get(3)).isEqualTo(3L);
	}

	@Test
	void addLineStationAtTheEndOfLine() {
		LineStationCreateRequest request = new LineStationCreateRequest(3L, 4L, 10, 10);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.addLineStation(line.getId(), request);

		assertThat(line.getLineStations()).hasSize(4);
		assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.findLineStationsId().get(1)).isEqualTo(2L);
		assertThat(line.findLineStationsId().get(2)).isEqualTo(3L);
		assertThat(line.findLineStationsId().get(3)).isEqualTo(4L);
	}

	@Test
	void removeLineStationAtTheFirstOfLine() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.removeLineStation(line.getId(), 1L);

		assertThat(line.getLineStations()).hasSize(2);
		assertThat(line.findLineStationsId().get(0)).isEqualTo(2L);
		assertThat(line.findLineStationsId().get(1)).isEqualTo(3L);
	}

	@Test
	void removeLineStationBetweenTwo() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.removeLineStation(line.getId(), 2L);

		assertThat(line.getLineStations()).hasSize(2);
		assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.findLineStationsId().get(1)).isEqualTo(3L);
	}

	@Test
	void removeLineStationAtTheEndOfLine() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.removeLineStation(line.getId(), 3L);

		assertThat(line.getLineStations()).hasSize(2);
		assertThat(line.findLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.findLineStationsId().get(1)).isEqualTo(2L);
	}

	@Test
	void findLineWithStationsById() {
		when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
		when(stationRepository.findAllById(anySet())).thenReturn(stations);

		LineResponse lineResponse = lineService.findLineResponseWithStationsById(1L);

		assertThat(lineResponse.getStations()).hasSize(3);
	}
}
