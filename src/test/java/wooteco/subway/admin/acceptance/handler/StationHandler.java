package wooteco.subway.admin.acceptance.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.dto.StationResponse;

@Component
public class StationHandler {
    private static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    public void createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/stations").
            then().
            log().all().
            statusCode(HttpStatus.CREATED.value());
    }

    public List<StationResponse> getStations() {
        return
            given().
                when().
                get("/stations").
                then().
                log().all().
                extract().
                jsonPath().getList(".", StationResponse.class);
    }

    public void deleteStation(Long id) {
        given().
            when().
            delete("/stations/" + id).
            then().
            log().all();
    }
}
