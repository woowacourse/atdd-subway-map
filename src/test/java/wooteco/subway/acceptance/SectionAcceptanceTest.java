package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
@Transactional
public class SectionAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("구간 추가")
    void addSection() {
        // given
        Long station1 = postStationAndGetId("선릉역");
        Long station2 = postStationAndGetId("강남역");
        Long station3 = postStationAndGetId("구의역");
        Long line = postLineAndGetId("2호선", "green", station1, station2, 5);

        //when
        Map<String, String> param = Map.of(
                "upStationId", String.valueOf(station3),
                "downStationId", String.valueOf(station1),
                "distance", "10"
        );
        ExtractableResponse<Response> response = post("/lines/" + line + "/sections", param);

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("이미 존재하는 구간이라면 예외 발생")
    void alreadyAdded() {
        // given
        Long station1 = postStationAndGetId("선릉역");
        Long station2 = postStationAndGetId("강남역");
        Long line = postLineAndGetId("2호선", "green", station1, station2, 5);

        //when
        Map<String, String> param = Map.of(
                "upStationId", String.valueOf(station1),
                "downStationId", String.valueOf(station2),
                "distance", "2"
        );
        ExtractableResponse<Response> response = post("/lines/" + line + "/sections", param);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("추가할 수 있는 구간이 없다면 예외 발생")
    void noSection() {
        // given
        Long station1 = postStationAndGetId("선릉역");
        Long station2 = postStationAndGetId("강남역");
        Long line = postLineAndGetId("2호선", "green", station1, station2, 5);

        //when
        Map<String, String> param = Map.of(
                "upStationId", "100",
                "downStationId", "101",
                "distance", "2"
        );
        ExtractableResponse<Response> response = post("/lines/" + line + "/sections", param);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("추가할 수 있는 구간 거리가 원래 구간보다 크다면 예외 발생")
    void tooLongDistance() {
        // given
        Long station1 = postStationAndGetId("선릉역");
        Long station2 = postStationAndGetId("강남역");
        Long station3 = postStationAndGetId("구의역");
        Long line = postLineAndGetId("2호선", "green", station1, station2, 5);

        //when
        Map<String, String> param = Map.of(
                "upStationId", String.valueOf(station1),
                "downStationId", String.valueOf(station3),
                "distance", "5"
        );
        ExtractableResponse<Response> response = post("/lines/" + line + "/sections", param);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("구간 삭제")
    void delete() {
        // given
        Long station1 = postStationAndGetId("선릉역");
        Long station2 = postStationAndGetId("강남역");
        Long station3 = postStationAndGetId("구의역");
        Long line = postLineAndGetId("2호선", "green", station1, station2, 5);
        Map<String, String> param = Map.of(
                "upStationId", String.valueOf(station2),
                "downStationId", String.valueOf(station3),
                "distance", "5"
        );
        post("/lines/" + line + "/sections", param);

        //when
        ExtractableResponse<Response> response = delete("/lines/" + line + "/sections?stationId=" + station2);

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("구간정보가 하나 뿐이라면 삭제 불가능 예외 발생")
    void cantDelete() {
        // given
        Long station1 = postStationAndGetId("선릉역");
        Long station2 = postStationAndGetId("강남역");
        Long line = postLineAndGetId("2호선", "green", station1, station2, 5);

        //when
        ExtractableResponse<Response> response = delete("/lines/" + line + "/sections?stationId=" + station2);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("삭제하려는 역이 구간에 존재하지 않다면 예외 발생")
    void notExistStation() {
        // given
        Long station1 = postStationAndGetId("선릉역");
        Long station2 = postStationAndGetId("강남역");
        Long station3 = postStationAndGetId("구의역");
        Long line = postLineAndGetId("2호선", "green", station1, station2, 5);
        Map<String, String> param = Map.of(
                "upStationId", String.valueOf(station2),
                "downStationId", String.valueOf(station3),
                "distance", "5"
        );
        post("/lines/" + line + "/sections", param);

        // when
        ExtractableResponse<Response> response = delete("/lines/" + line + "/sections?stationId=" + 10);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }
}
