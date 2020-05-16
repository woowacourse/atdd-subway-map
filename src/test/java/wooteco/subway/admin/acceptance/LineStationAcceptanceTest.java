package wooteco.subway.admin.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineStationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        // 노선 추가
        createLine("신분당선");

        List<LineResponse> lines = getLines();
        assertThat(lines.size()).isEqualTo(1);

        // 역 추가
        createStation("서울역");
        createStation("용산역");
        createStation("노량진역");
        createStation("시청역");

        List<StationResponse> stations = getStations();
        assertThat(stations.size()).isEqualTo(4);
        assertThat(stations.get(0).getName()).isEqualTo("서울역");
        assertThat(stations.get(1).getName()).isEqualTo("용산역");
        assertThat(stations.get(2).getName()).isEqualTo("노량진역");
        assertThat(stations.get(3).getName()).isEqualTo("시청역");

        // When 지하철 노선에 지하철 역을 등록하는 요청을 한다.
        // Then 지하철역이 노선에 추가 되었다.
        addLineStation(lines.get(0).getId(), null, stations.get(0).getId());
        addLineStation(lines.get(0).getId(), stations.get(0).getId(), stations.get(1).getId());
        addLineStation(lines.get(0).getId(), stations.get(1).getId(), stations.get(2).getId());

        // When 지하철 노선의 지하철역 목록 조회 요청을 한다.
        // Then 지하철역 목록을 응답 받는다.
        // And 새로 추가한 지하철역을 목록에서 찾는다.
        List<Station> lineStations = getLineStations(lines.get(0).getId());
        assertThat(lineStations.size()).isEqualTo(3);
        assertThat(lineStations.get(0).getId()).isEqualTo(stations.get(0).getId());

        // When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
        // Then 지하철역이 노선에서 제거 되었다.
        deleteLineStation(lines.get(0).getId(), stations.get(1).getId(), stations.get(2).getId());
        lineStations = getLineStations(lines.get(0).getId());
        assertThat(lineStations.size()).isEqualTo(2);

        // When 지하철 노선의 지하철역 목록 조회 요청을 한다.
        // Then 지하철역 목록을 응답 받는다.
        // And 제외한 지하철역이 목록에 존재하지 않는다.

    }

    private void deleteLineStation(final Long lineId, final Long preStationId, final long stationId) {
        given().
                when().
                delete("/lines/" + lineId + "/stations/" + stationId).
                then().
                log().all().
                statusCode(HttpStatus.NO_CONTENT.value());
    }

    private List<Station> getLineStations(final Long lineId) {
        return given().
                when().
                get("/lines/" + lineId + "/stations").
                then().
                log().all().
                extract().
                jsonPath().
                getList(".", Station.class);
    }

    private void addLineStation(final Long lineId, final Long preStationId, final Long stationId) {
        LineStationCreateRequest request = new LineStationCreateRequest(
                preStationId,
                stationId,
                10,
                10
        );

        given().
                body(request).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines/" + lineId + "/stations").
                then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }
}
