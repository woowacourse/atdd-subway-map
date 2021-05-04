package wooteco.subway.controller;

import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

class LineControllerTest extends AcceptanceTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() throws Exception {
        LineRequest lineRequest = new LineRequest("2호선", "red", 1L, 3L, 7);
        LineResponse lineResponse = new LineResponse(1L, "2호선", "red", new ArrayList<>());

        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .body(OBJECT_MAPPER.writeValueAsString(lineRequest))
            .when().post("/lines")
            .then().log().all()
            .statusCode(HttpStatus.valueOf(201).value())
            .body(is(OBJECT_MAPPER.writeValueAsString(lineResponse)));
    }
}