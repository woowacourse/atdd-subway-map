package wooteco.subway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.service.LineService;
import wooteco.subway.service.StationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LineAcceptanceTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    private List<Long> testLineIds;
    private List<Long> testStationIds;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testLineIds = new ArrayList<>();
        testStationIds = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        try {
            testLineIds.forEach(testLineId -> lineService.deleteLine(testLineId));
            testStationIds.forEach(testStationId -> stationService.deleteById(testStationId));
        } catch (SubwayException ignored) {
        }
    }

    private ValidatableResponse postLineApi(LineRequest lineRequest) throws JsonProcessingException {
        String requestBody = OBJECT_MAPPER.writeValueAsString(lineRequest);
        ValidatableResponse validatableResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when().post("/lines")
                .then().log().all();
        int statusCode = validatableResponse.extract().statusCode();
        if (statusCode != 400) {
            addCreatedLineId(validatableResponse);
        }
        return validatableResponse;
    }

    private void addCreatedLineId(ValidatableResponse validatableResponse) {
        String headerToken = validatableResponse.extract()
                .header("Location")
                .split("/")[2];
        long id = Long.parseLong(headerToken);
        testLineIds.add(id);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() throws Exception {
        long upStationId = createStationApi("천호역");
        long downStationId = createStationApi("강남역");
        LineRequest lineRequest = new LineRequest("2호선", "red", upStationId, downStationId, 7);
        ValidatableResponse validatableResponse = postLineApi(lineRequest);
        long id = testLineIds.get(0);

        LineResponse lineResponse = new LineResponse(id, "2호선", "red");
        String responseBody = OBJECT_MAPPER.writeValueAsString(lineResponse);

        validatableResponse.statusCode(HttpStatus.CREATED.value())
                .body(is(responseBody));
    }

    private long createStationApi(String name) {
        long id = stationService.createStation(name)
                .getId();
        testStationIds.add(id);
        return id;
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 등록을 시도한다.")
    @Test
    void cannotCreateLine() throws Exception {
        long upStationId = createStationApi("천호역");
        long downStationId = createStationApi("강남역");
        LineRequest lineRequest = new LineRequest("2호선", "red", upStationId, downStationId, 7);
        postLineApi(lineRequest);

        postLineApi(lineRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 노선을 조회한다.")
    @Test
    void showLines() throws Exception {
        long upStationId = createStationApi("천호역");
        long downStationId = createStationApi("강남역");
        LineRequest lineRequest1 = new LineRequest("3호선", "red", upStationId, downStationId, 7);
        LineRequest lineRequest2 = new LineRequest("4호선", "blue", upStationId, downStationId, 4);
        postLineApi(lineRequest1);
        postLineApi(lineRequest2);

        List<Long> resultLineIds = RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getList("", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds.containsAll(testLineIds)).isTrue();
    }

    @DisplayName("아이디로 노선을 조회한다.")
    @Test
    void showLine() throws JsonProcessingException {
        long upStationId = createStationApi("천호역");
        long downStationId = createStationApi("강남역");
        LineRequest lineRequest = new LineRequest("5호선", "red", upStationId, downStationId, 7);
        postLineApi(lineRequest);

        long id = testLineIds.get(0);
        List<StationResponse> stationResponses = Arrays.asList(new StationResponse(upStationId, "천호역"),
                new StationResponse(downStationId, "강남역"));
        LineResponse lineResponse = new LineResponse(id, "5호선", "red", stationResponses);
        String responseBody = OBJECT_MAPPER.writeValueAsString(lineResponse);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body(is(responseBody));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void editLine() throws JsonProcessingException {
        long upStationId = createStationApi("천호역");
        long downStationId = createStationApi("강남역");
        LineRequest lineRequest = new LineRequest("6호선", "red", upStationId, downStationId, 7);
        postLineApi(lineRequest);
        long id = testLineIds.get(0);
        LineRequest putRequest = new LineRequest("구분당선", "white", upStationId, downStationId, 7);
        String requestBody = OBJECT_MAPPER.writeValueAsString(putRequest);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when().put("/lines/" + id)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("없는 아이디의 지하철 노선은 수정할 수 없다.")
    @Test
    void cannotEditLineWhenNoId() throws JsonProcessingException {
        long upStationId = createStationApi("천호역");
        long downStationId = createStationApi("강남역");
        LineRequest putRequest = new LineRequest("구분당선", "white", upStationId, downStationId, 7);
        String requestBody = OBJECT_MAPPER.writeValueAsString(putRequest);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when().put("/lines/" + 9999)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 생성된 노선의 이름으로 수정할 수 없다.")
    @Test
    void cannotEditLineWhenDuplicateName() throws JsonProcessingException {
        long upStationId = createStationApi("천호역");
        long downStationId = createStationApi("강남역");
        LineRequest lineRequest1 = new LineRequest("6호선", "red", upStationId, downStationId, 7);
        LineRequest lineRequest2 = new LineRequest("신분당선", "red", upStationId, downStationId, 7);
        postLineApi(lineRequest1);
        postLineApi(lineRequest2);
        long id = testLineIds.get(0);
        LineRequest putRequest = new LineRequest("신분당선", "white", upStationId, downStationId, 7);
        String requestBody = OBJECT_MAPPER.writeValueAsString(putRequest);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when().put("/lines/" + id)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSections() throws JsonProcessingException {
        long upStationId = createStationApi("천호역");
        long downStationId = createStationApi("강남역");
        long newStationId = createStationApi("의정부역");
        LineRequest lineRequest = new LineRequest("6호선", "red", upStationId, downStationId, 7);
        postLineApi(lineRequest);
        long lineId = testLineIds.get(0);
        String requestBody = OBJECT_MAPPER.writeValueAsString(new SectionRequest(upStationId, newStationId, 3));
        List<StationResponse> stationResponses = Arrays.asList(new StationResponse(upStationId, "천호역"),
                new StationResponse(newStationId, "의정부역"),
                new StationResponse(downStationId, "강남역"));
        String lineResponse = OBJECT_MAPPER.writeValueAsString(new LineResponse(lineId, "6호선", "red", stationResponses));

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body(is(lineResponse));
    }

    @DisplayName("등록된 노선을 삭제한다.")
    @Test
    void deleteLine() throws JsonProcessingException {
        long upStationId = createStationApi("천호역");
        long downStationId = createStationApi("강남역");
        LineRequest lineRequest = new LineRequest("7호선", "green", upStationId, downStationId, 7);
        String uri = postLineApi(lineRequest).extract().header("Location");

        RestAssured.given().log().all()
                .when().delete(uri)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
