package wooteco.subway.admin.service;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.exception.DuplicateLineNameException;
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
		line = new Line(1L, "2호선", LocalTime.of(5, 30), LocalTime.of(22, 30), 5, "");
		lineService = new LineService(lineRepository, stationRepository);

		line.addLineStation(new LineStation(null, 1L, 10, 10));
		line.addLineStation(new LineStation(1L, 2L, 10, 10));
		line.addLineStation(new LineStation(2L, 3L, 10, 10));
	}

	@Test
	void addLineStationAtTheFirstOfLine() {
		LineStationCreateRequest request = new LineStationCreateRequest(null, 4L, 10, 10);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(stationRepository.existsById(anyLong())).thenReturn(true);

		lineService.addLineStation(line.getId(), request.toLineStation());

		assertThat(generatePairWith(findPreStationsId(), line.findLineStationIds()))
			.containsExactly(entry(null, 4L), entry(4L, 1L), entry(1L, 2L), entry(2L, 3L));
	}

	@Test
	void addLineStationBetweenTwo() {
		LineStationCreateRequest request = new LineStationCreateRequest(1L, 4L, 10, 10);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(stationRepository.existsById(anyLong())).thenReturn(true);

		lineService.addLineStation(line.getId(), request.toLineStation());

		assertThat(generatePairWith(findPreStationsId(), line.findLineStationIds()))
			.containsExactly(entry(null, 1L), entry(1L, 4L), entry(4L, 2L), entry(2L, 3L));
	}

	@Test
	void addLineStationAtTheEndOfLine() {
		LineStationCreateRequest request = new LineStationCreateRequest(3L, 4L, 10, 10);

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(stationRepository.existsById(anyLong())).thenReturn(true);

		lineService.addLineStation(line.getId(), request.toLineStation());

		assertThat(generatePairWith(findPreStationsId(), line.findLineStationIds()))
			.containsExactly(entry(null, 1L), entry(1L, 2L), entry(2L, 3L), entry(3L, 4L));
	}

	@Test
	void removeLineStationAtTheFirstOfLine() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(stationRepository.existsById(anyLong())).thenReturn(true);

		lineService.removeLineStation(line.getId(), 1L);

		assertThat(generatePairWith(findPreStationsId(), line.findLineStationIds()))
			.containsExactly(entry(null, 2L), entry(2L, 3L));
	}

	@Test
	void removeLineStationBetweenTwo() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(stationRepository.existsById(anyLong())).thenReturn(true);

		lineService.removeLineStation(line.getId(), 2L);

		assertThat(generatePairWith(findPreStationsId(), line.findLineStationIds()))
			.containsExactly(entry(null, 1L), entry(1L, 3L));
	}

	@Test
	void removeLineStationAtTheEndOfLine() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(stationRepository.existsById(anyLong())).thenReturn(true);

		lineService.removeLineStation(line.getId(), 3L);

		assertThat(generatePairWith(findPreStationsId(), line.findLineStationIds()))
			.containsExactly(entry(null, 1L), entry(1L, 2L));
	}

	@Test
	void removeAllLineStations() {
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(stationRepository.existsById(anyLong())).thenReturn(true);

		lineService.removeLineStation(line.getId(), 3L);
		lineService.removeLineStation(line.getId(), 1L);
		lineService.removeLineStation(line.getId(), 2L);

		assertThat(line.getStations()).hasSize(0);
	}

	@Test
	void findLineById() {
		when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
		Line line = lineService.findLine(1L);

		assertThat(line.getStations()).hasSize(3);
	}

	@Test
	void saveNewLineWithExistingName() {
		Line newLine = new Line(null, "2호선", LocalTime.of(5, 30), LocalTime.of(22, 30), 5, "");
		when(lineRepository.existsByName(newLine.getName())).thenReturn(line.getName().equals(newLine.getName()));
		assertThatThrownBy(() -> lineService.save(newLine))
			.isInstanceOf(DuplicateLineNameException.class);
	}

	@Test
	void saveNewLineWithNewName() {
		Line newLine = new Line(null, "3호선", LocalTime.of(5, 30), LocalTime.of(22, 30), 5, "");
		when(lineRepository.existsByName(newLine.getName())).thenReturn(line.getName().equals(newLine.getName()));
		assertThatCode(() -> lineService.save(newLine))
			.doesNotThrowAnyException();
	}

	@Test
	void updateLineWithSameName() {
		Line newLine = new Line(1L, "2호선", LocalTime.of(7, 50), LocalTime.of(16, 5), 9, "");

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));

		assertThatCode(() -> lineService.updateLine(newLine.getId(), newLine))
			.doesNotThrowAnyException();
	}

	@Test
	void updateLineWithExistName() {
		Line newLine = new Line(1L, "1호선", LocalTime.of(5, 30), LocalTime.of(22, 30), 5, "");

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(lineRepository.existsByName(newLine.getName())).thenReturn(true);
		assertThatThrownBy(() -> lineService.updateLine(newLine.getId(), newLine))
			.isInstanceOf(DuplicateLineNameException.class);
	}

	@Test
	void updateLineWithNewName() {
		Line newLine = new Line(1L, "6호선", LocalTime.of(5, 30), LocalTime.of(22, 30), 5, "");

		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(lineRepository.existsByName(newLine.getName())).thenReturn(line.getName().equals(newLine.getName()));
		assertThatCode(() -> lineService.updateLine(newLine.getId(), newLine))
			.doesNotThrowAnyException();
	}

	private Map<Long, Long> generatePairWith(List<Long> preStationIds, List<Long> stationIds) {
		Map<Long, Long> result = new LinkedHashMap<>();
		int size = preStationIds.size();
		for (int i = 0; i < size; i++) {
			result.put(preStationIds.get(i), stationIds.get(i));
		}
		return result;
	}

	private List<Long> findPreStationsId() {
		return line.getStations().stream()
			.map(LineStation::getPreStationId)
			.collect(toList());
	}
}
