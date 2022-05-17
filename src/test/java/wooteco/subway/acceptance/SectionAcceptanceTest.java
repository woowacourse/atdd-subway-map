package wooteco.subway.acceptance;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;
import static wooteco.subway.acceptance.util.RestAssuredUtils.checkProperResponseStatus;
import static wooteco.subway.acceptance.util.RestAssuredUtils.createData;
import static wooteco.subway.acceptance.util.RestAssuredUtils.deleteData;
import static wooteco.subway.acceptance.util.RestAssuredUtils.getLocationId;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.SectionRequest;

@DisplayName("지하철구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private String lineName = "신분당선";
    private String lineColor = "bg-red-600";
    private int distance = 10;
    private int shortDistance = 9;
    private int longDistance = 11;

    @DisplayName("상행 종점을 등록한다.")
    @Test
    void createLastUpStation() {
        // given
        ExtractableResponse<Response> createStation1 = createData("/stations", new Station("새로운상행종점"));
        ExtractableResponse<Response> createStation2 = createData("/stations", new Station("기존상행종점"));
        ExtractableResponse<Response> createStation3 = createData("/stations", new Station("기존하행종점"));
        final LineRequest lineRequest = new LineRequest(lineName, lineColor,
                getLocationId(createStation2), getLocationId(createStation3), distance);
        ExtractableResponse<Response> lineResponse = createData("/lines", lineRequest);

        // when
        final SectionRequest sectionRequest = new SectionRequest(getLocationId(createStation1),
                getLocationId(createStation2), distance);
        final ExtractableResponse<Response> sectionResponse
                = createData("/lines/" + getLocationId(lineResponse) + "/sections", sectionRequest);

        // then
        checkProperResponseStatus(sectionResponse, HttpStatus.OK);
        checkProperData("/lines/" + getLocationId(lineResponse),
                new Line(getLocationId(lineResponse), lineName, lineColor),
                new Station(getLocationId(createStation1), "새로운상행종점"),
                new Station(getLocationId(createStation2), "기존상행종점"),
                new Station(getLocationId(createStation3), "기존하행종점"));
    }

    @DisplayName("하행 종점을 등록한다.")
    @Test
    void createLastDownStation() {
        // given
        ExtractableResponse<Response> createStation1 = createData("/stations", new Station("기존상행종점"));
        ExtractableResponse<Response> createStation2 = createData("/stations", new Station("기존하행종점"));
        ExtractableResponse<Response> createStation3 = createData("/stations", new Station("새로운하행종점"));
        final LineRequest lineRequest = new LineRequest(lineName, lineColor,
                getLocationId(createStation1), getLocationId(createStation2), distance);
        ExtractableResponse<Response> lineResponse = createData("/lines", lineRequest);

        // when
        final SectionRequest sectionRequest = new SectionRequest(getLocationId(createStation2),
                getLocationId(createStation3), distance);
        final ExtractableResponse<Response> sectionResponse
                = createData("/lines/" + getLocationId(lineResponse) + "/sections", sectionRequest);

        // then
        checkProperResponseStatus(sectionResponse, HttpStatus.OK);
        checkProperData("/lines/" + getLocationId(lineResponse),
                new Line(getLocationId(lineResponse), lineName, lineColor),
                new Station(getLocationId(createStation1), "기존상행종점"),
                new Station(getLocationId(createStation2), "기존하행종점"),
                new Station(getLocationId(createStation3), "새로운하행종점"));
    }

    @DisplayName("갈래길방지1 - 기존 구간의 상행역과 같고 하행역이 다를 경우, 기존 구간을 변경하고 등록한다.")
    @Test
    void createInterruptSameUpStation() {
        // given
        ExtractableResponse<Response> createStation1 = createData("/stations", new Station("기존상행종점"));
        ExtractableResponse<Response> createStation2 = createData("/stations", new Station("새로운하행"));
        ExtractableResponse<Response> createStation3 = createData("/stations", new Station("기존하행종점"));
        final LineRequest lineRequest = new LineRequest(lineName, lineColor,
                getLocationId(createStation1), getLocationId(createStation3), distance);
        ExtractableResponse<Response> lineResponse = createData("/lines", lineRequest);

        // when
        final SectionRequest sectionRequest = new SectionRequest(getLocationId(createStation1),
                getLocationId(createStation2), shortDistance);
        final ExtractableResponse<Response> sectionResponse
                = createData("/lines/" + getLocationId(lineResponse) + "/sections", sectionRequest);

        // then
        checkProperResponseStatus(sectionResponse, HttpStatus.OK);
        checkProperData("/lines/" + getLocationId(lineResponse),
                new Line(getLocationId(lineResponse), lineName, lineColor),
                new Station(getLocationId(createStation1), "기존상행종점"),
                new Station(getLocationId(createStation2), "새로운하행"),
                new Station(getLocationId(createStation3), "기존하행종점"));
    }

    @DisplayName("갈래길방지2 - 기존 구간의 하행역과 같고 상행역이 다를 경우, 기존 구간을 변경하고 등록한다.")
    @Test
    void createInterruptSameDownStation() {
        // given
        ExtractableResponse<Response> createStation1 = createData("/stations", new Station("기존상행종점"));
        ExtractableResponse<Response> createStation2 = createData("/stations", new Station("새로운상행"));
        ExtractableResponse<Response> createStation3 = createData("/stations", new Station("기존하행종점"));
        final LineRequest lineRequest = new LineRequest(lineName, lineColor,
                getLocationId(createStation1), getLocationId(createStation3), distance);
        ExtractableResponse<Response> lineResponse = createData("/lines", lineRequest);

        // when
        final SectionRequest sectionRequest = new SectionRequest(getLocationId(createStation2),
                getLocationId(createStation3), shortDistance);
        final ExtractableResponse<Response> sectionResponse
                = createData("/lines/" + getLocationId(lineResponse) + "/sections", sectionRequest);

        // then
        checkProperResponseStatus(sectionResponse, HttpStatus.OK);
        checkProperData("/lines/" + getLocationId(lineResponse),
                new Line(getLocationId(lineResponse), lineName, lineColor),
                new Station(getLocationId(createStation1), "기존상행종점"),
                new Station(getLocationId(createStation2), "새로운상행"),
                new Station(getLocationId(createStation3), "기존하행종점"));
    }

    @DisplayName("상행 종점을 제거한다.")
    @Test
    void deleteLastUpSection() {
        // given
        ExtractableResponse<Response> createStation1 = createData("/stations", new Station("삭제될상행종점"));
        ExtractableResponse<Response> createStation2 = createData("/stations", new Station("상행종점"));
        ExtractableResponse<Response> createStation3 = createData("/stations", new Station("하행종점"));
        final LineRequest lineRequest = new LineRequest(lineName, lineColor, getLocationId(createStation2), getLocationId(createStation3), distance);
        ExtractableResponse<Response> lineResponse = createData("/lines", lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(getLocationId(createStation1), getLocationId(createStation2), distance);
        createData("/lines/" + getLocationId(lineResponse) + "/sections", sectionRequest);

        // when
        ExtractableResponse<Response> deleteResponse
                = deleteData("/lines/" + getLocationId(lineResponse) + "/sections?stationId=" + getLocationId(createStation1));

        // then
        checkProperResponseStatus(deleteResponse, HttpStatus.OK);
        checkDeletedData("/lines/" + getLocationId(lineResponse),
                new Line(getLocationId(lineResponse), lineName, lineColor),
                new Station(getLocationId(createStation2), "상행종점"),
                new Station(getLocationId(createStation3), "하행종점"));
    }

    @DisplayName("하행 종점을 제거한다.")
    @Test
    void deleteLastDownSection() {
        // given
        ExtractableResponse<Response> createStation1 = createData("/stations", new Station("상행종점"));
        ExtractableResponse<Response> createStation2 = createData("/stations", new Station("하행종점"));
        ExtractableResponse<Response> createStation3 = createData("/stations", new Station("삭제될하행종점"));
        final LineRequest lineRequest = new LineRequest(lineName, lineColor, getLocationId(createStation1), getLocationId(createStation2), distance);
        ExtractableResponse<Response> lineResponse = createData("/lines", lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(getLocationId(createStation2), getLocationId(createStation3), distance);
        createData("/lines/" + getLocationId(lineResponse) + "/sections", sectionRequest);

        // when
        ExtractableResponse<Response> deleteResponse
                = deleteData("/lines/" + getLocationId(lineResponse) + "/sections?stationId=" + getLocationId(createStation3));

        // then
        checkProperResponseStatus(deleteResponse, HttpStatus.OK);
        checkDeletedData("/lines/" + getLocationId(lineResponse),
                new Line(getLocationId(lineResponse), lineName, lineColor),
                new Station(getLocationId(createStation1), "상행종점"),
                new Station(getLocationId(createStation2), "하행종점"));
    }

    @DisplayName("중간역을 제거한다.")
    @Test
    void deleteMiddleSection() {
        // given
        ExtractableResponse<Response> createStation1 = createData("/stations", new Station("상행종점"));
        ExtractableResponse<Response> createStation2 = createData("/stations", new Station("삭제될중간역"));
        ExtractableResponse<Response> createStation3 = createData("/stations", new Station("하행종점"));
        final LineRequest lineRequest = new LineRequest(lineName, lineColor, getLocationId(createStation1), getLocationId(createStation2), distance);
        ExtractableResponse<Response> lineResponse = createData("/lines", lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(getLocationId(createStation2), getLocationId(createStation3), distance);
        createData("/lines/" + getLocationId(lineResponse) + "/sections", sectionRequest);

        // when
        ExtractableResponse<Response> deleteResponse
                = deleteData("/lines/" + getLocationId(lineResponse) + "/sections?stationId=" + getLocationId(createStation2));

        // then
        checkProperResponseStatus(deleteResponse, HttpStatus.OK);
        checkDeletedData("/lines/" + getLocationId(lineResponse),
                new Line(getLocationId(lineResponse), lineName, lineColor),
                new Station(getLocationId(createStation1), "상행종점"),
                new Station(getLocationId(createStation3), "하행종점"));
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 경우 에러를 발생한다.")
    @Test
    void deleteLastSection() {
        // given
        ExtractableResponse<Response> createStation1 = createData("/stations", new Station("상행종점"));
        ExtractableResponse<Response> createStation2 = createData("/stations", new Station("하행종점"));
        final LineRequest lineRequest = new LineRequest(lineName, lineColor, getLocationId(createStation1), getLocationId(createStation2), distance);
        ExtractableResponse<Response> lineResponse = createData("/lines", lineRequest);

        // when
        ExtractableResponse<Response> deleteResponse1
                = deleteData("/lines/" + getLocationId(lineResponse) + "/sections?stationId=" + getLocationId(createStation1));
        ExtractableResponse<Response> deleteResponse2
                = deleteData("/lines/" + getLocationId(lineResponse) + "/sections?stationId=" + getLocationId(createStation2));

        // then
        checkProperResponseStatus(deleteResponse1, HttpStatus.BAD_REQUEST);
        checkProperResponseStatus(deleteResponse2, HttpStatus.BAD_REQUEST);
    }

    private void checkProperData(String url, Line line, Station station1, Station station2, Station station3) {
        get(url).then()
                .log().all()
                .assertThat()
                .body("id", equalTo(Integer.parseInt(url.split("/")[2])))
                .body("name", equalTo(line.getName()))
                .body("color", equalTo(line.getColor()))
                .body("stations[0].id", equalTo(station1.getId().intValue()))
                .body("stations[0].name", equalTo(station1.getName()))
                .body("stations[1].id", equalTo(station2.getId().intValue()))
                .body("stations[1].name", equalTo(station2.getName()))
                .body("stations[2].id", equalTo(station3.getId().intValue()))
                .body("stations[2].name", equalTo(station3.getName()));
    }

    private void checkDeletedData(String url, Line line, Station station1, Station station2) {
        get(url).then()
                .log().all()
                .assertThat()
                .body("id", equalTo(Integer.parseInt(url.split("/")[2])))
                .body("name", equalTo(line.getName()))
                .body("color", equalTo(line.getColor()))
                .body("stations[0].id", equalTo(station1.getId().intValue()))
                .body("stations[0].name", equalTo(station1.getName()))
                .body("stations[1].id", equalTo(station2.getId().intValue()))
                .body("stations[1].name", equalTo(station2.getName()));
    }
}
