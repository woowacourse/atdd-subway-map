package wooteco.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.Rest;
import wooteco.subway.line.LineResponse;
import wooteco.subway.station.StationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUpStations() {
        // 강남역 추가
        Map<String, Object> params = new HashMap<>();
        params.put("name", "강남역");
        Rest.post(params, "/stations");

        // 잠실역 추가
        Map<String, Object> params2 = new HashMap<>();
        params2.put("name", "잠실역");
        Rest.post(params2, "/stations");

        // 역삼역 추가
        Map<String, Object> params3 = new HashMap<>();
        params3.put("name", "역삼역");
        Rest.post(params3, "/stations");

        // 2호선 추가
        Map<String, Object> paramLine = new HashMap<>();
        paramLine.put("name", "2호선");
        paramLine.put("color", "red");
        paramLine.put("upStationId", 1L);
        paramLine.put("downStationId", 2L);
        paramLine.put("distance", 1000);


        Rest.post(paramLine, "/lines");
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", 2L);
        params.put("upStationId", 3L);
        params.put("distance", 10);

        ExtractableResponse<Response> response = Rest.post(params, "/lines/1/sections");
        ExtractableResponse<Response> lineResponse = Rest.get("/lines/1");

        LineResponse lineResponses = lineResponse.jsonPath().getObject(".", LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(lineResponses.getStations().size()).isEqualTo(3);
    }

    @DisplayName("구간 추가시 이미 존재하는 구간인 경우 예외 발생")
    @Test
    void addSectionException() {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 1L);
        params.put("downStationId", 2L);
        params.put("distance", 10);

        ExtractableResponse<Response> response = Rest.post(params, "/lines/1/sections");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @DisplayName("노선 조회시 구간에 포함된 역 목록을 보여준다")
    @Test
    void showLineInfo() {
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", 2L);
        params.put("upStationId", 3L);
        params.put("distance", 10);

        Rest.post(params, "/lines/1/sections");
        ExtractableResponse<Response> lineResponse = Rest.get("/lines/1");
        LineResponse lineResponses = lineResponse.jsonPath().getObject(".", LineResponse.class);
        List<StationResponse> stations = lineResponses.getStations();

        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations.get(0).getName()).isEqualTo("강남역");
        assertThat(stations.get(1).getName()).isEqualTo("역삼역");
        assertThat(stations.get(2).getName()).isEqualTo("잠실역");
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void deleteSection() {
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", 2L);
        params.put("upStationId", 3L);
        params.put("distance", 10);

        Rest.post(params, "/lines/1/sections");
        ExtractableResponse<Response> response = Rest.delete("/lines/1/sections?stationId=2");
        ExtractableResponse<Response> lineResponse = Rest.get("/lines/1");
        LineResponse lineResponses = lineResponse.jsonPath().getObject(".", LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(lineResponses.getStations().size()).isEqualTo(2);
    }

    @DisplayName("구간이 하나뿐일 경우 더이상 역을 삭제할수 없다")
    @Test
    void deleteSectionException() {
        ExtractableResponse<Response> response = Rest.delete("/lines/1/sections?stationId=2");
        ExtractableResponse<Response> lineResponse = Rest.get("/lines/1");
        LineResponse lineResponses = lineResponse.jsonPath().getObject(".", LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        assertThat(lineResponses.getStations().size()).isEqualTo(2);
    }


}
