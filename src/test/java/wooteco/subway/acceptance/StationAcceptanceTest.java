package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.station.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> responseCreateStation;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        responseCreateStation = insertStation("테스트역");
    }

    private ExtractableResponse<Response> insertStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return requestCreate("/stations", params);
    }

    @DisplayName("새로운 지하철역을 생성한다.")
    @Test
    void createStation() {
        assertAll(
                () -> assertThat(responseCreateStation.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(responseCreateStation.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 생성시 400에러 발생.")
    @Test
    void createStationWithDuplicateName() {
        var response = insertStation("테스트역");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        var responseCreateStation2 = insertStation("테스트2역");

        // when
        var response = requestGet("/stations");

        // then
        var ids = getIds(response);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(ids).contains(getId(responseCreateStation)),
                () -> assertThat(ids).contains(getId(responseCreateStation2))
        );
    }


    private List<String> getIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        var response = requestDelete("/stations/" + getId(responseCreateStation));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
