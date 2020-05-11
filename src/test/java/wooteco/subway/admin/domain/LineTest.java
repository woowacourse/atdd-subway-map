package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wooteco.subway.admin.line.domain.edge.LineStation;
import wooteco.subway.admin.line.domain.line.Line;

public class LineTest {
	private Line line;

	@BeforeEach
	void setUp() {
		line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-blue-600");
		line.addLineStation(new LineStation(0L, null, 1L, 10, 10));
		line.addLineStation(new LineStation(1L, 1L, 2L, 10, 10));
		line.addLineStation(new LineStation(2L, 2L, 3L, 10, 10));
	}

	@Test
	void getLineStations() {
		List<Long> stationIds = line.getLineStationsId();

		assertThat(stationIds.size()).isEqualTo(3);
		assertThat(stationIds.get(0)).isEqualTo(1L);
		assertThat(stationIds.get(2)).isEqualTo(3L);
	}

	@ParameterizedTest
	@ValueSource(longs = {1L, 2L, 3L})
	void removeLineStation(Long stationId) {
		line.removeLineStationById(stationId);

		assertThat(line.getStations()).hasSize(2);
	}
}
