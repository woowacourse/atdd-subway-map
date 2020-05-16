package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
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
	void update() {
	}

	@DisplayName("구간을 맨 앞에 추가한다.")
	@Test
	void addEdge_Front() {
	}

	@DisplayName("구간을 중간에 추가한다.")
	@Test
	void addEdge_Middle() {

	}

	@DisplayName("구간을 끝에 추가한다.")
	@Test
	void addEdge_Back() {

	}
}
