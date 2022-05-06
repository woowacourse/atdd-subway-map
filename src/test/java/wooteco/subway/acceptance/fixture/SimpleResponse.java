package wooteco.subway.acceptance.fixture;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.ExceptionResponse;

public class SimpleResponse {
    private final Response response;

    public SimpleResponse(Response response) {
        this.response = response;
    }

    public ExtractableResponse<Response> thenExtract() {
        return response
                .then().log().all()
                .extract();
    }

    public boolean hasStatus(HttpStatus status) {
        return response.statusCode() == status.value();
    }

    public boolean containsExceptionMessage(String message) {
        return this.toObject(ExceptionResponse.class)
                .getMessage()
                .contains(message);
    }

    public String getHeader(String name) {
        return response.header(name);
    }

    public <T> T toObject(Class<T> clazz) {
        return this.thenExtract()
                .body().jsonPath()
                .getObject(".", clazz);
    }

    public <T> List<T> toList(Class<T> clazz) {
        return this.thenExtract()
                .body().jsonPath()
                .getList(".", clazz);
    }

    public Long getIdFromLocation() {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }
}
