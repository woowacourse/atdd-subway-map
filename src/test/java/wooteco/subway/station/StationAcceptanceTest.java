package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.web.dto.StationResponse;

@DisplayName("역 인수 테스트")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final Map<String, String> DATA1 = new HashMap<>();
    private static final Map<String, String> DATA2 = new HashMap<>();
    private static final Map<String, String> DATA_EMPTY_STRING = new HashMap<>();
    private static final Map<String, String> DATA_NULL = new HashMap<>();

    private static final String STATIONS_PATH = "/stations/";
    private static final String LOCATION = "Location";
    private static final String NAME = "name";
    private static final long INVALID_ID = Long.MAX_VALUE;

    static {
        DATA1.put(NAME, "A역");
        DATA2.put(NAME, "B역");
        DATA_EMPTY_STRING.put(NAME, " ");
        DATA_NULL.put(NAME, null);
    }

    @Test
    @DisplayName("역 생성")
    void create() {
        // when
        ExtractableResponse<Response> response = postStation(DATA1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(LOCATION)).isNotBlank();
    }

    @Test
    @DisplayName("중복이름 역 생성불가")
    void createFail_duplicatedName() {
        // when
        ExtractableResponse<Response> response1 = postStation(DATA1);
        ExtractableResponse<Response> response2 = postStation(DATA1);

        // then
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("name 빈 문자열: 역 생성불가")
    void createFail_emptyString() {
        // when
        ExtractableResponse<Response> response = postStation(DATA_EMPTY_STRING);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("name null: 역 생성불가")
    void createFail_null() {
        // when
        ExtractableResponse<Response> response = postStation(DATA_NULL);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("역 목록 조회")
    void list() {
        /// given
        ExtractableResponse<Response> postResponse1 = postStation(DATA1);
        ExtractableResponse<Response> postResponse2 = postStation(DATA2);

        // when
        ExtractableResponse<Response> listResponse = listStation();

        // then
        assertThat(listResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<StationResponse> expectedList = toStationDtos(postResponse1, postResponse2);
        List<StationResponse> resultList = listResponse.jsonPath()
                .getList(".", StationResponse.class);

        assertThat(resultList).containsAll(expectedList);
    }

    private List<StationResponse> toStationDtos(ExtractableResponse<Response>... responses) {
        return Arrays.stream(responses)
                .map(response -> response.as(StationResponse.class))
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("역 삭제")
    void delete() {
        // given
        ExtractableResponse<Response> postResponse1 = postStation(DATA1);
        ExtractableResponse<Response> postResponse2 = postStation(DATA2);
        String uri = postResponse1.header(LOCATION);

        // when
        ExtractableResponse<Response> response = deleteStation(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        List<StationResponse> stationResponses = listStation().jsonPath()
                .getList(".", StationResponse.class);
        assertThat(stationResponses.size()).isEqualTo(1);
        assertThat(stationResponses.get(0).getName()).isEqualTo(DATA2.get(NAME));
    }

    @Test
    @DisplayName("존재하지 않는 역 삭제불가")
    void deleteLineByInvalidId() {
        // when
        ExtractableResponse<Response> response = deleteStation(STATIONS_PATH + INVALID_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> postStation(Map<String, String> data) {
        return getRequestSpecification()
                .body(data)
                .post(STATIONS_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> listStation() {
        return getRequestSpecification()
                .get(STATIONS_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteStation(String uri) {
        return getRequestSpecification()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private RequestSpecification getRequestSpecification() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
