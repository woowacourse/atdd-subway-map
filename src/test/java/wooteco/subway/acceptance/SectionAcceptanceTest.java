package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@Sql("/sectionTestSchema.sql")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("하행에 구간을 추가한다.")
    void save() {
        //given
        String location = getLocation();

        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 6);

        //when
        ExtractableResponse<Response> response = getResponseExtractableResponse(sectionRequest);

        ExtractableResponse<Response> lineResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(location)
                .then().log().all()
                .extract();
        List<StationResponse> stations = lineResponse.jsonPath().getList("stations", StationResponse.class);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stations).extracting("id", "name")
                .containsExactly(tuple(1L, "신도림역"), tuple(2L, "왕십리역"), tuple(3L, "용산역"));
    }


    @Test
    @DisplayName("상행에 구간을 추가한다.")
    void save2() {
        //given
        String location = getLocation();

        SectionRequest sectionRequest = new SectionRequest(3L, 1L, 6);

        //when
        ExtractableResponse<Response> response = getResponseExtractableResponse(sectionRequest);

        ExtractableResponse<Response> lineResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(location)
                .then().log().all()
                .extract();
        List<StationResponse> stations = lineResponse.jsonPath().getList("stations", StationResponse.class);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stations).extracting("id", "name")
                .containsExactly(tuple(3L, "용산역"), tuple(1L, "신도림역"), tuple(2L, "왕십리역"));
    }

    @Test
    @DisplayName("상행 중간에 구간을 추가한다.")
    void save3() {
        //given
        String location = getLocation();

        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 6);

        //when
        ExtractableResponse<Response> response = getResponseExtractableResponse(sectionRequest);

        ExtractableResponse<Response> lineResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(location)
                .then().log().all()
                .extract();
        List<StationResponse> stations = lineResponse.jsonPath().getList("stations", StationResponse.class);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stations).extracting("id", "name")
                .containsExactly(tuple(1L, "신도림역"), tuple(3L, "용산역"), tuple(2L, "왕십리역"));
    }

    @Test
    @DisplayName("하행 중간에 구간을 추가한다.")
    void save4() {
        //given
        String location = getLocation();

        SectionRequest sectionRequest = new SectionRequest(3L, 2L, 6);

        //when
        ExtractableResponse<Response> response = getResponseExtractableResponse(sectionRequest);

        ExtractableResponse<Response> lineResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(location)
                .then().log().all()
                .extract();
        List<StationResponse> stations = lineResponse.jsonPath().getList("stations", StationResponse.class);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stations).extracting("id", "name")
                .containsExactly(tuple(1L, "신도림역"), tuple(3L, "용산역"), tuple(2L, "왕십리역"));
    }

    @Test
    @DisplayName("중간에 거리가 더 큰 구간을 추가할 수 없다.")
    void notSave() {
        //given
        getLocation();

        SectionRequest sectionRequest = new SectionRequest(3L, 2L, 11);

        //when
        ExtractableResponse<Response> response = getResponseExtractableResponse(sectionRequest);

        assertThat(response.jsonPath().getString("message"))
                .isEqualTo("역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void delete() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", "분당선");
        params.put("color", "yellow");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 6);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/2/sections?stationId=2")
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private String getLocation() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "분당선");
        params.put("color", "yellow");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract()
                .header("location");
    }

    private ExtractableResponse<Response> getResponseExtractableResponse(SectionRequest sectionRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/2/sections")
                .then().log().all()
                .extract();
        return response;
    }
}
