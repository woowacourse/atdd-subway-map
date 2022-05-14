package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.Fixture.createLineRequest;
import static wooteco.subway.Fixture.createSectionRequest;
import static wooteco.subway.Fixture.deleteSectionRequest;
import static wooteco.subway.Fixture.makeLineSinBunDangCreationParams;
import static wooteco.subway.Fixture.makeLineTwoCreationParams;
import static wooteco.subway.Fixture.save2StationsRequest;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.response.LineResponseDto;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        final Map<String, String> params = makeLineTwoCreationParams(stationIds.get(0), stationIds.get(1));

        // when
        final ExtractableResponse<Response> response = createLineRequest(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        final Map<String, String> params = makeLineTwoCreationParams(stationIds.get(0), stationIds.get(1));
        createLineRequest(params);

        // when
        final ExtractableResponse<Response> response = createLineRequest(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void showLines() {
        /// given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        final Map<String, String> lineParams1 = makeLineTwoCreationParams(stationIds.get(0), stationIds.get(1));
        final ExtractableResponse<Response> createResponse1 = createLineRequest(lineParams1);

        final Map<String, String> lineParams2 = makeLineSinBunDangCreationParams(stationIds.get(0), stationIds.get(1));
        final ExtractableResponse<Response> createResponse2 = createLineRequest(lineParams2);

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        final List<Long> resultLineIds = response.jsonPath().getList(".", LineResponseDto.class).stream()
                .map(LineResponseDto::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("id 로 노선을 조회한다.")
    @Test
    void showLine() {
        /// given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");

        final Map<String, String> params = makeLineTwoCreationParams(stationIds.get(0), stationIds.get(1));
        final ExtractableResponse<Response> createResponse = createLineRequest(params);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
        final LineResponseDto responseBody = response.jsonPath().getObject(".", LineResponseDto.class);

        // then
        assertAll(
                () -> assertThat(responseBody.getName()).isEqualTo("2호선"),
                () -> assertThat(responseBody.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void modifyLine() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        final Map<String, String> params = makeLineTwoCreationParams(stationIds.get(0), stationIds.get(1));
        final ExtractableResponse<Response> createResponse = createLineRequest(params);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        final Map<String, String> modifyParams = new HashMap<>();
        modifyParams.put("name", "신분당선");
        modifyParams.put("color", "bg-red-600");
        final ExtractableResponse<Response> modifyResponse = RestAssured.given().log().all()
                .body(modifyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void removeLine() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        final Map<String, String> params = makeLineTwoCreationParams(stationIds.get(0), stationIds.get(1));
        final ExtractableResponse<Response> createResponse = createLineRequest(params);

        // when
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("노선에 새로운 구간을 추가한다.")
    void addSection() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        final Map<String, String> lineParams = makeLineTwoCreationParams(stationIds.get(0), stationIds.get(1));
        final ExtractableResponse<Response> lineCreateResponse = createLineRequest(lineParams);
        final LineResponseDto createdLine = lineCreateResponse.jsonPath()
                .getObject(".", LineResponseDto.class);
        final List<Long> newStationIds = save2StationsRequest("삼성역", "봉은사역");

        // when
        final Map<String, String> sectionParams = new HashMap<>();
        sectionParams.put("upStationId", stationIds.get(0).toString());
        sectionParams.put("downStationId", newStationIds.get(0).toString());
        sectionParams.put("distance", "5");
        final ExtractableResponse<Response> response = createSectionRequest(createdLine.getId(), sectionParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선에서 역을 삭제한다.")
    void removeStation() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        final Map<String, String> lineParams = makeLineTwoCreationParams(stationIds.get(0), stationIds.get(1));
        final ExtractableResponse<Response> lineCreateResponse = createLineRequest(lineParams);
        final LineResponseDto createdLine = lineCreateResponse.jsonPath()
                .getObject(".", LineResponseDto.class);

        final Long newStationId = save2StationsRequest("삼성역", "봉은사역").get(0);

        final Map<String, String> sectionParams = new HashMap<>();
        sectionParams.put("upStationId", stationIds.get(0).toString());
        sectionParams.put("downStationId", newStationId.toString());
        sectionParams.put("distance", "5");
        createSectionRequest(createdLine.getId(), sectionParams);

        // when
        final ExtractableResponse<Response> response = deleteSectionRequest(
                createdLine.getId(),
                newStationId,
                sectionParams
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
