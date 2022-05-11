package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void set() {
        StationRequest upStationRequest = new StationRequest("강남역");
        StationAcceptanceTest.postStations(upStationRequest);
        StationRequest downStationRequest = new StationRequest("선릉역");
        StationAcceptanceTest.postStations(downStationRequest);
    }

    private ExtractableResponse<Response> postSections(SectionRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();
    }

    @Test
    @DisplayName("노선을 생성한다.")
    void createLine() {
        // given
        SectionRequest request = new SectionRequest(1L, 2L, 5);

        // when
        ExtractableResponse<Response> response = postSections(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("등록시 기존 역 사이 길이와 같으면 예외를 반환한다")
    void createLine_sameDistance() {
        // given
        SectionRequest request1 = new SectionRequest(1L, 2L, 5);
        postSections(request1);

        SectionRequest request2 = new SectionRequest(1L, 3L, 3);

        // when
        ExtractableResponse<Response> response = postSections(request2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
