package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.StationDao;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setup() {
        LineDao.clear();
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "grey darken-1");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 2);
        params.put("extraFare", 500);

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
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.body().as(LineResponse.class).getId()).isEqualTo(1L);
        assertThat(response.body().as(LineResponse.class).getName()).isEqualTo("2호선");
        assertThat(response.body().as(LineResponse.class).getColor()).isEqualTo("grey darken-1");
    }
}
