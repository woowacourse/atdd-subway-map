package wooteco.subway.acceptance.fixture;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.List;

import org.springframework.http.HttpStatus;

import wooteco.subway.dto.response.ExceptionResponse;

public class SimpleResponse {
    private final Response response;

    public SimpleResponse(Response response) {
        this.response = response;
    }

    public boolean containsExceptionMessage(String message) {
        return this.toObject(ExceptionResponse.class)
                .getMessage()
                .contains(message);
    }

    public <T> T toObject(Class<T> clazz) {
        return this.extractJsonPath()
                .getObject(".", clazz);
    }

    public <T> List<T> toList(Class<T> clazz) {
        return this.extractJsonPath()
                .getList(".", clazz);
    }

    private JsonPath extractJsonPath() {
        return response
                .then().log().all()
                .extract()
                .body().jsonPath();
    }

    public void assertStatus(HttpStatus status) {
        response.then()
                .statusCode(status.value());
    }

    public Long getIdFromLocation() {
        return Long.parseLong(getHeader("Location").split("/")[2]);
    }

    public String getHeader(String name) {
        return response.header(name);
    }
}
