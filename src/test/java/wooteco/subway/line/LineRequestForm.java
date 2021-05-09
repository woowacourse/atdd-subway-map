package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.RequestForm;
import wooteco.subway.line.dto.request.LineUpdateRequest;

public class LineRequestForm extends RequestForm {

    private LineRequestForm() {
    }

    public static ExtractableResponse<Response> findByIdRequest(String id) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> updateRequest(String id, LineUpdateRequest body) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }
}
