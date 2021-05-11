package wooteco.subway.line.section;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.line.LineAcceptanceTest.LINE_2;
import static wooteco.subway.line.LineAcceptanceTest.노선_등록;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        final String uri = 노선_등록(LINE_2).header("Location") + "/sections";
        final SectionRequest sectionRequest = new SectionRequest(2L, 4L, 10);
        final ExtractableResponse<Response> response = 구간_등록(uri, sectionRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        구간_생성값_검증(response, sectionRequest);
    }

    private ExtractableResponse<Response> 구간_등록(final String uri, final SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(uri)
            .then().log().all()
            .extract();
    }

    private void 구간_생성값_검증(final ExtractableResponse<Response> response, final SectionRequest sectionRequest) {
        final SectionResponse sectionResponse = response.body().as(SectionResponse.class);
        assertThat(sectionResponse.getId()).isEqualTo(getCreatedId(response));
        assertThat(sectionResponse.getDownStationId()).isEqualTo(sectionRequest.getDownStationId());
        assertThat(sectionResponse.getUpStationId()).isEqualTo(sectionRequest.getUpStationId());
        assertThat(sectionResponse.getDistance()).isEqualTo(sectionRequest.getDistance());
    }

    private long getCreatedId(final ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[4]);
    }
}
