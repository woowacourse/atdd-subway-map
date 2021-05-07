package wooteco.subway.station;

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
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.controller.dto.response.station.StationResponseDto;

@DisplayName("지하철역 관련 기능")
@Transactional
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        String stationNameToCreate = "강남역";

        // when
        ExtractableResponse<Response> response = requestCreateStationWithNameAndGetResponse(stationNameToCreate);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.header("Location")).isNotBlank();
    }

    private ExtractableResponse<Response> requestCreateStationWithNameAndGetResponse(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String duplicateStationName = "강남역";
        requestCreateStationWithNameAndGetResponse(duplicateStationName);
        Long savedStationIdBeforeRequest = requestAndGetAllSavedStationIds().get(0);

        // when
        ExtractableResponse<Response> response = requestCreateStationWithNameAndGetResponse(duplicateStationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Long> allSavedStationIds = requestAndGetAllSavedStationIds();
        assertThat(allSavedStationIds).hasSize(1);
        assertThat(allSavedStationIds).containsExactly(savedStationIdBeforeRequest);
    }

    @DisplayName("모든 지하철역들을 조회한다.")
    @Test
    void getAllStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = requestCreateStationWithNameAndGetResponse("강남역");
        ExtractableResponse<Response> createResponse2 = requestCreateStationWithNameAndGetResponse("역삼역");

        List<Long> createdStationIds = Stream.of(createResponse1, createResponse2)
            .map(createResponse -> Long.parseLong(createResponse.header("Location").split("/")[2]))
            .collect(Collectors.toList());

        // when
        List<Long> retrievedStationIds = requestAndGetAllSavedStationIds();

        // then
        assertThat(retrievedStationIds).containsExactlyInAnyOrderElementsOf(createdStationIds);
    }

    private List<Long> requestAndGetAllSavedStationIds() {
        return requestAndGetAllSavedStationResponseDtos().stream()
            .map(StationResponseDto::getId)
            .collect(Collectors.toList());
    }

    private List<StationResponseDto> requestAndGetAllSavedStationResponseDtos() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/stations")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        return response.jsonPath().getList(".", StationResponseDto.class);
    }

    @DisplayName("지하철역을 Id로 제거한다.")
    @Test
    void deleteStationById() {
        // given
        requestCreateStationWithNameAndGetResponse("강남역");
        requestCreateStationWithNameAndGetResponse("역삼역");
        List<Long> allSavedStationIdsBeforeDelete = requestAndGetAllSavedStationIds();
        Long stationIdToDelete = allSavedStationIdsBeforeDelete.get(0);
        Long stationIdNotToDelete = allSavedStationIdsBeforeDelete.get(1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/stations/{id}", stationIdToDelete)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        List<Long> allSavedStationIdsAfterDelete = requestAndGetAllSavedStationIds();
        assertThat(allSavedStationIdsAfterDelete).containsExactly(stationIdNotToDelete);
    }
}
