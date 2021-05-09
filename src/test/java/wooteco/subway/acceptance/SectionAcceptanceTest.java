package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.acceptance.template.LineRequest;
import wooteco.subway.controller.dto.request.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.SectionRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void createSection() {
        // given
        Long lineId = LineRequest.createLineRequestAndReturnId(new LineCreateRequestDto(
                "1호선",
                "yellow",
                1L,
                2L,
                10
        ));

        // when
        ExtractableResponse<Response> response = LineRequest.createSectionRequestAndReturnResponse(
                new SectionRequestDto(2L, 150L, 10),
                lineId
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
