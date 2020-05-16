package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class LineTest {
	private Line line;

	@BeforeEach
	void setUp() {
		line = Line.of("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-red-500");
		line.addEdge(new Edge(1L, 2L, 10, 10));
		line.addEdge(new Edge(2L, 3L, 10, 10));
	}

	@DisplayName("구간 목록을 조회한다.")
	@Test
	void getLineStations() {
		List<Long> stationIds = line.getEdgesId();

		assertThat(stationIds.size()).isEqualTo(3);
		assertThat(stationIds.get(0)).isEqualTo(1L);
		assertThat(stationIds.get(2)).isEqualTo(3L);
	}

	@DisplayName("특정 구간을 제거한다.")
	@ParameterizedTest
	@ValueSource(longs = {1L, 2L, 3L})
	void removeLineStation(Long stationId) {
		line.removeEdgeById(stationId);

		assertThat(line.getEdges()).hasSize(2);
	}

	@DisplayName("존재하지 않는 구간을 제거하려 시도한다.")
	@Test
	void removeLineStation_NotExistLineStation() {
		assertThatThrownBy(() -> line.removeEdgeById(4L))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("라인 정보를 갱신한다.")
	@Test
	void update() throws InterruptedException {
		Thread.sleep(100L);
		Line updated = line.update(Line.of("그니역", LocalTime.of(10, 0),
			LocalTime.of(17, 0), 10, "bg-blue-600"));

		assertThat(updated.getName()).isEqualTo("그니역");
		assertThat(updated.getStartTime()).isEqualTo(LocalTime.of(10, 0));
		assertThat(updated.getEndTime()).isEqualTo(LocalTime.of(17, 0));
		assertThat(updated.getIntervalTime()).isEqualTo(10);
		assertThat(updated.getColor()).isEqualTo("bg-blue-600");
		assertThat(updated.getCreatedAt()).isEqualTo(line.getCreatedAt());
		assertThat(updated.getUpdatedAt()).isNotEqualTo(line.getUpdatedAt());
	}

	@DisplayName("구간을 맨 앞에 추가한다.")
	@Test
	void addEdge_Front() {
		Edge front = Edge.starter(4L);
		line.addEdge(front);

		List<Long> stations = line.getEdgesId();
		assertThat(stations).hasSize(4);
		assertThat(stations).isEqualTo(Arrays.asList(4L, 1L, 2L, 3L));
	}

	@DisplayName("구간을 중간에 추가한다.")
	@Test
	void addEdge_Middle() {
		Edge middle = new Edge(1L, 4L, 10, 10);
		line.addEdge(middle);

		List<Long> stations = line.getEdgesId();
		assertThat(stations).hasSize(4);
		assertThat(stations).isEqualTo(Arrays.asList(1L, 4L, 2L, 3L));
	}

	@DisplayName("구간을 끝에 추가한다.")
	@Test
	void addEdge_Back() {
		Edge back = new Edge(3L, 4L, 10, 10);
		line.addEdge(back);

		List<Long> stations = line.getEdgesId();
		assertThat(stations).hasSize(4);
		assertThat(stations).isEqualTo(Arrays.asList(1L, 2L, 3L, 4L));
	}

	@DisplayName("이미 존재하는 구간을 추가한다.")
	@ParameterizedTest
	@CsvSource(value = {"1,2", "1,1"})
	void addEdge_Invalid(Long preStationId, Long stationId) {
		Edge invalid = new Edge(preStationId, stationId, 10, 10);

		assertThatThrownBy(() -> {
			line.addEdge(invalid);
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("역에 이전 역을 하나 더 추가한다.")
	@Test
	void addEdge_SameStationDifferentPreStation() {
		Edge invalid = new Edge(4L, 2L, 10, 10);

		assertThatThrownBy(() -> {
			line.addEdge(invalid);
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("역 방향 역을 추가한다.")
	@Test
	void addEdge_reversedEdge() {
		Edge invalid = new Edge(2L, 1L, 10, 10);

		assertThatThrownBy(() -> {
			line.addEdge(invalid);
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("본래와 끊어진 구간을 추가한다.")
	@Test
	void addEdge_CutoffEdge() {
		Edge invalid = new Edge(4L, 5L, 10, 10);

		assertThatThrownBy(() -> {
			line.addEdge(invalid);
		}).isInstanceOf(IllegalArgumentException.class);
	}
}
