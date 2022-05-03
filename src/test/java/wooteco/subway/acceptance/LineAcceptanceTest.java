package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String LOCATION = "Location";

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        String lineName = "7호선";
        String lineColor = "bg-red-600";

        Map<String, String> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", lineColor);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(LOCATION)).isNotBlank();

        LineResponse lineResponse = response.body().as(LineResponse.class);
        assertAll(() -> {
            assertThat(lineResponse.getId()).isNotNull();
            assertThat(lineResponse.getName()).isEqualTo(lineName);
            assertThat(lineResponse.getColor()).isEqualTo(lineColor);
        });
    }
}
