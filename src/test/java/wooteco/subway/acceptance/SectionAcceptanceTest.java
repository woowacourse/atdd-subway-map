package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private final StationRequest station = new StationRequest("강남역");
    private final StationRequest station2 = new StationRequest("선릉역");
    private final StationRequest station3 = new StationRequest("구성역");
    private final StationRequest station4 = new StationRequest("양재역");
    private final LineRequest line = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
    private final SectionRequest section = new SectionRequest(1L, 2L, 10);
    private final SectionRequest section2 = new SectionRequest(2L, 3L, 10);

    private final int lineId = 1;

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        //given
        setRequest().body(station).post("/stations");
        setRequest().body(station2).post("/stations");
        setRequest().body(station3).post("/stations");
        setRequest().body(station4).post("/stations");
        setRequest().body(line).post("/lines");

        //when
        ExtractableResponse<Response> response = getResponse(setRequest().body(section2)
                .post("/lines/" + lineId + "/sections"));

        //then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        setRequest().body(station).post("/stations");
        setRequest().body(station2).post("/stations");
        setRequest().body(station3).post("/stations");
        setRequest().body(station4).post("/stations");
        setRequest().body(line).post("/lines");

        setRequest().body(section).post("/lines/" + lineId + "/sections");
        setRequest().body(section2).post("/lines/" + lineId + "/sections");

        // when
        ExtractableResponse<Response> response = getResponse(setRequest()
                .delete("/lines/" + lineId + "/sections?stationId=" + section.getUpStationId().intValue()));

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());
    }
}
