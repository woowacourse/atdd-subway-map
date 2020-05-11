package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wooteco.subway.admin.line.domain.edge.LineStation;
import wooteco.subway.admin.line.domain.line.Line;
import wooteco.subway.admin.line.repository.LineRepository;
import wooteco.subway.admin.line.service.LineService;
import wooteco.subway.admin.line.service.dto.edge.LineStationCreateRequest;
import wooteco.subway.admin.line.service.dto.line.LineResponse;
import wooteco.subway.admin.station.domain.Station;
import wooteco.subway.admin.station.repository.StationRepository;

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
		line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-blue-600");
		lineService = new LineService(lineRepository, stationRepository);

		line.addLineStation(new LineStation(0L, null, 1L, 10, 10));
		line.addLineStation(new LineStation(1L, 1L, 2L, 10, 10));
		line.addLineStation(new LineStation(2L, 2L, 3L, 10, 10));
	}

	@Test
	void addLineStationAtTheFirstOfLine() {
		LineStationCreateRequest request = new LineStationCreateRequest(null, 4L, 10, 10);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.save(line.getId(), request);

		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(4L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(1L);
		assertThat(line.getLineStationsId().get(2)).isEqualTo(2L);
		assertThat(line.getLineStationsId().get(3)).isEqualTo(3L);
	}

	@Test
	void addLineStationBetweenTwo() {
		LineStationCreateRequest request = new LineStationCreateRequest(1L, 4L, 10, 10);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.save(line.getId(), request);

		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(4L);
		assertThat(line.getLineStationsId().get(2)).isEqualTo(2L);
		assertThat(line.getLineStationsId().get(3)).isEqualTo(3L);
	}

	@Test
	void addLineStationAtTheEndOfLine() {
		LineStationCreateRequest request = new LineStationCreateRequest(3L, 4L, 10, 10);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.save(line.getId(), request);

		assertThat(line.getStations()).hasSize(4);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(2L);
		assertThat(line.getLineStationsId().get(2)).isEqualTo(3L);
		assertThat(line.getLineStationsId().get(3)).isEqualTo(4L);
	}

	@Test
	void removeLineStationAtTheFirstOfLine() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.delete(line.getId(), 1L);

		assertThat(line.getStations()).hasSize(2);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(2L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(3L);
	}

	@Test
	void removeLineStationBetweenTwo() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.delete(line.getId(), 2L);

		assertThat(line.getStations()).hasSize(2);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(3L);
	}

	@Test
	void removeLineStationAtTheEndOfLine() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		lineService.delete(line.getId(), 3L);

		assertThat(line.getStations()).hasSize(2);
		assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
		assertThat(line.getLineStationsId().get(1)).isEqualTo(2L);
	}

	/* findLineWithStationsById 메서드를 순서에 따라 정렬하도록 수정해서
	   내부적으로 반복을 돌면서 findById를 수행하도록 변경하였습니다.
	   그래서 해당 테스트가 통과되지 않습니다. */
	@Test
	void findLineWithStationsById() {
		List<Station> stations = Lists.newArrayList(
			new Station("강남역"),
			new Station("역삼역"),
			new Station("삼성역"));
		when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
		when(stationRepository.findAllById(anyList())).thenReturn(stations);

		LineResponse lineResponse = lineService.findLineWithStationsById(1L);

		assertThat(lineResponse.getStations()).hasSize(3);
	}
}
