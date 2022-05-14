package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;

@Sql("/sectionInitSchema.sql")
public class SectionAcceptanceTest extends AcceptanceTest {

    private LineRequest createLineRequest(String name, String color) {
        return new LineRequest(name, color, 1L, 2L, 10);
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void save() {
        // given
        LineRequest lineRequest = createLineRequest("1호선", "blue");
        requestHttpPost(lineRequest, "/lines");

        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 6);

        // when
        ExtractableResponse<Response> response =
            requestHttpPost(sectionRequest, "/lines/2/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void delete() {
        // given
        LineRequest lineRequest = createLineRequest("분당선", "green");
        requestHttpPost(lineRequest, "/lines");

        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 6);

        // when
        ExtractableResponse<Response> response =
            requestHttpDelete(sectionRequest, "/lines/2/sections?stationId=2");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
