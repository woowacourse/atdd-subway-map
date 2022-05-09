package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.repository.StationRepository;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationRepository stationRepository;

    private LineRequest request;

    @BeforeEach
    void setUp2(){
        Station upStation = stationRepository.save(new Station("신림역"));
        Station downStation = stationRepository.save(new Station("신도림"));
        request = new LineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10);
    }

    @DisplayName("노선을 생성하면 201 created를 반환하고 Location header에 url resource를 반환한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성하면 400 bad-request가 발생한다.")
    @Test
    void createLineWithDuplicateName() {

        RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 노선을 조회하면 200 ok와 노선 정보를 반환한다.")
    @Test
    void getLines() {

        ExtractableResponse<Response> newBundangPostResponse = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Station upStation = stationRepository.save(new Station("을지로입구역"));
        Station downStation = stationRepository.save(new Station("을지로3가역"));
        LineRequest request2 = new LineRequest("분당선", "bg-green-600",upStation.getId(), downStation.getId(), 5);

        ExtractableResponse<Response> bundangPostResponse = RestAssured.given().log().all()
                .body(request2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Stream.of(newBundangPostResponse, bundangPostResponse)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<LineResponse> responses = response.jsonPath().getList(".", LineResponse.class);

        List<Long> ids = responses.stream().map(LineResponse::getId).collect(Collectors.toList());

        assertAll(
                () -> assertThat(ids).containsAll(expectedLineIds),
                () -> assertThat(responses.get(0).getName()).isEqualTo("신분당선"),
                () -> assertThat(responses.get(0).getColor()).isEqualTo("bg-red-600"),
                () -> assertThat(responses.get(1).getName()).isEqualTo("분당선"),
                () -> assertThat(responses.get(1).getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("단건 노선을 조회하면 200 OK와 노선 정보를 반환한다")
    @Test
    void getLine() {

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        long id = Long.parseLong(createResponse.header(HttpHeaders.LOCATION).split("/")[2]);

        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();

        long responseId = getResponse.jsonPath().getLong("id");
        assertThat(id).isEqualTo(responseId);
    }

    @DisplayName("노선을 수정하면 200 OK를 반환한다.")
    @Test
    void updateLine() {

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        long id = Long.parseLong(createResponse.header(HttpHeaders.LOCATION).split("/")[2]);

        LineRequest request = new LineRequest("다른분당선", "bg-red-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
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

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
