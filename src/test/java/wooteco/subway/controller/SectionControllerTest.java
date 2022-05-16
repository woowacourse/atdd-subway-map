package wooteco.subway.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.acceptance.AcceptanceTest;
import wooteco.subway.dto.SectionRequest;

class SectionControllerTest extends AcceptanceTest {

    @DisplayName("구간을 저장한다")
    @Test
    void createSection_success() {
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10L);

        RestAssured.
                given().log().all().
                    body(sectionRequest).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines/{lineId}/sections", 1).
                then().log().all().
                    statusCode(HttpStatus.OK.value());
    }

    @DisplayName("구간을 제거한다")
    @Test
    void deleteSection_success() {
        RestAssured.
                given().log().all().
                when().
                    delete("/lines/{lineId}/sections?stationId=3", 1).
                then().log().all().
                    statusCode(HttpStatus.OK.value());
    }
}
