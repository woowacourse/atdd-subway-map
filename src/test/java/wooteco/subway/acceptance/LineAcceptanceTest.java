package wooteco.subway.acceptance;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;
import static wooteco.subway.acceptance.util.RestAssuredUtils.checkProperErrorMessage;
import static wooteco.subway.acceptance.util.RestAssuredUtils.checkProperResponseStatus;
import static wooteco.subway.acceptance.util.RestAssuredUtils.checkSameResponseIds;
import static wooteco.subway.acceptance.util.RestAssuredUtils.createData;
import static wooteco.subway.acceptance.util.RestAssuredUtils.deleteData;
import static wooteco.subway.acceptance.util.RestAssuredUtils.getData;
import static wooteco.subway.acceptance.util.RestAssuredUtils.getLocationId;
import static wooteco.subway.acceptance.util.RestAssuredUtils.modifyData;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.domain.Line;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private String lineName = "신분당선";
    private String lineColor = "bg-red-600";

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void create() {
        // given
        final Line line = new Line(lineName, lineColor);

        // when
        ExtractableResponse<Response> createResponse = createData("/lines", line);

        // then
        checkProperResponseStatus(createResponse, HttpStatus.CREATED);
        checkProperData("/lines/" + getLocationId(createResponse), line);
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        final Line line = new Line(lineName, lineColor);
        createData("/lines", line);

        // when
        ExtractableResponse<Response> createResponse = createData("/lines", line);

        // then
        checkProperResponseStatus(createResponse, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> response1 = createData("/lines", new Line(lineName, lineColor));
        ExtractableResponse<Response> response2 = createData("/lines", new Line("분당선", "bg-green-600"));

        // when
        ExtractableResponse<Response> getLinesResponse = getData("/lines");

        // then
        checkProperResponseStatus(getLinesResponse, HttpStatus.OK);
        List<ExtractableResponse<Response>> responses = Arrays.asList(response1, response2);
        checkSameResponseIds(getLinesResponse, responses);
    }

    @DisplayName("지하철 단일 노선을 조회한다.")
    @Test
    void getLineById() {
        // given
        final Line line = new Line(lineName, lineColor);
        ExtractableResponse<Response> createResponse = createData("/lines", line);

        // when
        Long resultLineId = getLocationId(createResponse);
        ExtractableResponse<Response> getLineResponse = getData("/lines/" + resultLineId);

        // then
        checkProperResponseStatus(getLineResponse, HttpStatus.OK);
        checkProperData("/lines/" + getLocationId(createResponse), line);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final Line line = new Line(lineName, lineColor);
        ExtractableResponse<Response> createResponse = createData("/lines", line);

        // when
        final Line line2 = new Line("다른분당선", "bg-red-600");
        ExtractableResponse<Response> modifyResponse = modifyData("/lines/" + getLocationId(createResponse), line2);

        // then
        checkProperResponseStatus(modifyResponse, HttpStatus.OK);
        checkProperData("/lines/" + getLocationId(createResponse), line2);
    }

    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        final Line line = new Line(lineName, lineColor);
        ExtractableResponse<Response> createResponse = createData("/lines", line);

        // when
        ExtractableResponse<Response> deleteResponse = deleteData(createResponse.header("Location"));

        // then
        checkProperResponseStatus(deleteResponse, HttpStatus.NO_CONTENT);
        checkProperErrorMessage("/lines/" + getLocationId(createResponse), "해당하는 노선이 존재하지 않습니다.");
    }

    private void checkProperData(String url, Line line) {
        get(url).then()
                .assertThat()
                .body("id", equalTo(Integer.parseInt(url.split("/")[2])))
                .body("name", equalTo(line.getName()))
                .body("color", equalTo(line.getColor()));
    }
}
