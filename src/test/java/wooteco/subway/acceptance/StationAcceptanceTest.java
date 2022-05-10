package wooteco.subway.acceptance;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;
import static wooteco.subway.acceptance.util.RestAssuredUtils.checkProperErrorMessage;
import static wooteco.subway.acceptance.util.RestAssuredUtils.checkProperResponseStatus;
import static wooteco.subway.acceptance.util.RestAssuredUtils.checkSameResponseIds;
import static wooteco.subway.acceptance.util.RestAssuredUtils.createData;
import static wooteco.subway.acceptance.util.RestAssuredUtils.deleteData;
import static wooteco.subway.acceptance.util.RestAssuredUtils.getData;
import static wooteco.subway.acceptance.util.RestAssuredUtils.getLocationId;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.domain.Station;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private String stationName = "잠실역";

    @DisplayName("지하철역을 생성한다.")
    @Test
    void create() {
        // given
        final Station station = new Station(stationName);

        // when
        ExtractableResponse<Response> createResponse = createData("/stations", station);

        // then
        checkProperResponseStatus(createResponse, HttpStatus.CREATED);
        checkProperData("/stations/" + getLocationId(createResponse), station);

    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        final Station station = new Station(stationName);
        createData("/stations", station);

        // when
        ExtractableResponse<Response> createResponse = createData("/stations", station);

        // then
        checkProperResponseStatus(createResponse, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> response1 = createData("/stations", new Station(stationName));
        ExtractableResponse<Response> response2 = createData("/stations", new Station("역삼역"));

        // when
        ExtractableResponse<Response> getStationResponse = getData("/stations");

        // then
        checkProperResponseStatus(getStationResponse, HttpStatus.OK);
        List<ExtractableResponse<Response>> responses = Arrays.asList(response1, response2);
        checkSameResponseIds(getStationResponse, responses);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        final Station station = new Station(stationName);
        ExtractableResponse<Response> createResponse = createData("/stations", station);

        // when
        ExtractableResponse<Response> deleteResponse = deleteData(createResponse.header("Location"));

        // then
        checkProperResponseStatus(deleteResponse, HttpStatus.NO_CONTENT);
        checkProperErrorMessage("/stations/" + getLocationId(createResponse), "해당하는 역이 존재하지 않습니다.");
    }

    private void checkProperData(String url, Station station) {
        get(url).then()
                .assertThat()
                .body("id", equalTo(Integer.parseInt(url.split("/")[2])))
                .body("name", equalTo(station.getName()));
    }
}
