package wooteco.subway.admin.acceptance;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.admin.dto.StationResponse;

public class StationAcceptanceTest extends AcceptanceTest {
	private static final String STATION_NAME_JAMSIL = "잠실역";
	private static final String STATION_NAME_UNDONGJANG = "종합운동장역";
	private static final String STATION_NAME_SUNLEUNG = "선릉역";
	private static final String STATION_NAME_GANGNAM = "강남역";
	private static final long NON_EXIST_STATION_ID = Long.MAX_VALUE;

	@DisplayName("지하철 역 추가/삭제/목록 조회 기능. 이미 존재하는 역 추가 하거나, 존재하지 않는 역 삭제시 예외 발생")
	@Test
	void manageStation() {
		//given
		createStation(STATION_NAME_JAMSIL);
		createStation(STATION_NAME_UNDONGJANG);
		createStation(STATION_NAME_SUNLEUNG);
		createStation(STATION_NAME_GANGNAM);

		//when
		List<StationResponse> stations = getStations();
		//then
		assertThat(stations.size()).isEqualTo(4);

		//when	//then
		assertThatThrownBy(() -> createStation(STATION_NAME_SUNLEUNG));

		//when
		StationResponse stationJamsil = stations.get(0);
		Long jamsilStationId = stationJamsil.getId();
		deleteStation(jamsilStationId);

		//then
		List<StationResponse> stationsAfterDelete = getStations();
		assertThat(findRemainStationNames(stationsAfterDelete)).containsExactly(STATION_NAME_UNDONGJANG,
			STATION_NAME_SUNLEUNG, STATION_NAME_GANGNAM);

		//when	//then
		assertThatThrownBy(() -> deleteStation(NON_EXIST_STATION_ID));
	}

	private List<String> findRemainStationNames(List<StationResponse> stationsAfterDelete) {
		return stationsAfterDelete.stream().map(StationResponse::getName).collect(toList());
	}
}
