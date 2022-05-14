package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.repository.StationRepository;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationRepository stationRepository;

    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;

    @BeforeEach
    void createStations() {
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        선릉역 = stationRepository.save(new Station("선릉역"));
    }

    @DisplayName("노선을 생성하면 201 created를 반환하고 Location header에 url resource를 반환한다.")
    @Test
    void createLine() {
        LineRequest params = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 5);
        ExtractableResponse<Response> response = httpPostTest(params, "/lines");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성하면 400 bad-request가 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        LineRequest params = new LineRequest("신분당선", "bg-red-600");

        httpPostTest(params, "/lines");
        ExtractableResponse<Response> response = httpPostTest(params, "/lines");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 노선을 조회하면 200 ok와 노선 정보를 반환한다.")
    @Test
    void getLines() {
        LineRequest newBundangLine = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 5);
        ExtractableResponse<Response> newBundangPostResponse = httpPostTest(newBundangLine, "/lines");

        LineRequest bundangLine = new LineRequest("분당선", "bg-green-600", 강남역.getId(), 역삼역.getId(), 5);

        ExtractableResponse<Response> bundangPostResponse = httpPostTest(bundangLine, "/lines");

        ExtractableResponse<Response> response = httpGetTest("/lines");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(newBundangPostResponse, bundangPostResponse).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("단건 노선을 조회하면 200 OK와 노선 정보를 반환한다")
    @Test
    void getLine() {
        LineRequest params = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 5);
        ExtractableResponse<Response> createResponse = httpPostTest(params, "/lines");

        long id = Long.parseLong(createResponse.header(HttpHeaders.LOCATION).split("/")[2]);

        ExtractableResponse<Response> getResponse = httpGetTest("/lines/" + id);
        JsonPath lineResponsePath = getResponse.jsonPath();
        long responseId = lineResponsePath.getLong("id");
        List<Station> stations = lineResponsePath.getList("stations", Station.class);
        assertAll(
                () -> assertThat(id).isEqualTo(responseId),
                () -> assertThat(stations).extracting("id", "name").containsExactly(
                        tuple(강남역.getId(), 강남역.getName()),
                        tuple(역삼역.getId(), 역삼역.getName())
                )
        );
    }

    @DisplayName("노선을 수정하면 200 OK를 반환한다.")
    @Test
    void updateLine() {
        LineRequest params = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 5);
        ExtractableResponse<Response> createResponse = httpPostTest(params, "/lines");

        long id = Long.parseLong(createResponse.header(HttpHeaders.LOCATION).split("/")[2]);

        LineRequest updateParam = new LineRequest("다른분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 5);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 제거하면 204 No Content를 반환한다.")
    @Test
    void deleteStation() {
        LineRequest params = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 5);

        ExtractableResponse<Response> createResponse = httpPostTest(params, "/lines");

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = httpDeleteTest(uri);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @AfterEach
    void tearDown() {
        stationRepository.deleteById(강남역.getId());
        stationRepository.deleteById(역삼역.getId());
        stationRepository.deleteById(선릉역.getId());
    }
}
