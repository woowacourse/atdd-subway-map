package wooteco.subway.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.RequestUtil;
import wooteco.subway.dto.StationResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {
    private String lineLocation;
    private String namSungStationId;
    private String naeBangStationId;

    @BeforeEach
    void setUp7Line() {
        namSungStationId = RequestUtil.requestCreateStation("남성역").header("Location").split("/")[2];
        naeBangStationId = RequestUtil.requestCreateStation("내방역").header("Location").split("/")[2];
        int distance = 10;

        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-green-600");
        params.put("name", "7호선");
        params.put("upStationId", namSungStationId);
        params.put("downStationId", naeBangStationId);
        params.put("distance", Integer.toString(distance));

        ExtractableResponse<Response> createResponse = RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();

        lineLocation = createResponse.header("Location");
    }

    @Test
    @DisplayName("구간을 생성하는 요청을 보낸다.")
    void createSection() {
        String isuStationId = RequestUtil.requestCreateStation("이수역").header("Location").split("/")[2];
        ExtractableResponse<Response> response = RequestUtil.requestSection(lineLocation.split("/")[2], namSungStationId, isuStationId, "3");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간을 삭제하는 요청을 보낸다.")
    void deleteSection() {
        // given
        String sangDoStationId = RequestUtil.requestCreateStation("상도역").header("Location").split("/")[2];
        RequestUtil.requestSection(lineLocation.split("/")[2], sangDoStationId, namSungStationId, "7");

        // when
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
                .when()
                .delete(lineLocation + "/sections?stationId=" + sangDoStationId)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .when()
                .get(lineLocation)
                .then().log().all()
                .extract();

        List<StationResponse> stationResponses = getResponse.jsonPath().getList("stations", StationResponse.class);
        List<StationResponse> expectedStationResponses = Arrays.asList(
                new StationResponse(Long.parseLong(namSungStationId), "남성역"),
                new StationResponse(Long.parseLong(naeBangStationId), "내방역")
        );

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(stationResponses).isEqualTo(expectedStationResponses);
    }
}
