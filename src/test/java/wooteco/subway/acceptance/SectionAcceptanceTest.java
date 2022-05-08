package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.section.SectionRequest;
import wooteco.subway.dto.station.StationRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private static final String LINE_NAME = "2호선";
    private static final String LINE_COLOR = "bg-green-600";

    private Station yeoksam;
    private Station seolleung;
    private Station samseong;

    @BeforeEach
    void setUpData() {
        yeoksam = createStation(new StationRequest("역삼역")).as(Station.class);
        seolleung = createStation(new StationRequest("선릉역")).as(Station.class);
        samseong = createStation(new StationRequest("삼성역")).as(Station.class);
    }

    @Test
    @DisplayName("기존 구간 사이에 새로운 구간을 등록한다.")
    void CreateSection() {
        // given
        final SectionRequest request = new SectionRequest(yeoksam.getId(), seolleung.getId(), 7);
        final long lineId = createAndGetLineId(new LineRequest(
                LINE_NAME,
                LINE_COLOR,
                yeoksam.getId(),
                samseong.getId(),
                10
        ));

        final LineResponse expectedResponse = LineResponse.of(
                new Line(lineId, LINE_NAME, LINE_COLOR),
                List.of(this.yeoksam, seolleung, samseong)
        );

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(LINE_PATH_PREFIX + SLASH + lineId + SECTION_PATH_PREFIX)
                .then().log().all()
                .extract();

        final LineResponse actualResponse = findLineById(lineId);

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    private LineResponse findLineById(final long lineId) {
        return RestAssured.given().log().all()
                .get(LINE_PATH_PREFIX + SLASH + lineId)
                .then().log().all()
                .extract()
                .as(LineResponse.class);
    }
}
