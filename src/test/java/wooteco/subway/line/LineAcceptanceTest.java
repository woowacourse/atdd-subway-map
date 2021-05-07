package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

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
import wooteco.subway.AcceptanceTest;
import wooteco.subway.controller.dto.response.line.LineResponseDto;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        String lineNameToCreate = "신분당선";
        String lineColorToCreate = "bg-red-600";

        // when
        ExtractableResponse<Response> response = requestCreateLineAndGetResponse(lineNameToCreate, lineColorToCreate);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.header("Location")).isNotBlank();
    }

    private ExtractableResponse<Response> requestCreateLineAndGetResponse(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
            .body(params)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        String duplicateLineName = "신분당선";
        requestCreateLineAndGetResponse(duplicateLineName, "bg-red-600");
        Long savedLineIdBeforeRequest = requestAndGetAllSavedLinesIds().get(0);

        // when
        ExtractableResponse<Response> response = requestCreateLineAndGetResponse(duplicateLineName, "bg-green-600");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Long> allSavedLineIds = requestAndGetAllSavedLinesIds();
        assertThat(allSavedLineIds).hasSize(1);
        assertThat(allSavedLineIds).containsExactly(savedLineIdBeforeRequest);
    }

    @DisplayName("기존에 존재하는 색깔로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        String duplicateLineColor = "bg-red-600";
        requestCreateLineAndGetResponse("신분당선", duplicateLineColor);
        Long savedLineIdBeforeRequest = requestAndGetAllSavedLinesIds().get(0);

        // when
        ExtractableResponse<Response> response = requestCreateLineAndGetResponse("2호선", duplicateLineColor);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Long> allSavedLineIds = requestAndGetAllSavedLinesIds();
        assertThat(allSavedLineIds).hasSize(1);
        assertThat(allSavedLineIds).containsExactly(savedLineIdBeforeRequest);
    }

    @DisplayName("Id로 하나의 노선을 조회한다.")
    @Test
    void getOneLineById() {
        // given
        requestCreateLineAndGetResponse("신분당선", "bg-red-600");
        Long savedLineId = requestAndGetAllSavedLinesIds().get(0);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .pathParam("id", savedLineId)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/{id}")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        Long retrievedLineId = response.jsonPath().getObject(".", LineResponseDto.class).getId();
        assertThat(retrievedLineId).isEqualTo(savedLineId);
    }

    @DisplayName("모든 노선들을 조회한다.")
    @Test
    void getAllLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = requestCreateLineAndGetResponse("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse2 = requestCreateLineAndGetResponse("2호선", "bg-green-600");

        List<Long> createdLineIds = Stream.of(createResponse1, createResponse2)
            .map(createResponse -> Long.parseLong(createResponse.header("Location").split("/")[2]))
            .collect(Collectors.toList());

        // when
        List<Long> retrievedLineIds = requestAndGetAllSavedLinesIds();

        // then
        assertThat(retrievedLineIds).containsExactlyInAnyOrderElementsOf(createdLineIds);
    }

    private List<Long> requestAndGetAllSavedLinesIds() {
        return requestAndGetAllSavedLineResponseDtos().stream()
            .map(LineResponseDto::getId)
            .collect(Collectors.toList());
    }

    private List<LineResponseDto> requestAndGetAllSavedLineResponseDtos() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        return response.jsonPath().getList(".", LineResponseDto.class);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        requestCreateLineAndGetResponse("신분당선", "bg-red-600");
        Long lineIdToUpdate = requestAndGetAllSavedLinesIds().get(0);

        // when
        String newLineName = "구분당선";
        String newColor = "bg-blue-600";
        Map<String, String> params = new HashMap<>();
        params.put("name", newLineName);
        params.put("color", newColor);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .pathParam("id", lineIdToUpdate)
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{id}")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponseDto updatedLineResponseDto = requestAndGetAllSavedLineResponseDtos().get(0);
        assertThat(updatedLineResponseDto.getName()).isEqualTo(newLineName);
        assertThat(updatedLineResponseDto.getColor()).isEqualTo(newColor);
    }

    @DisplayName("이미 존재하는 이름으로 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateName() {
        // given
        requestCreateLineAndGetResponse("신분당선", "bg-green-600");

        String oldLineName = "2호선";
        String oldColor = "bg-red-600";
        ExtractableResponse<Response> oldLineResponse = requestCreateLineAndGetResponse(oldLineName, oldColor);
        Long lineIdToUpdate = Long.parseLong(oldLineResponse.header("Location").split("/")[2]);

        // when
        String newLineName = "신분당선";
        String newColor = "bg-blue-600";
        Map<String, String> params = new HashMap<>();
        params.put("name", newLineName);
        params.put("color", newColor);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .pathParam("id", lineIdToUpdate)
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{id}")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        LineResponseDto savedLineResponseDto = requestAndGetAllSavedLineResponseDtos().stream()
            .filter(lineResponseDto -> lineResponseDto.getId().equals(lineIdToUpdate))
            .collect(Collectors.toList())
            .get(0);
        assertThat(savedLineResponseDto.getId()).isEqualTo(lineIdToUpdate);
        assertThat(savedLineResponseDto.getName()).isEqualTo(oldLineName);
        assertThat(savedLineResponseDto.getColor()).isEqualTo(oldColor);
    }

    @DisplayName("이미 존재하는 색깔로 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateColor() {
        // given
        requestCreateLineAndGetResponse("신분당선", "bg-green-600");

        String oldLineName = "2호선";
        String oldColor = "bg-red-600";
        ExtractableResponse<Response> oldLineResponse = requestCreateLineAndGetResponse(oldLineName, oldColor);
        Long lineIdToUpdate = Long.parseLong(oldLineResponse.header("Location").split("/")[2]);

        // when
        String newLineName = "2호선";
        String newColor = "bg-green-600";
        Map<String, String> params = new HashMap<>();
        params.put("name", newLineName);
        params.put("color", newColor);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .pathParam("id", lineIdToUpdate)
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{id}")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        LineResponseDto savedLineResponseDto = requestAndGetAllSavedLineResponseDtos().stream()
            .filter(lineResponseDto -> lineResponseDto.getId().equals(lineIdToUpdate))
            .collect(Collectors.toList())
            .get(0);
        assertThat(savedLineResponseDto.getId()).isEqualTo(lineIdToUpdate);
        assertThat(savedLineResponseDto.getName()).isEqualTo(oldLineName);
        assertThat(savedLineResponseDto.getColor()).isEqualTo(oldColor);
    }

    @DisplayName("노선을 Id로 제거한다.")
    @Test
    void deleteLineById() {
        // given
        requestCreateLineAndGetResponse("신분당선", "bg-red-600");
        requestCreateLineAndGetResponse("2호선", "bg-green-600");
        List<Long> allSavedLineIdsBeforeDelete = requestAndGetAllSavedLinesIds();
        Long lineIdToDelete = allSavedLineIdsBeforeDelete.get(0);
        Long lineIdNotToDelete = allSavedLineIdsBeforeDelete.get(1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/{id}", lineIdToDelete)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        List<Long> allSavedStationIdsAfterDelete = requestAndGetAllSavedLinesIds();
        assertThat(allSavedStationIdsAfterDelete).containsExactly(lineIdNotToDelete);
    }
}