package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.StationService;

public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    @Test
    @DisplayName("구간 등록하기")
    void save() {
        // given
        StationResponse gangnam = stationService.create(new StationRequest("강남"));
        StationResponse nowon = stationService.create(new StationRequest("노원"));
        StationResponse jamsil = stationService.create(new StationRequest("잠실"));

        LineResponse line1 = lineService.save(new LineRequest("1호선", "red", nowon.getId(), gangnam.getId(), 10));
        SectionRequest request = new SectionRequest(jamsil.getId(), gangnam.getId(), 5);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + line1.getId() + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
