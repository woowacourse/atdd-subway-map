package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.controller.dto.line.LineRequest;
import wooteco.subway.controller.dto.line.LineResponse;
import wooteco.subway.controller.dto.station.StationRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.ResponseCreator.*;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private final StationRequest 낙성대 = new StationRequest("낙성대");
    private final StationRequest 사당 = new StationRequest("사당");
    private final StationRequest 방배 = new StationRequest("방배");
    private LineRequest 일호선 = new LineRequest("1호선", "bg-blue-200", 1L, 2L, 10);
    private LineRequest 이호선 = new LineRequest("2호선", "bg-green-200", 1L, 2L, 10);

    @BeforeEach
    void init() {
        createPostStationResponse(낙성대);
        createPostStationResponse(사당);
        createPostStationResponse(방배);
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // when
        ExtractableResponse<Response> response = createPostLineResponse(이호선);
        LineResponse lineResponse = response.body().jsonPath().getObject(".", LineResponse.class);
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(lineResponse.getName()).isEqualTo(이호선.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(이호선.getColor())
        );
    }

    @DisplayName("기존에 존재하는 노선 이름으로 생성시 예외가 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        createPostLineResponse(이호선);
        // when
        ExtractableResponse<Response> response = createPostLineResponse(이호선);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void findLine() {
        // given
        ExtractableResponse<Response> createResponse = createPostLineResponse(이호선);
        String id = createResponse.header("Location").split("/")[2];
        // when
        ExtractableResponse<Response> response = createGetLineResponseById(id);
        LineResponse lineResponse = response.body().jsonPath().getObject(".", LineResponse.class);
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getId()).isEqualTo(Long.valueOf(id)),
                () -> assertThat(lineResponse.getName()).isEqualTo(이호선.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(이호선.getColor())
        );
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> 일호선응답 = createPostLineResponse(일호선);
        ExtractableResponse<Response> 이호선응답 = createPostLineResponse(이호선);
        // when
        ExtractableResponse<Response> response = createGetLinesResponse();
        List<Long> 추가한노선Id = postIds(일호선응답, 이호선응답);
        List<Long> 전체노선Id = responseIds(response);
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(전체노선Id).containsAll(추가한노선Id)
        );
    }

    @DisplayName("노선을 업데이트한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = createPostLineResponse(일호선);
        String id = createResponse.header("Location").split("/")[2];
        // when
        ExtractableResponse<Response> response = createPutLineResponse(id, 이호선);
        LineResponse lineResponse = createGetLineResponseById(id).body().jsonPath().getObject(".", LineResponse.class);
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getId()).isEqualTo(Long.valueOf(id)),
                () -> assertThat(lineResponse.getName()).isEqualTo(이호선.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(이호선.getColor())
        );
    }

    @DisplayName("이미 존재하는 노선이름으로 변경 시 업데이트에 실패한다.")
    @Test
    void failUpdateLine() {
        // given
        createPostLineResponse(일호선);
        ExtractableResponse<Response> createResponse2 = createPostLineResponse(이호선);
        // when
        String id = createResponse2.header("Location").split("/")[2];
        ExtractableResponse<Response> response = createPutLineResponse(id, 일호선);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선삭제")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createPostLineResponse(일호선);
        String id = createResponse.header("Location").split("/")[2];
        // when
        ExtractableResponse<Response> response = createDeleteLineResponseById(id);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("없는 노선 삭제시 예외 발생")
    @Test
    void invalidLine() {
        // given
        // when
        ExtractableResponse<Response> response = createDeleteLineResponseById(-1L);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}

