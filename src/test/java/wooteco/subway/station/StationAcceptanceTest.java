package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.dto.station.response.StationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.station.StationAcceptanceTestUtils.*;

@DisplayName("지하철역 관련 기능")
@Sql("classpath:tableInit.sql")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        String 강남역 = "강남역";

        // when
        ExtractableResponse<Response> response = createStationWithName(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.header("Location")).isEqualTo("/stations/1");

        assertThat(response.body().jsonPath().getString("id")).isEqualTo("1");
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(강남역);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String 강남역 = "강남역";
        createStationWithName(강남역);
        Long 강남역_id = requestAndGetAllStationIds().get(0);

        // when
        ExtractableResponse<Response> response = createStationWithName(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("강남역이 이미 존재합니다.");

        List<Long> allSavedStationIds = requestAndGetAllStationIds();
        assertThat(allSavedStationIds).hasSize(1);
        assertThat(allSavedStationIds).containsExactly(강남역_id);
    }

    @DisplayName("이름이 null일 때 지하철역을 생성한다.")
    @Test
    void createStationWithNullName() {
        // given
        String nullName = null;

        // when
        ExtractableResponse<Response> response = createStationWithName(nullName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("올바르지 않은 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithWrongName() {
        // given
        String wrongName = "강남";

        // when
        ExtractableResponse<Response> response = createStationWithName(wrongName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        String 강남역 = "강남역";
        String 역삼역 = "역삼역";
        createStationWithName(강남역);
        createStationWithName(역삼역);

        // when
        List<StationResponse> stationResponses = getAllStationResponse();

        // then
        StationResponse firstStationResponseDto = stationResponses.get(0);
        assertThat(firstStationResponseDto.getId()).isEqualTo(1L);
        assertThat(firstStationResponseDto.getName()).isEqualTo(강남역);

        StationResponse secondStationResponseDto = stationResponses.get(1);
        assertThat(secondStationResponseDto.getId()).isEqualTo(2L);
        assertThat(secondStationResponseDto.getName()).isEqualTo(역삼역);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        //given
        createStationWithName("강남역");
        createStationWithName("역삼역");
        List<Long> allSavedStationIdsBeforeDelete = requestAndGetAllStationIds();
        Long 강남역_id = allSavedStationIdsBeforeDelete.get(0);
        Long 역삼역_id = allSavedStationIdsBeforeDelete.get(1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/stations/{id}", 강남역_id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        List<Long> allSavedStationIdsAfterDelete = requestAndGetAllStationIds();
        assertThat(allSavedStationIdsAfterDelete).containsExactly(역삼역_id);
    }
}
