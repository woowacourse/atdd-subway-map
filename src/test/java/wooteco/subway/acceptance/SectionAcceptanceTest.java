package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.acceptance.request.LineRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest {
    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void createSection() {
        // given
        ExtractableResponse<Response> createLineResponse = LineRequest.createLineRequest(LineRequest.line1(1L, 2L));
        Long lineId = createLineResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = LineRequest.createSectionRequest(LineRequest.section1(2L, 150L), lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
