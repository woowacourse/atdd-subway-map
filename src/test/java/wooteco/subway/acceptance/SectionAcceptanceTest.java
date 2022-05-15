package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.SectionRequest;

@DisplayName("구간 관련 기능")
@Sql("classpath:setUp_test_db.sql")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("상행 종점 구간을 생성한다.")
    @Test
    void addUpSection() {
        // given
        SectionRequest 구간_param = new SectionRequest(3L, 1L, 10);

        // when
        ExtractableResponse<Response> 구간_response = httpPost("/lines/1/sections", 구간_param);

        // then
        assertThat(구간_response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("하행 종점 구간을 생성한다.")
    void addDownSection() {
        // given
        SectionRequest 구간_param = new SectionRequest(2L, 3L, 10);

        // when
        ExtractableResponse<Response> 구간_response = httpPost("/lines/1/sections", 구간_param);

        // then
        assertThat(구간_response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("중간에 구간을 생성한다.")
    void addUpMiddleSection() {
        // given
        SectionRequest 구간_param = new SectionRequest(2L, 3L, 4);

        // when
        final ExtractableResponse<Response> 구간_응답 = httpPost("/lines/1/sections", 구간_param);

        // then
        assertThat(구간_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("종점의 구간을 제거한다.")
    void deleteFinalSection() {
        // given
        SectionRequest 구간_param = new SectionRequest(2L, 3L, 10);
        httpPost("/lines/1/sections", 구간_param);

        // when
        ExtractableResponse<Response> 삭제_응답 = httpDelete("/lines/1/sections?stationId=1");

        // then
        assertThat(삭제_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("중간의 구간을 제거한다.")
    void deleteMiddleSection() {
        // given
        SectionRequest 구간_param = new SectionRequest(2L, 3L, 10);
        httpPost("/lines/1/sections", 구간_param);

        // when
        final ExtractableResponse<Response> 삭제_응답 = httpDelete("/lines/1/sections?stationId=2");

        // then
        assertThat(삭제_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
