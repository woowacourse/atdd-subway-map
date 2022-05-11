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
    @DisplayName("상행종점을 추가한다.")
    void addUpTerminus() {
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

}
