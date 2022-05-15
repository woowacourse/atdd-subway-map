package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptance.TestFixtures.extractDeleteResponse;
import static wooteco.subway.acceptance.TestFixtures.extractGetResponse;
import static wooteco.subway.acceptance.TestFixtures.extractPostResponse;
import static wooteco.subway.acceptance.TestFixtures.extractPutResponse;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineTest() {
        //given, when
        LineRequest lineRequest = new LineRequest(
                "분당선", "yellow", 1L, 2L, 5);
        ExtractableResponse<Response> response = extractPostResponse(lineRequest, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


    @DisplayName("기존에 존재하는 지하철 노선 정보로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateInfoTest() {
        // given
        LineRequest lineRequest = new LineRequest(
                "신분당선", "red", 1L, 2L, 5);

        // when
        ExtractableResponse<Response> repeatedResponse = extractPostResponse(lineRequest, "/lines");

        // then
        assertThat(repeatedResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getStations() {
        ExtractableResponse<Response> response = extractGetResponse("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(List.of(1L));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteStation() {
        ExtractableResponse<Response> response = extractDeleteResponse("/lines/1");
        ExtractableResponse<Response> getResponse = extractGetResponse("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("아이디를 받아 지하철 노선을 단 건 조회한다.")
    @Test
    void getStationByIdTest() {
        ExtractableResponse<Response> response = extractGetResponse("lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();

        assertThat(resultLineId).isEqualTo(1L);
    }

    @DisplayName("지하철 노선을 업데이트한다.")
    @Test
    void updateLine() {
        // when
        LineRequest updateLineRequest = new LineRequest(
                "분당선", "green", 1L, 2L, 5);

        ExtractableResponse<Response> updateResponse = extractPutResponse(updateLineRequest, "lines/1");
        ExtractableResponse<Response> getResponse = extractGetResponse("lines/1");

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getObject(".", LineResponse.class))
                .extracting("name", "color")
                .containsExactly("분당선", "green");
    }

    @DisplayName("구간을 등록한다.")
    @Test
    void addSection() {
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 5);
        ExtractableResponse<Response> response = extractPostResponse(sectionRequest,
                "/lines/1/sections");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void deleteSection() {
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 5);
        extractPostResponse(sectionRequest, "/lines/1/sections");

        ExtractableResponse<Response> response = extractDeleteResponse(
                "/lines/1/sections?stationId=2");
        ExtractableResponse<Response> getResponse = extractGetResponse("/lines/1");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse lineResponse = getResponse.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getStations())
                .extracting("id")
                .containsExactly(1L, 3L);
    }
}
