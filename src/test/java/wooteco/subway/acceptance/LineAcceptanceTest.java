package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.ui.request.LineRequest;
import wooteco.subway.ui.request.SectionRequest;
import wooteco.subway.ui.request.StationRequest;
import wooteco.subway.ui.response.LineResponse;
import wooteco.subway.ui.response.StationResponse;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private static final Long stationIdA = 1L;
    private static final Long stationIdB = 2L;

    private final String defaultUri = "/lines";

    @BeforeEach
    @Override
    public void setUp() {
        RestAssured.port = port;
        StationRequest stationRequestA = new StationRequest("강남");
        StationRequest stationRequestB = new StationRequest("역삼");
        getExtractablePostResponse(stationRequestA, "/stations");
        getExtractablePostResponse(stationRequestB, "/stations");
    }

    @Test
    @DisplayName("지하철 노선을 등록한다.")
    void createLine() {
        // given
        String lineName = "7호선";
        String lineColor = "khaki";
        LineRequest request = new LineRequest(lineName, lineColor, stationIdA, stationIdB, 10);

        // when
        ExtractableResponse<Response> response = getExtractablePostResponse(request, defaultUri);
        Long lineId = Long.parseLong(response.header("Location").split("/")[2]);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.body().jsonPath().getLong("id")).isEqualTo(lineId),
            () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo(lineName),
            () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo(lineColor),
            () -> assertThat(response.body().jsonPath().getString("stations"))
                .isEqualTo("[[id:1, name:강남], [id:2, name:역삼]]")
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"라:0", "라:31"}, delimiter = ':')
    @DisplayName("유효하지 않는 이름으로 노선을 등록할 경우 400 응답을 던진다.")
    void createLineWithInvalidName(String name, int repeatCount) {
        // given
        LineRequest request = new LineRequest(name.repeat(repeatCount), "khaki", stationIdA, stationIdB, 10);

        // when
        ExtractableResponse<Response> response = getExtractablePostResponse(request, defaultUri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo("이름은 1~30 자 이내여야 합니다.");
    }

    @ParameterizedTest
    @CsvSource(value = {"라:0", "라:21"}, delimiter = ':')
    @DisplayName("유효하지 않는 색상으로 노선을 등록할 경우 400 응답을 던진다.")
    void createLineWithInvalidColor(String color, int repeatCount) {
        // given
        LineRequest request = new LineRequest("7호선", color.repeat(repeatCount), stationIdA, stationIdB, 10);

        // when
        ExtractableResponse<Response> response = getExtractablePostResponse(request, defaultUri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo("색상은 1~20 자 이내여야 합니다.");
    }

    @Test
    @DisplayName("기존에 존재하는 이름으로 노선을 등록하면 400 응답을 던진다.")
    void createLineWithDuplicateName() {
        // given
        LineRequest request = new LineRequest("4호선", "sky-blue", stationIdA, stationIdB, 10);
        getExtractablePostResponse(request, defaultUri);

        // when
        ExtractableResponse<Response> response = getExtractablePostResponse(request, defaultUri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void getLines() {
        // given
        StationRequest stationRequestA = new StationRequest("선릉");
        StationRequest stationRequestB = new StationRequest("삼성");
        String idA = getExtractablePostResponse(stationRequestA, "/stations").header("Location").split("/")[2];
        String idB = getExtractablePostResponse(stationRequestB, "/stations").header("Location").split("/")[2];

        LineRequest firstRequest = new LineRequest("4호선", "sky-blue", Long.parseLong(idA), Long.parseLong(idB), 10);
        ExtractableResponse<Response> firstResponse = getExtractablePostResponse(firstRequest, defaultUri);

        LineRequest secondRequest = new LineRequest("7호선", "khaki", stationIdA, stationIdB, 10);
        ExtractableResponse<Response> secondResponse = getExtractablePostResponse(secondRequest, defaultUri);

        List<LineResponse> expectedLineResponses = Stream.of(firstResponse, secondResponse)
            .map(it -> it.jsonPath().getObject(".", LineResponse.class))
            .collect(Collectors.toList());

        // when
        ExtractableResponse<Response> response = getExtractableGetResponse(defaultUri);
        List<LineResponse> actualLineResponses = response.jsonPath().getList(".", LineResponse.class);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(actualLineResponses).isEqualTo(expectedLineResponses)
        );
    }

    @Test
    @DisplayName("단일 노선을 조회한다.")
    void getLine() {
        // given
        LineRequest request = new LineRequest("4호선", "sky-blue", stationIdA, stationIdB, 5);
        ExtractableResponse<Response> createResponse = getExtractablePostResponse(request, defaultUri);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = getExtractableGetResponse(uri);

        LineResponse lineResponse = response.body().jsonPath().getObject(".", LineResponse.class);
        LineResponse expected = createResponse.jsonPath().getObject(".", LineResponse.class);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(lineResponse).isEqualTo(expected)
        );
    }

    @Test
    @DisplayName("존재하지 않는 노선을 조회할 경우 404 응답을 던진다.")
    void getLineNotExists() {
        // when
        ExtractableResponse<Response> response = getExtractableGetResponse(defaultUri + "/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("노선 정보를 수정한다.")
    void update() {
        // given
        LineRequest request = new LineRequest("4호선", "sky-blue", stationIdA, stationIdB, 10);
        ExtractableResponse<Response> createResponse = getExtractablePostResponse(request, defaultUri);

        // when
        String uri = createResponse.header("Location");
        LineRequest updateRequest = new LineRequest("2호선", "green");
        getExtractablePutResponse(updateRequest, uri);

        ExtractableResponse<Response> response = getExtractableGetResponse(uri);
        LineResponse lineResponse = response.body().jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getColor()).isEqualTo("green");
    }

    @Test
    @DisplayName("이미 존재하는 이름으로 수정할 경우 400 응답을 던진다.")
    void updateWithDuplicatedName() {
        // given
        LineRequest request = new LineRequest("4호선", "sky-blue", stationIdA, stationIdB, 10);
        ExtractableResponse<Response> createResponse = getExtractablePostResponse(request, defaultUri);

        // when
        long savedId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        long otherId = savedId + 1;

        LineRequest updateRequest = new LineRequest("4호선", "green");
        ExtractableResponse<Response> response = getExtractablePutResponse(updateRequest,
            defaultUri + "/" + otherId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("노선을 제거한다.")
    void deleteLine() {
        // given
        LineRequest request = new LineRequest("7호선", "khaki", stationIdA, stationIdB, 10);
        ExtractableResponse<Response> createResponse = getExtractablePostResponse(request, defaultUri);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = getExtractableDeleteResponse(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 id 로 노선을 제거할 경우 404 응답을 던진다.")
    void deleteLineWithIdNotExists() {
        // when
        ExtractableResponse<Response> response = getExtractableDeleteResponse(defaultUri + "/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("상행 종점 좌우측에 구간을 추가한다(C -> D -> A -> B).")
    void addUpDestination() {
        //given
        StationRequest stationRequest = new StationRequest("선릉");
        ExtractableResponse<Response> createStationResponse = getExtractablePostResponse(stationRequest, "/stations");
        long stationIdC = Long.parseLong(createStationResponse.header("Location").split("/")[2]);

        StationRequest stationRequest1 = new StationRequest("삼성");
        ExtractableResponse<Response> createStationResponse1 = getExtractablePostResponse(stationRequest1, "/stations");
        long stationIdD = Long.parseLong(createStationResponse1.header("Location").split("/")[2]);

        LineRequest lineRequest = new LineRequest("7호선", "khaki", stationIdA, stationIdB, 10);
        ExtractableResponse<Response> createLineResponse = getExtractablePostResponse(lineRequest, defaultUri);
        long lineId = Long.parseLong(createLineResponse.header("Location").split("/")[2]);

        //when
        SectionRequest sectionRequestA = new SectionRequest(stationIdC, stationIdA, 10);
        ExtractableResponse<Response> responseA = getExtractablePostResponse(sectionRequestA,
            defaultUri + "/" + lineId + "/sections");

        SectionRequest sectionRequestB = new SectionRequest(stationIdC, stationIdD, 2);
        ExtractableResponse<Response> responseB = getExtractablePostResponse(sectionRequestB,
            defaultUri + "/" + lineId + "/sections");

        ExtractableResponse<Response> actual = getExtractableGetResponse(defaultUri);
        List<LineResponse> actualLineResponses = actual.jsonPath().getList(".", LineResponse.class);
        List<LineResponse> expectedLineResponses = List.of(
            new LineResponse(1L, "7호선", "khaki", List.of(new StationResponse(3L, "선릉"),
                new StationResponse(4L, "삼성"), new StationResponse(1L, "강남"),
                new StationResponse(2L, "역삼"))));

        //then
        assertAll(
            () -> assertThat(responseA.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(responseB.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(actualLineResponses).isEqualTo(expectedLineResponses)
        );
    }

    @Test
    @DisplayName("하행 종점 좌우측에 구간을 추가한다(A -> B -> D -> C).")
    void addDownDestination() {
        //given
        StationRequest stationRequestC = new StationRequest("선릉");
        ExtractableResponse<Response> createStationResponseC = getExtractablePostResponse(stationRequestC, "/stations");
        long stationIdC = Long.parseLong(createStationResponseC.header("Location").split("/")[2]);

        StationRequest stationRequestD = new StationRequest("삼성");
        ExtractableResponse<Response> createStationResponseD = getExtractablePostResponse(stationRequestD, "/stations");
        long stationIdD = Long.parseLong(createStationResponseD.header("Location").split("/")[2]);

        LineRequest lineRequest = new LineRequest("7호선", "khaki", stationIdA, stationIdB, 10);
        ExtractableResponse<Response> createLineResponse = getExtractablePostResponse(lineRequest, defaultUri);
        long lineId = Long.parseLong(createLineResponse.header("Location").split("/")[2]);

        //when
        SectionRequest sectionRequestA = new SectionRequest(stationIdB, stationIdC, 10);
        ExtractableResponse<Response> responseA = getExtractablePostResponse(sectionRequestA,
            defaultUri + "/" + lineId + "/sections");

        SectionRequest sectionRequestB = new SectionRequest(stationIdD, stationIdC, 2);
        ExtractableResponse<Response> responseB = getExtractablePostResponse(sectionRequestB,
            defaultUri + "/" + lineId + "/sections");

        ExtractableResponse<Response> actual = getExtractableGetResponse(defaultUri);
        List<LineResponse> actualLineResponses = actual.jsonPath().getList(".", LineResponse.class);
        List<LineResponse> expectedLineResponses = List.of(
            new LineResponse(1L, "7호선", "khaki", List.of(new StationResponse(1L, "강남"),
                new StationResponse(2L, "역삼"), new StationResponse(4L, "삼성"),
                new StationResponse(3L, "선릉"))));

        //then
        assertAll(
            () -> assertThat(responseA.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(responseB.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(actualLineResponses).isEqualTo(expectedLineResponses)
        );
    }

    @Test
    @DisplayName("역 목록의 중간에 구간을 추가한다(A -> C -> D -> B).")
    void addSectionsInMiddle() {
        //given
        StationRequest stationRequestC = new StationRequest("선릉");
        ExtractableResponse<Response> createStationResponseC = getExtractablePostResponse(stationRequestC, "/stations");
        long stationIdC = Long.parseLong(createStationResponseC.header("Location").split("/")[2]);

        StationRequest stationRequestD = new StationRequest("삼성");
        ExtractableResponse<Response> createStationResponseD = getExtractablePostResponse(stationRequestD, "/stations");
        long stationIdD = Long.parseLong(createStationResponseD.header("Location").split("/")[2]);

        LineRequest lineRequest = new LineRequest("7호선", "khaki", stationIdA, stationIdB, 10);
        ExtractableResponse<Response> createLineResponse = getExtractablePostResponse(lineRequest, defaultUri);
        long lineId = Long.parseLong(createLineResponse.header("Location").split("/")[2]);

        //when
        SectionRequest sectionRequestA = new SectionRequest(stationIdC, stationIdB, 4);
        ExtractableResponse<Response> responseA = getExtractablePostResponse(sectionRequestA,
            defaultUri + "/" + lineId + "/sections");

        SectionRequest sectionRequestB = new SectionRequest(stationIdC, stationIdD, 2);
        ExtractableResponse<Response> responseB = getExtractablePostResponse(sectionRequestB,
            defaultUri + "/" + lineId + "/sections");

        ExtractableResponse<Response> actual = getExtractableGetResponse(defaultUri);
        List<LineResponse> actualLineResponses = actual.jsonPath().getList(".", LineResponse.class);
        List<LineResponse> expectedLineResponses = List.of(
            new LineResponse(1L, "7호선", "khaki",
                List.of(new StationResponse(1L, "강남"),
                    new StationResponse(3L, "선릉"),
                    new StationResponse(4L, "삼성"),
                    new StationResponse(2L, "역삼")
                )));

        //then
        assertAll(
            () -> assertThat(responseA.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(responseB.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(actualLineResponses).isEqualTo(expectedLineResponses)
        );
    }
}
