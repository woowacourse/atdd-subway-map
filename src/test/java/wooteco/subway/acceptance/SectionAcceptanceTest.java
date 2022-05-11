package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class SectionAcceptanceTest extends AcceptanceTest {

    private Long createdLineId;
    private Long createdStationId1;
    private Long createdStationId2;

    @BeforeEach
    void createLineAndStations() {
        createdStationId1 = AcceptanceUtil.createStation("선릉역");
        createdStationId2 = AcceptanceUtil.createStation("잠실역");
        createdLineId = AcceptanceUtil.createLine("2호선", "bg-red-600", createdStationId1, createdStationId2,
                10);
    }

    @Disabled
    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        // given
        Long lineId = createdLineId;
        Long upStationId = createdStationId1;
        Long downStationId = createdStationId2;
        Integer distance = 10;

        // when
        ExtractableResponse<Response> response = requestCreateSection(lineId, upStationId, downStationId, distance);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    private ExtractableResponse<Response> requestCreateSection(Long lineId, Long upStationId, Long downStationId,
                                                               Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("lineId", lineId.toString());
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", distance.toString());

        return AcceptanceUtil.postRequest(params, "/stations");
    }
}

