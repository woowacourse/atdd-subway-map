package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.dto.StationResponse;

public class LineStationAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철 노선에서 지하철역 추가 / 제외")
	@Test
	void manageLineStation() {
		//given
		createStation("강변");
		createStation("잠실나루");
		createStation("잠실");

		createLine("2호선");
		List<StationResponse> stations = getStations();
		List<LineResponse> lines = getLines();
		LineResponse line = getLine(lines.get(0).getId());

		//when
		createLineStation(null, stations.get(1).getId(), line.getId());
		createLineStation(stations.get(1).getId(), stations.get(2).getId(), line.getId());
		createLineStation(stations.get(2).getId(), stations.get(0).getId(), line.getId());
		//then
		List<LineStationResponse> lineStations = getLineStations(line.getId());
		assertThat(lineStations.size()).isEqualTo(3);

		assertThat(getLine(lines.get(0).getId()).getStations().stream().map(StationResponse::getName))
			.containsExactly("잠실나루", "잠실", "강변");

		//when
		LineStationResponse lineStationResponse = lineStations.get(0);
		//then
		assertThat(lineStationResponse.getLineId()).isEqualTo(line.getId());
		assertThat(lineStationResponse.getStationId()).isEqualTo(stations.get(1).getId());

		//given
		createStation("테스트역");

		//when //then
		assertThatThrownBy(() -> createLineStation(null, stations.get(0).getId(), line.getId()));
		//when //then
		assertThatThrownBy(() -> createLineStation(null, stations.get(3).getId(), null));
		//when //then
		assertThatThrownBy(() -> createLineStation(null, Long.MAX_VALUE, line.getId()));
		//when //then
		assertThatThrownBy(() -> createLineStation(Long.MAX_VALUE, stations.get(3).getId(), line.getId()));

		//when
		deleteLineStation(line.getId(), stations.get(1).getId());
		//then
		List<LineStationResponse> lineStationsAfterDelete = getLineStations(line.getId());
		assertThat(lineStationsAfterDelete.size()).isEqualTo(2);
		//and
		boolean isExistLineStation = isExistLineStation(lineStationResponse, lineStationsAfterDelete);
		assertThat(isExistLineStation).isFalse();

		//when //then
		assertThatThrownBy(() -> deleteLineStation(Long.MAX_VALUE, stations.get(0).getId()));

		//when //then
		assertThatThrownBy(() -> deleteLineStation(line.getId(), Long.MAX_VALUE));
	}

	private boolean isExistLineStation(LineStationResponse lineStationResponse,
		List<LineStationResponse> lineStationsAfterDelete) {
		return lineStationsAfterDelete.stream()
			.filter(response -> response.getLineId().equals(lineStationResponse.getLineId()))
			.anyMatch(response -> response.getStationId().equals(lineStationResponse.getStationId()));
	}
}
