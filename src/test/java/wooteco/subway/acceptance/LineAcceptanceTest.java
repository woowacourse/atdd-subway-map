package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.Fixtures.getStation;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private final LineRequest line = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
    private final LineRequest line2 = new LineRequest("분당선", "bg-green-600", 3L, 4L, 10);

    private final StationRequest station = new StationRequest("강남역");
    private final StationRequest station2 = new StationRequest("선릉역");
    private final StationRequest station3 = new StationRequest("구성역");
    private final StationRequest station4 = new StationRequest("양재역");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        setRequest().body(station).post("/stations");
        setRequest().body(station2).post("/stations");
        LineResponse lineResponse = new LineResponse(1L, "신분당선", "bg-red-600",
                List.of(getStation(1L, station.toEntity()), getStation(2L, station2.toEntity())));

        //when
        ExtractableResponse<Response> response = getResponse(setRequest().body(line).post("/lines"));

        //then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location"))
                .isNotBlank();
        assertThat(response.body().as(LineResponse.class))
                .isEqualTo(lineResponse);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        setRequest().body(line).post("/lines");

        // when
        ExtractableResponse<Response> response = getResponse(setRequest().body(line).post("/lines"));

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
                .isEqualTo("이미 해당 이름의 노선이 있습니다.");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        setRequest().body(station).post("/stations");
        setRequest().body(station2).post("/stations");
        setRequest().body(station3).post("/stations");
        setRequest().body(station4).post("/stations");

        String uri1 = getResponse(setRequest().body(line).post("/lines")).header("Location");
        String uri2 = getResponse(setRequest().body(line2).post("/lines")).header("Location");

        List<Long> expectedLineIds = Stream.of(uri1, uri2)
                .map(it -> Long.parseLong(it.split("/")[2]))
                .collect(Collectors.toList());

        // when
        ExtractableResponse<Response> findAllResponse = getResponse(setRequest().get("/lines"));

        List<Long> resultLineIds = findAllResponse.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        // then
        assertThat(findAllResponse.statusCode())
                .isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds)
                .containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        setRequest().body(line).post("/lines");

        // when
        ExtractableResponse<Response> updateResponse = getResponse(setRequest().body(line2).put("/lines/1"));
        // then
        assertThat(updateResponse.statusCode())
                .isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 ID의 지하철 노선을 수정한다.")
    @Test
    void updateLine_error() {
        // given
        setRequest().body(line).post("/lines");

        // when
        ExtractableResponse<Response> updateResponse = getResponse(setRequest().body(line2).put("/lines/2"));
        // then
        assertThat(updateResponse.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(updateResponse.body().asString())
                .isEqualTo("해당 아이디의 노선이 없습니다.");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        setRequest().body(station).post("/stations");
        setRequest().body(station2).post("/stations");
        String uri = getResponse(setRequest().body(line).post("/lines"))
                .header("Location");

        // when
        ExtractableResponse<Response> response = getResponse(setRequest().delete(uri));

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거한다.")
    @Test
    void deleteLine_error() {
        ExtractableResponse<Response> response = getResponse(setRequest().delete("/lines/100"));
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
                .isEqualTo("해당 아이디의 노선이 없습니다.");
    }

    private RequestSpecification setRequest() {
        return RestAssured.given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    private ExtractableResponse<Response> getResponse(Response response) {
        return response.then().log().all().extract();
    }
}
