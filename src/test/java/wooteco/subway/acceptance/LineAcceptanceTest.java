package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptance.AcceptanceTestFixture.delete;
import static wooteco.subway.acceptance.AcceptanceTestFixture.get;
import static wooteco.subway.acceptance.AcceptanceTestFixture.getLineRequest;
import static wooteco.subway.acceptance.AcceptanceTestFixture.getStationRequest;
import static wooteco.subway.acceptance.AcceptanceTestFixture.insert;
import static wooteco.subway.acceptance.AcceptanceTestFixture.line1Post;
import static wooteco.subway.acceptance.AcceptanceTestFixture.line2Post;
import static wooteco.subway.acceptance.AcceptanceTestFixture.line2Put;
import static wooteco.subway.acceptance.AcceptanceTestFixture.update;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.LineResponse;

@Transactional
@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setStations() {
        insert(getStationRequest("name1"), "/stations");
        insert(getStationRequest("name2"), "/stations");
        insert(getStationRequest("name3"), "/stations");
        insert(getStationRequest("name4"), "/stations");
    }

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given & when
        ExtractableResponse<Response> response = insert(getLineRequest(line1Post), "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        insert(getLineRequest(line1Post), "/lines");

        // when
        ExtractableResponse<Response> createResponse = insert(getLineRequest(line1Post), "/lines");

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 전체 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = insert(getLineRequest(line1Post), "/lines");

        ExtractableResponse<Response> createResponse2 = insert(getLineRequest(line2Post), "/lines");
        // when
        ExtractableResponse<Response> getResponse = get("/lines");

        // then
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = getResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }


    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        ExtractableResponse<Response> createResponse1 = insert(getLineRequest(line1Post), "/lines");

        ExtractableResponse<Response> createResponse2 = insert(getLineRequest(line2Post), "/lines");

        Long expectedId = Long.parseLong(createResponse1.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = get("/lines/" + expectedId);

        Long resultId = response.jsonPath().getLong("id");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultId).isEqualTo(expectedId);
    }


    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = insert(getLineRequest(line1Post), "/lines");

        // when
        String path = createResponse.header("Location");
        ExtractableResponse<Response> response = delete(path);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }


    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = insert(getLineRequest(line1Post), "/lines");
        String path = createResponse.header("Location");

        //when
        ExtractableResponse<Response> updateResponse = update(getLineRequest(line2Put), path);

        // then
        ExtractableResponse<Response> findResponse = get(path);

        LineResponse lineResponse = findResponse.as(LineResponse.class);

        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getColor()).isEqualTo("blue");
    }

    @DisplayName("기존에 존재하는 지하철 노선명으로 지하철 노선명을 수정한다.")
    @Test
    void updateLineWithDuplicateName() {
        /// given
        ExtractableResponse<Response> createResponse1 = insert(getLineRequest(line1Post), "/lines");
        ExtractableResponse<Response> createResponse2 = insert(getLineRequest(line2Post), "/lines");
        String path = createResponse1.header("Location");

        //when
        ExtractableResponse<Response> updateResponse = update(getLineRequest(line2Put), path);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
