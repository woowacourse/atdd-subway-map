package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private StationResponse response;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        response = 강남역_response.as(StationResponse.class);
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        assertThat(response.getName()).isEqualTo("강남역");
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        ExtractableResponse<Response> duplicateResponse = postStation("강남역");

        assertThat(duplicateResponse.statusCode())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void showStations() {
        ExtractableResponse<Response> response = getStations();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> resultLineNames = response.jsonPath().getList(".", StationResponse.class)
            .stream()
            .map(StationResponse::getName)
            .collect(Collectors.toList());
        assertThat(resultLineNames).hasSize(3);
        assertThat(resultLineNames).containsExactly("강남역", "역삼역", "도곡역");
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        Long id = response.getId();
        ExtractableResponse<Response> actualResponse = deleteStation(id);

        assertThat(actualResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
