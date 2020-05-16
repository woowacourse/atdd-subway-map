package wooteco.subway.admin.acceptance;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.dto.StationResponse;

public class LineStationAcceptanceTest extends AcceptanceTest {

	private static final long NOT_EXIST_STATION_ID = Long.MAX_VALUE;
	private static final String STATION_NAME_GANGBYUN = "강변";
	private static final String STATION_NAME_JAMSILNARU = "잠실나루";
	private static final String STATION_NAME_JAMSIL = "잠실";
	private static final String STATION_NAME_SINDANG = "신당";
	private static final String LINE_NAME_TWO = "2호선";

	@DisplayName("지하철 노선에서 지하철역 추가/제외")
	@Test
	void manageLineStation() {
		//given
		createStation(STATION_NAME_GANGBYUN);
		createStation(STATION_NAME_JAMSILNARU);
		createStation(STATION_NAME_JAMSIL);
		createStation(STATION_NAME_SINDANG);

		createLine(LINE_NAME_TWO);

		List<StationResponse> stations = getStations();
		Long firstStationId = stations.get(0).getId();
		Long secondStationId = stations.get(1).getId();
		Long thirdStationId = stations.get(2).getId();
		Long newStationId = stations.get(3).getId();

		List<LineResponse> lines = getLines();
		Long firstLineId = lines.get(0).getId();

		//when
		createLineStation(null, secondStationId, firstLineId);
		createLineStation(secondStationId, thirdStationId, firstLineId);
		createLineStation(thirdStationId, firstStationId, firstLineId);
		LineResponse firstLine = getLine(firstLineId);
		List<LineStationResponse> firstLineStations = getLineStations(firstLineId);
		//then
		assertThat(firstLineStations.size()).isEqualTo(3);

		assertThat(getStationNamesFrom(firstLine))
			.containsExactly(STATION_NAME_JAMSILNARU, STATION_NAME_JAMSIL, STATION_NAME_GANGBYUN);

		//when
		LineStationResponse firstLineStationResponse = firstLineStations.get(0);
		//then
		assertThat(firstLineStationResponse.getLineId()).isEqualTo(firstLineId);
		assertThat(firstLineStationResponse.getStationId()).isEqualTo(secondStationId);

		//given


		//when
		Long alreadyExistStationId = firstStationId;
		//then
		assertThatThrownBy(() -> createLineStation(null, alreadyExistStationId, firstLineId));

		//when
		Long nonExistLineId = null;
		//then
		assertThatThrownBy(() -> createLineStation(null, newStationId, nonExistLineId));

		//when //then
		assertThatThrownBy(() -> createLineStation(null, NOT_EXIST_STATION_ID, firstLineId));
		//when //then
		assertThatThrownBy(() -> createLineStation(NOT_EXIST_STATION_ID, newStationId, firstLineId));

		//when
		deleteLineStation(firstLineId, secondStationId);
		//then
		List<LineStationResponse> lineStationsAfterDelete = getLineStations(firstLineId);
		assertThat(lineStationsAfterDelete.size()).isEqualTo(2);
		//and
		boolean isExistLineStation = isExistLineStation(firstLineStationResponse, lineStationsAfterDelete);
		assertThat(isExistLineStation).isFalse();

		//when //then
		assertThatThrownBy(() -> deleteLineStation(NOT_EXIST_STATION_ID, firstStationId));

		//when //then
		assertThatThrownBy(() -> deleteLineStation(firstLineId, NOT_EXIST_STATION_ID));
	}

	private List<String> getStationNamesFrom(LineResponse lineResponse) {
		return lineResponse.getStations().stream()
			.map(StationResponse::getName)
			.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

	private boolean isExistLineStation(LineStationResponse lineStationResponse,
		List<LineStationResponse> lineStationsAfterDelete) {
		return lineStationsAfterDelete.stream()
			.filter(response -> response.getLineId().equals(lineStationResponse.getLineId()))
			.anyMatch(response -> response.getStationId().equals(lineStationResponse.getStationId()));
	}
}
