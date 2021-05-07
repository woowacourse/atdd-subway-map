package wooteco.subway.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("지하철 노선 관련 기능")
@Transactional
class LineAcceptanceTest extends AcceptanceTest {
//
//    @DisplayName("노선을 생성한다.")
//    @Test
//    void createLine() {
//        // given, when
//        ExtractableResponse<Response> response = LineRequest.createLineRequest(LineRequest.Line1());
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
//        assertThat(response.header("Location")).isNotBlank();
//        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
//        JsonAndMapContainsTester.test(response.body().asString(), AcceptanceDummyData.getLineParams());
//        System.out.println(response.body().asString());
//    }

//    @DisplayName("기존에 존재하는 노선 이름으로 지하철역을 생성한다.")
//    @Test
//    void createLineWithDuplicateName() {
//        // given
//        FixtureRequest.createLineRequest(FixtureParams.getLineParams());
//
//        // when
//        ExtractableResponse<Response> response = FixtureRequest.createLineRequest(FixtureParams.getLineParams());
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
//    }
//
//    @DisplayName("노선에 목록을 조회한다.")
//    @Test
//    void getLines() {
//        /// given
//        ExtractableResponse<Response> createdResponse1 = LineRequest.createLineRequest(AcceptanceDummyData.getLineParams());
//        ExtractableResponse<Response> createdResponse2 = LineRequest.createLineRequest(AcceptanceDummyData.getLineParams2());
//
//        // when
//        ExtractableResponse<Response> response = RestAssured.given().log().all()
//                .when()
//                .get("/lines")
//                .then().log().all()
//                .extract();
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
//        List<Long> expectedLineIds = Arrays.asList(createdResponse1, createdResponse2).stream()
//                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
//                .collect(Collectors.toList());
//        List<Long> resultLineIds = response.jsonPath().getList(".", LineFindAllResponseDto.class).stream()
//                .map(it -> it.getId())
//                .collect(Collectors.toList());
//        assertThat(resultLineIds).containsAll(expectedLineIds);
//    }
//
//    @DisplayName("노선에 등록된 역을 조회한다.")
//    @Test
//    void getLine() {
//        // given
//        ExtractableResponse<Response> createdResponse = FixtureRequest.createLineRequest(FixtureParams.getLineParams());
//
//        Long expectedLineId = Long.parseLong(createdResponse.header("Location").split("/")[2]);
//
//        // when
//        ExtractableResponse<Response> response = RestAssured.given().log().all()
//                .pathParam("lineId", expectedLineId)
//                .body(FixtureParams.getLineParams())
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when()
//                .get("/lines/{lineId}")
//                .then().log().all()
//                .extract();
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
//        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();
//        assertThat(resultLineId).isEqualTo(expectedLineId);
//    }
//
//    @DisplayName("노선을 수정한다.")
//    @Test
//    void updateLine() {
//        // given
//        ExtractableResponse<Response> createdResponse = FixtureRequest.createLineRequest(FixtureParams.getLineParams());
//        Long expectedLineId = Long.parseLong(createdResponse.header("Location").split("/")[2]);
//
//        // when
//        ExtractableResponse<Response> response = RestAssured.given().log().all()
//                .pathParam("lineId", expectedLineId)
//                .body(getLineParams2())
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when()
//                .put("/lines/{lineId}")
//                .then().log().all()
//                .extract();
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
//    }
//
//    @DisplayName("노선을 제거한다.")
//    @Test
//    void deleteLine() {
//        // given
//        ExtractableResponse<Response> createdResponse = LineRequest.createLineRequest(AcceptanceDummyData.getLineParams());
//
//        // when
//        String uri = createdResponse.header("Location");
//        ExtractableResponse<Response> response = RestAssured.given().log().all()
//                .when()
//                .delete(uri)
//                .then().log().all()
//                .extract();
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
//    }
}