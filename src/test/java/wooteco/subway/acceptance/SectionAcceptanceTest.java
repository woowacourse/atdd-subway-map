package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        ExtractableResponse<Response> stationResponse1 = post("/stations", new StationRequest("강남역"));
        ExtractableResponse<Response> stationResponse2 = post("/stations", new StationRequest("잠실역"));
        ExtractableResponse<Response> stationResponse3 = post("/stations", new StationRequest("선릉역"));
        Long upStationId = getId(stationResponse1);
        Long downStationId = getId(stationResponse2);

        ExtractableResponse<Response> response = post("/lines",
                new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10));
        Long lineId = getId(response);

        ExtractableResponse<Response> sectionResponse = post("/lines/" + lineId + "/sections",
                new SectionRequest(upStationId, getId(stationResponse3), 4));

        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("상행역 하행역이 기존에 존재하는 것과 모두 일치하면 예외가 발생한다.")
    @Test
    void addSameSection() {
        ExtractableResponse<Response> stationResponse1 = post("/stations", new StationRequest("강남역"));
        ExtractableResponse<Response> stationResponse2 = post("/stations", new StationRequest("잠실역"));
        Long upStationId = getId(stationResponse1);
        Long downStationId = getId(stationResponse2);

        ExtractableResponse<Response> response = post("/lines",
                new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10));
        Long lineId = getId(response);

        ExtractableResponse<Response> sectionResponse = post("/lines/" + lineId + "/sections",
                new SectionRequest(upStationId, downStationId, 10));

        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간에서 삭제한다.")
    @Test
    void deleteStationInSection() {
        ExtractableResponse<Response> stationResponse1 = post("/stations", new StationRequest("강남역"));
        ExtractableResponse<Response> stationResponse2 = post("/stations", new StationRequest("잠실역"));
        ExtractableResponse<Response> stationResponse3 = post("/stations", new StationRequest("선릉역"));

        Long upStationId = getId(stationResponse1);
        Long downStationId = getId(stationResponse2);
        Long newId = getId(stationResponse3);

        ExtractableResponse<Response> response = post("/lines",
                new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 15));
        Long lineId = getId(response);

        post("/lines/" + lineId + "/sections",
                new SectionRequest(upStationId, newId, 10));

        ExtractableResponse<Response> deleteResponse = delete("/lines/" + lineId + "/sections?stationId=" + downStationId);

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
