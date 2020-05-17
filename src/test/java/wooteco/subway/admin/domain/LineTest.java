package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class LineTest {
	private Line line;

	@BeforeEach
	void setUp() {
		line = Line.of("2호선", "blue", LocalTime.of(5, 30), LocalTime.of(22, 30)
			, 5, new HashSet<>());
		line = line.withId(1L);
		line.addLineStation(LineStation.of(null, 1L, 10, 10));
		line.addLineStation(LineStation.of(1L, 2L, 10, 10));
		line.addLineStation(LineStation.of(2L, 3L, 10, 10));
	}

	@Test
	void getLineStations() {
		List<Long> stationIds = line.getLineStationsId();

		assertThat(stationIds.size()).isEqualTo(3);
		assertThat(stationIds.get(0)).isEqualTo(1L);
		assertThat(stationIds.get(2)).isEqualTo(3L);
	}

	@DisplayName("이미 있는 lineStation을 넣을 때 예외처리하는지 확인")
	@Test
	void addLineStation() {
		assertThatThrownBy(() -> line.addLineStation(LineStation.of(null, 1L, 10, 10)))
			.isInstanceOf(UnsupportedOperationException.class)
			.hasMessageContaining("이미 존재");
	}

	@DisplayName("없는 lineStation을 넣을 때 정상적으로 추가되는지 확인")
	@Test
	void addLineStation2() {
		int beforeSize = line.getStations().size();

		line.addLineStation(LineStation.of(3L, 4L, 10, 20));

		assertThat(line.getStations().size()).isEqualTo(beforeSize + 1);
	}

	@ParameterizedTest
	@ValueSource(longs = {1L, 2L, 3L})
	void removeLineStation(Long stationId) {
		line.removeLineStationById(stationId);

		assertThat(line.getStations()).hasSize(2);
	}
}
