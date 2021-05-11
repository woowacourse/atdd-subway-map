package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.acceptance.template.LineRequest;
import wooteco.subway.acceptance.template.SectionRequest;
import wooteco.subway.acceptance.template.StationRequest;
import wooteco.subway.controller.dto.request.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.SectionRequestDto;
import wooteco.subway.controller.dto.request.StationRequestDto;

public class SectionCreateAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선의 하행선 종점에 구간을 추가하고, 순서대로 잘 등록 됐는 지 테스트")
    @Test
    void createSection_하행성_종점() {
        // given
        Long stationId1 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("강남역"));
        Long stationId2 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("길동역"));
        Long stationId3 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("주안역"));

        Long lineId = LineRequest.createLineRequestAndReturnId(new LineCreateRequestDto(
                "1호선",
                "yellow",
                stationId1,
                stationId2,
                10
        ));

        // when
        ExtractableResponse<Response> createSectionResponse = SectionRequest.createSectionRequestAndReturnResponse(
                new SectionRequestDto(stationId2, stationId3, 10),
                lineId
        );

        // then
        JsonPath getLineJson = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract()
                .jsonPath();

        assertThat(createSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationId1).isEqualTo(getLineJson.getLong("stations[0].id"));
        assertThat(stationId2).isEqualTo(getLineJson.getLong("stations[1].id"));
        assertThat(stationId3).isEqualTo(getLineJson.getLong("stations[2].id"));
    }

    @DisplayName("지하철 노선의 상행선 종점에 구간을 추가하고, 순서대로 잘 등록 됐는 지 테스트")
    @Test
    void createSection_상행선_종점() {
        // given
        Long stationId1 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("강남역"));
        Long stationId2 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("길동역"));
        Long stationId3 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("주안역"));

        Long lineId = LineRequest.createLineRequestAndReturnId(new LineCreateRequestDto(
                "1호선",
                "yellow",
                stationId2,
                stationId3,
                10
        ));

        // when
        ExtractableResponse<Response> createSectionResponse = SectionRequest.createSectionRequestAndReturnResponse(
                new SectionRequestDto(stationId1, stationId2, 10),
                lineId
        );

        // then
        JsonPath getLineJson = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract()
                .jsonPath();

        assertThat(createSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationId1).isEqualTo(getLineJson.getLong("stations[0].id"));
        assertThat(stationId2).isEqualTo(getLineJson.getLong("stations[1].id"));
        assertThat(stationId3).isEqualTo(getLineJson.getLong("stations[2].id"));
    }

    @DisplayName("지하철 라인 중간에 구간을 추가하고, 순서대로 잘 등록 됐는 지 테스트")
    @Test
    void createSection_라인_중간() {
        // given
        Long stationId1 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("강남역"));
        Long stationId2 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("길동역"));
        Long stationId3 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("주안역"));

        Long lineId = LineRequest.createLineRequestAndReturnId(new LineCreateRequestDto(
                "1호선",
                "yellow",
                stationId1,
                stationId3,
                10
        ));

        // when
        ExtractableResponse<Response> createSectionResponse = SectionRequest.createSectionRequestAndReturnResponse(
                new SectionRequestDto(stationId1, stationId2, 5),
                lineId
        );

        // then
        JsonPath getLineJson = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract()
                .jsonPath();

        assertThat(createSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationId1).isEqualTo(getLineJson.getLong("stations[0].id"));
        assertThat(stationId2).isEqualTo(getLineJson.getLong("stations[1].id"));
        assertThat(stationId3).isEqualTo(getLineJson.getLong("stations[2].id"));
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 예외 발생")
    @Test
    void createSection_상행역_하행역_이미_등록_throwException() {
        // given
        Long stationId1 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("강남역"));
        Long stationId2 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("길동역"));

        Long lineId = LineRequest.createLineRequestAndReturnId(new LineCreateRequestDto(
                "1호선",
                "yellow",
                stationId1,
                stationId2,
                10
        ));

        // when
        ExtractableResponse<Response> createSectionResponse = SectionRequest.createSectionRequestAndReturnResponse(
                new SectionRequestDto(stationId1, stationId2, 5),
                lineId
        );

        // then
        assertThat(createSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 라인에 하나라도 포함되어 있지 않다면 예외 발생")
    @Test
    void createSection_상행역_하행역_등록_X_throwException() {
        // given
        Long stationId1 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("강남역"));
        Long stationId2 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("길동역"));
        Long stationId3 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("주안역"));
        Long stationId4 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("홍대역"));

        Long lineId = LineRequest.createLineRequestAndReturnId(new LineCreateRequestDto(
                "1호선",
                "yellow",
                stationId1,
                stationId2,
                10
        ));

        // when
        ExtractableResponse<Response> createSectionResponse = SectionRequest.createSectionRequestAndReturnResponse(
                new SectionRequestDto(stationId3, stationId4, 5),
                lineId
        );

        // then
        assertThat(createSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void createSection_길이_검증_throwException() {
        // given
        Long stationId1 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("강남역"));
        Long stationId2 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("길동역"));
        Long stationId3 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("주안역"));

        Long lineId = LineRequest.createLineRequestAndReturnId(new LineCreateRequestDto(
                "1호선",
                "yellow",
                stationId1,
                stationId3,
                10
        ));

        // given, when
        ExtractableResponse<Response> response1 = validateDistance(stationId1, stationId2, lineId, 10);
        ExtractableResponse<Response> response2 = validateDistance(stationId1, stationId2, lineId, 11);
        ExtractableResponse<Response> response3 = validateDistance(stationId1, stationId2, lineId, 9);

        // then
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response3.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    ExtractableResponse<Response> validateDistance(Long stationId1, Long stationId2, Long lineId, int newSectionDistance) {
        // when
        return SectionRequest.createSectionRequestAndReturnResponse(
                new SectionRequestDto(stationId1, stationId2, newSectionDistance),
                lineId
        );
    }
}
