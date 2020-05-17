package wooteco.subway.admin.service;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineWithStationsResponse;
import wooteco.subway.admin.repository.LineRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
	@Mock
	private LineRepository lineRepository;
	@Mock
	private StationService stationService;

	private Line line;
	private LineService lineService;
	private Station station1;
	private Station station2;
	private Station station3;

	@BeforeEach
	void setUp() {
		line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-pink-700");
		lineService = new LineService(lineRepository, stationService);

		line.addLineStation(new LineStation(null, 1L, 10, 10));
		line.addLineStation(new LineStation(1L, 2L, 10, 10));
		line.addLineStation(new LineStation(2L, 3L, 10, 10));

		station1 = new Station(1L, "일번역");
		station2 = new Station(2L, "이번역");
		station3 = new Station(3L, "삼번역");
	}

	@Test
	void addLineStationAtTheFirstOfLine() {
		LineStationRequest lineStationRequest = new LineStationRequest(line.getId(), ""
				, "처음에들어갈역");

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(stationService.findByName(anyString())).thenReturn(new Station(4L, "처음에들어갈역"));
		lineService.addLineStation(lineStationRequest);

		System.out.println(line.getStations());

		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(4L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(1L);
		assertThat(line.getLineStationsId().get(2)).isEqualTo(2L);
		assertThat(line.getLineStationsId().get(3)).isEqualTo(3L);
	}

	@Test
	void addLineStationBetweenTwo() {
		LineStationRequest lineStationRequest = new LineStationRequest(line.getId(), "일번역",
				"중간에들어갈역");

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(stationService.findByName(lineStationRequest.getPreStationName())).thenReturn(station1);
		when(stationService.findByName(lineStationRequest.getStationName()))
				.thenReturn(new Station(4L, "중간에들어갈역"));
		lineService.addLineStation(lineStationRequest);

		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(4L);
		assertThat(line.getLineStationsId().get(2)).isEqualTo(2L);
		assertThat(line.getLineStationsId().get(3)).isEqualTo(3L);
	}

	@Test
	void addLineStationAtTheEndOfLine() {
		LineStationRequest lineStationRequest = new LineStationRequest(line.getId(), "삼번역",
				"마지막에들어갈역");

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(stationService.findByName(lineStationRequest.getPreStationName())).thenReturn(station3);
		when(stationService.findByName(lineStationRequest.getStationName()))
				.thenReturn(new Station(4L, "마지막에들어갈역"));
		lineService.addLineStation(lineStationRequest);

		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(2L);
		assertThat(line.getLineStationsId().get(2)).isEqualTo(3L);
		assertThat(line.getLineStationsId().get(3)).isEqualTo(4L);
	}

	@Test
	void removeLineStationAtTheFirstOfLine() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.removeLineStation(line.getId(), 1L);

		assertThat(line.getStations()).hasSize(2);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(2L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(3L);
	}

	@Test
	void removeLineStationBetweenTwo() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.removeLineStation(line.getId(), 2L);

		assertThat(line.getStations()).hasSize(2);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(3L);
	}

	@Test
	void removeLineStationAtTheEndOfLine() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.removeLineStation(line.getId(), 3L);

		assertThat(line.getStations()).hasSize(2);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(2L);
	}

	@Test
	void findLineWithStationsById() {
		List<Station> stations = Lists.newArrayList(new Station("강남역"), new Station("역삼역"), new Station("삼성역"));
		when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
		when(stationService.findAllById(anyList())).thenReturn(stations);

		LineWithStationsResponse lineWithStationsResponse = lineService.findLineWithStationsBy(1L);

		assertThat(lineWithStationsResponse.getStations()).hasSize(3);
	}
}
