package wooteco.subway.admin.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest extends AcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    /**
     * Given 지하철역이 여러 개 추가되어있다.
     * And 지하철 노선이 추가되어있다.
     * <p>
     * When 지하철 노선에 지하철역을 등록하는 요청을 한다.
     * Then 지하철역이 노선에 추가 되었다.
     * <p>
     * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     * Then 지하철역 목록을 응답 받는다.
     * And 새로 추가한 지하철역을 목록에서 찾는다.
     * <p>
     * When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
     * Then 지하철역이 노선에서 제거 되었다.
     * <p>
     * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     * Then 지하철역 목록을 응답 받는다.
     * And 제외한 지하철역이 목록에 존재하지 않는다.
     */
    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        //지하철역과 노선이 추가되어있다
        createLine("1호선");
        createStation("유안역");
        createStation("디디역");

        //지하철 노선에 지하철역을 등록하는 요청을 한다
        createLineStation(lineOne().getId(), null, "유안역");
        createLineStation(lineOne().getId(), "유안역", "디디역");

        //지하철 노선의 지하철역 목록 조회 요청을 한다
        List<Station> stations = lineOne().getStations();

        //지하철역이 노선에 추가 되었다
        assertThat(stations.size()).isEqualTo(2);
        //새로 추가한 지하철역을 목록에서 찾았다
        assertThat(stations.stream()
                .anyMatch(station -> station.getId().equals(lineOne().getId())))
                .isTrue();

        //지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다
        Station yuanStation = stations.get(0);
        deleteLineStation(lineOne().getId(), yuanStation.getId());

        //지하철 노선의 지하철역 목록 조회 요청을 한다
        List<LineResponse> linesAfterDelete = getLines();
        List<Station> stationsAfterDelete = linesAfterDelete.get(0).getStations();

        //지하철 역이 노선에서 제거되었다
        assertThat(stationsAfterDelete.size()).isEqualTo(1);
        //제외한 지하철 역이 목록에 존재하지 않는다
        assertThat(stationsAfterDelete.stream()
                .anyMatch(station -> station.getId().equals(yuanStation.getId())))
                .isFalse();
    }

    private LineResponse lineOne() {
        return getLines().get(0);
    }
}
