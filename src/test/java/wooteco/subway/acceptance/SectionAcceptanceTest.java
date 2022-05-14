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
    @DisplayName("구간을 추가한다.")
    void save() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", "분당선");
        params.put("color", "yellow");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");
        String location = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract()
                .header("location");

        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 6);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/2/sections")
                .then().log().all()
                .extract();

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
}
