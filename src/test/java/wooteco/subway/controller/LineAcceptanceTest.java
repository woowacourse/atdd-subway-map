package wooteco.subway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.controller.request.LineRequest;
import wooteco.subway.controller.request.SectionRequest;
import wooteco.subway.controller.response.LineResponse;
import wooteco.subway.controller.response.StationResponse;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql("classpath:/truncate-test.sql")
class LineAcceptanceTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private StationService stationService;

    private long upStationId;
    private long downStationId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        upStationId = createStation("천호역");
        downStationId = createStation("강남역");
    }

    private ValidatableResponse createLine(LineRequest lineRequest) throws JsonProcessingException {
        String requestBody = OBJECT_MAPPER.writeValueAsString(lineRequest);
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when().post("/lines")
                .then().log().all();
    }

    private long createStation(String name) {
        return stationService.createStation(name)
                .getId();
    }

    private long extractLineId(ValidatableResponse validatableResponse) {
        String headerToken = validatableResponse.extract()
                .header("Location")
                .split("/")[2];
        return Long.parseLong(headerToken);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() throws Exception {
        LineRequest lineRequest = new LineRequest("2호선", "red", upStationId, downStationId, 7);
        ValidatableResponse validatableResponse = createLine(lineRequest);
        long id = extractLineId(validatableResponse);

        LineResponse lineResponse = new LineResponse(id, "2호선", "red", Collections.emptyList());
        String responseBody = OBJECT_MAPPER.writeValueAsString(lineResponse);

        validatableResponse.statusCode(HttpStatus.CREATED.value())
                .body(is(responseBody));
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 등록을 시도한다.")
    @Test
    void cannotCreateLine() throws Exception {
        LineRequest lineRequest = new LineRequest("2호선", "red", upStationId, downStationId, 7);
        createLine(lineRequest);

        createLine(lineRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 노선을 조회한다.")
    @Test
    void showLines() throws Exception {
        LineRequest lineRequest1 = new LineRequest("3호선", "red", upStationId, downStationId, 7);
        LineRequest lineRequest2 = new LineRequest("4호선", "blue", upStationId, downStationId, 4);
        ValidatableResponse line1 = createLine(lineRequest1);
        ValidatableResponse line2 = createLine(lineRequest2);
        long line1Id = extractLineId(line1);
        long line2id = extractLineId(line2);

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

        assertThat(resultLineIds).contains(line1Id, line2id);
    }

    @DisplayName("아이디로 노선을 조회한다.")
    @Test
    void showLine() throws JsonProcessingException {
        LineRequest lineRequest = new LineRequest("5호선", "red", upStationId, downStationId, 7);
        ValidatableResponse response = createLine(lineRequest);
        long id = extractLineId(response);
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
        LineRequest lineRequest = new LineRequest("6호선", "red", upStationId, downStationId, 7);
        ValidatableResponse response = createLine(lineRequest);
        long id = extractLineId(response);

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
        LineRequest lineRequest1 = new LineRequest("6호선", "red", upStationId, downStationId, 7);
        LineRequest lineRequest2 = new LineRequest("신분당선", "red", upStationId, downStationId, 7);
        ValidatableResponse line1Response = createLine(lineRequest1);
        createLine(lineRequest2);

        long id = extractLineId(line1Response);
        LineRequest putRequest = new LineRequest("신분당선", "white", upStationId, downStationId, 7);
        String requestBody = OBJECT_MAPPER.writeValueAsString(putRequest);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when().put("/lines/" + id)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("등록된 노선을 삭제한다.")
    @Test
    void deleteLine() throws JsonProcessingException {
        LineRequest lineRequest = new LineRequest("7호선", "green", upStationId, downStationId, 7);
        String uri = createLine(lineRequest).extract().header("Location");

        RestAssured.given().log().all()
                .when().delete(uri)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("노선 구간의 중간에 신규 구간을 추가한다.")
    @Test
    void addSections() throws JsonProcessingException {
        LineRequest lineRequest = new LineRequest("6호선", "red", upStationId, downStationId, 7);
        ValidatableResponse response = createLine(lineRequest);
        long lineId = extractLineId(response);

        long newStationId = createStation("의정부역");
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

    @DisplayName("구간을 삭제한다.")
    @Test
    void deleteSection() throws JsonProcessingException {
        LineRequest lineRequest = new LineRequest("6호선", "red", upStationId, downStationId, 7);
        ValidatableResponse response = createLine(lineRequest);
        long lineId = extractLineId(response);

        long newStationId = createStation("의정부역");
        LineRequest sectionRequest = new LineRequest("6호선", "red", upStationId, newStationId, 3);
        sectionService.createSection(sectionRequest, lineId);

        List<StationResponse> stationResponses = Arrays.asList(new StationResponse(upStationId, "천호역"),
                new StationResponse(downStationId, "강남역"));
        String lineResponse = OBJECT_MAPPER.writeValueAsString(new LineResponse(lineId, "6호선", "red", stationResponses));

        RestAssured.given().log().all()
                .when().delete("/lines/" + lineId + "/sections?stationId=" + newStationId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body(is(lineResponse));
    }
}
