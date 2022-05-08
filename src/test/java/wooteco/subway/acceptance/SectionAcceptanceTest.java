package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.Map;
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
    private final SectionRequest section = new SectionRequest(1L,2L,10);
    private final SectionRequest section2 = new SectionRequest(2L,3L,10);

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
        ExtractableResponse<Response> response = getResponse(setRequest().body(section)
                .post("/lines/"+ lineId + "/sections"));

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

        setRequest().body(section).post("/lines/"+ lineId + "/sections");
        setRequest().body(section2).post("/lines/"+ lineId + "/sections");


        // when
        ExtractableResponse<Response> response = getResponse(setRequest()
                .delete("/lines/"+ lineId + "/sections?stationId=" + section.getUpStationId().intValue()));

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());
    }
    //    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    //    @Test
    //    void createStationWithDuplicateName() {
    //        // given
    //        setRequest(station).post("/stations");
    //
    //        // when
    //        ExtractableResponse<Response> response = getResponse(setRequest(station).post("/stations"));
    //
    //        // then
    //        assertThat(response.statusCode())
    //                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    //        assertThat(response.body().asString())
    //                .isEqualTo("이미 해당 이름의 역이 있습니다.");
    //    }
    //
    //    @DisplayName("지하철역을 조회한다.")
    //    @Test
    //    void getStations() {
    //        /// given
    //        Map<String, String> station2 = getSection("역삼역");
    //        String uri1 = getResponse(setRequest(station).post("/stations"))
    //                .header("Location");
    //
    //        String uri2 = getResponse(setRequest(station2).post("/stations"))
    //                .header("Location");
    //
    //        List<Long> expectedLineIds = Stream.of(uri1, uri2)
    //                .map(it -> Long.parseLong(it.split("/")[2]))
    //                .collect(Collectors.toList());
    //
    //        // when
    //        ExtractableResponse<Response> response = getResponse(setRequest(station2).get("/stations"));
    //
    //        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class)
    //                .stream()
    //                .map(StationResponse::getId)
    //                .collect(Collectors.toList());
    //
    //        // then
    //        assertThat(response.statusCode())
    //                .isEqualTo(HttpStatus.OK.value());
    //        assertThat(resultLineIds)
    //                .containsAll(expectedLineIds);
    //    }
    //
//
//    @DisplayName("존재하지 않는 지하철역을 제거한다.")
//    @Test
//    void deleteStation_error() {
//        ExtractableResponse<Response> response = getResponse(setRequest().delete("/stations/100"));
//        assertThat(response.statusCode())
//                .isEqualTo(HttpStatus.BAD_REQUEST.value());
//        assertThat(response.body().asString())
//                .isEqualTo("해당 아이디의 역이 없습니다.");
//    }
//


    private RequestSpecification setRequest() {
        return RestAssured.given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE);
    }
    private ExtractableResponse<Response> getResponse(Response response) {
        return response.then().log().all().extract();
    }
}
