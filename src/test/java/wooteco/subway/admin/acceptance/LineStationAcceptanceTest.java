package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

public class LineStationAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철 노선에서 지하철역 추가 / 제외")
	@Test
	void manageLineStation() {
		// Given
		createLine("신분당선");
		createStation("잠실역");
		createStation("종합운동장역");
		LineResponse line = getLines().get(0); // 신분당
		StationResponse station = getStations().get(0); // 잠실

		// When
		addStationToLine(line.getId(), station.getId());
		// Then
		LineResponse lineResponse = findStationsByLineId(line.getId());
		assertThat(lineResponse.getStations()).isNotNull();

		// When
		List<Station> stations = findStationsByLineId(line.getId()).getStations();
		// Then
		assertThat(stations.size()).isNotEqualTo(0);
		assertThat(stations).contains(station.toStation());

		// When
		deleteStation(line.getId(), station.getId());
		// Then
		stations = findStationsByLineId(line.getId()).getStations();
		assertThat(stations).doesNotContain(station.toStation());

		// When
		stations = findStationsByLineId(line.getId()).getStations();
		// Then
		assertThat(stations).isNotNull();
		assertThat(stations).doesNotContain(station.toStation());
	}
}