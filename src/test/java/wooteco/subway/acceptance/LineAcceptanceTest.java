package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.acceptance.template.LineRequest;
import wooteco.subway.acceptance.template.StationRequest;
import wooteco.subway.controller.dto.request.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.SectionRequestDto;
import wooteco.subway.controller.dto.request.StationRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@Transactional
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given, when
        ExtractableResponse<Response> response = LineRequest.createLineRequestAndReturnResponse(new LineCreateRequestDto(
                "1호선",
                "yellow",
                1L,
                2L,
                10
        ));

        // then
        Long lineId = response.jsonPath().getLong("id");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/lines/" + lineId);
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }

    @DisplayName("기존에 존재하는 노선 이름으로 지하철역을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineCreateRequestDto dto = new LineCreateRequestDto(
                "1호선",
                "yellow",
                1L,
                2L,
                10
        );
        LineRequest.createLineRequestAndReturnId(dto);

        // when
        ExtractableResponse<Response> response = LineRequest.createLineRequestAndReturnResponse(dto);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선들의 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Long stationId1 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("강남역"));
        Long stationId2 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("길동역"));
        Long stationId3 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("주안역"));
        Long stationId4 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("홍대역"));

        LineCreateRequestDto dto1 = new LineCreateRequestDto(
                "yellow",
                "1호선",
                stationId1,
                stationId2,
                10
        );
        LineCreateRequestDto dto2 = new LineCreateRequestDto(
                "2호선",
                "red",
                stationId3,
                stationId4,
                10
        );
        Long lineId1 = LineRequest.createLineRequestAndReturnId(dto1);
        Long lineId2 = LineRequest.createLineRequestAndReturnId(dto2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        JsonPath jsonPath = response.jsonPath();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(jsonPath.getLong("[0].id")).isEqualTo(lineId1);
        assertThat(jsonPath.getString("[0].name")).isEqualTo(dto1.getName());
        assertThat(jsonPath.getString("[0].color")).isEqualTo(dto1.getColor());
        assertThat(jsonPath.getLong("[1].id")).isEqualTo(lineId2);
        assertThat(jsonPath.getString("[1].name")).isEqualTo(dto2.getName());
        assertThat(jsonPath.getString("[1].color")).isEqualTo(dto2.getColor());
    }

    @DisplayName("노선에 등록되어있는 역을 조회한다.")
    @Test
    void getLineWithSections() {
        // given

        String stationName1 = "강남역";
        Long stationId1 = StationRequest.createStationRequestAndReturnId(new StationRequestDto(stationName1));
        String stationName2 = "길동역";
        Long stationId2 = StationRequest.createStationRequestAndReturnId(new StationRequestDto(stationName2));

        String lineName = "1호선";
        String lineColor = "yellow";
        Long lineId = LineRequest.createLineRequestAndReturnId(new LineCreateRequestDto(
                lineName,
                lineColor,
                stationId1,
                stationId2,
                10
        ));

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
        JsonPath jsonPath = response.jsonPath();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(jsonPath.getLong("id")).isEqualTo(lineId);
        assertThat(jsonPath.getString("name")).isEqualTo(lineName);
        assertThat(jsonPath.getString("color")).isEqualTo(lineColor);
        assertThat(jsonPath.getLong("stations[0].id")).isEqualTo(stationId1);
        assertThat(jsonPath.getString("stations[0].name")).isEqualTo(stationName1);
        assertThat(jsonPath.getLong("stations[1].id")).isEqualTo(stationId2);
        assertThat(jsonPath.getString("stations[1].name")).isEqualTo(stationName2);
    }

    @DisplayName("노선에 있는 지하철 역을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationRequestDto station1 = new StationRequestDto("주안역");
        StationRequestDto station2 = new StationRequestDto("강남역");
        StationRequestDto station3 = new StationRequestDto("길동역");

        Long stationId1 = StationRequest.createStationRequestAndReturnId(station1);
        Long stationId2 = StationRequest.createStationRequestAndReturnId(station2);
        Long stationId3 = StationRequest.createStationRequestAndReturnId(station3);
        Long lineId = LineRequest.createLineRequestAndReturnId(new LineCreateRequestDto(
                "1호선",
                "yellow",
                stationId1,
                stationId2,
                15
        ));
        LineRequest.createSectionRequestAndReturnResponse(new SectionRequestDto(
                stationId2,
                stationId3,
                10
        ), lineId);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId2)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}