package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("노선을 생성한다.")
    public void createLine() {
        // given
        Map<String, String> body =
            Map.of("name", "신분당선", "color", "bg-red-600");
        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(body).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
        // then
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank()
        );
    }
}
