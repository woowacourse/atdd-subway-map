package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.dto.StationRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = saveStation(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        saveStation(stationRequest);

        // when
        ExtractableResponse<Response> response = saveStation(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest gangNamStationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> afterSaveGangNam = saveStation(gangNamStationRequest);
        assertThat(afterSaveGangNam.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        StationRequest jamSilStationRequest = new StationRequest("잠실역");
        ExtractableResponse<Response> afterSaveJamsil = saveStation(jamSilStationRequest);
        assertThat(afterSaveJamsil.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> responseAfterLookUp = lookUpStation();

        // then
        assertThat(responseAfterLookUp.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedIds = Arrays.asList(1L, 2L);
        List<Long> resultStationIds = Stream.of(afterSaveGangNam, afterSaveJamsil)
                .map(response -> Long.parseLong(response.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        assertThat(resultStationIds).containsAll(expectedIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest gangNamStationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> afterSaveGangNam = saveStation(gangNamStationRequest);
        assertThat(afterSaveGangNam.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        String uri = afterSaveGangNam.header("Location");
        ExtractableResponse<Response> deleteAfterGangNam = deleteStation(uri);

        // then
        assertThat(deleteAfterGangNam.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("중복된 역 이름 추가시 예외 처리")
    @Test
    void nameDuplication() {
        // given
        StationRequest gangNamStationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> afterSaveGangNam = saveStation(gangNamStationRequest);
        assertThat(afterSaveGangNam.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> afterSaveDuplicateGangNam = saveStation(gangNamStationRequest);

        // then
        assertThat(afterSaveDuplicateGangNam.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    private ExtractableResponse<Response> saveStation(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteStation(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> lookUpStation() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations/")
                .then().log().all()
                .extract();
    }
}