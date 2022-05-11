package wooteco.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptance.AcceptanceFixture.delete;
import static wooteco.subway.acceptance.AcceptanceFixture.insert;

public class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach()
    void setStation() {
        // 지하철 등록
        insert(new StationRequest("강남역"), "/stations", 201);
        insert(new StationRequest("역삼역"), "/stations", 201);
        insert(new StationRequest("선릉역"), "/stations", 201);
        insert(new StationRequest("잠실역"), "/stations", 201);
    }

    @Test
    @DisplayName("지하철 구간 등록")
    void createSection() {
        //지하철 라인 등록
        insert(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10), "/lines", 201);

        //지하철 구간 등록
        ExtractableResponse<Response> response = insert(new SectionRequest(2L, 3L, 10), "/lines/" + 1 + "/sections", 201);

        //확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("지하철 구간 등록 예외 - 역 사이 간격이 조건에 맞지 않는 경우")
    void createSectionLengthException() {
        //지하철 라인 등록
        insert(new LineRequest("신분당선", "bg-red-600", 1L, 3L, 10), "/lines", 201);

        //지하철 구간 등록 예외
        insert(new SectionRequest(1L, 2L, 10), "/lines/" + 1 + "/sections", 400);
    }

    @Test
    @DisplayName("지하철 구간 등록 예외 - 노선이 이미 모두 추가되어 있는 경우")
    void createSameSectionException() {
        //지하철 라인 등록
        insert(new LineRequest("신분당선", "bg-red-600", 1L, 3L, 10), "/lines", 201);

        //지하철 구간 등록 예외
        insert(new SectionRequest(1L, 3L, 2), "/lines/" + 1 + "/sections", 400);
    }

    @Test
    @DisplayName("지하철 구간 등록 예외 - 등록하려는 노선이 하나도 겹치지 않는 경우")
    void createSectionNotAnySameException() {
        //지하철 라인 등록
        insert(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10), "/lines", 201);

        //지하철 구간 등록 예외
        insert(new SectionRequest(3L, 4L, 2), "/lines/" + 1 + "/sections", 400);
    }

    @Test
    @DisplayName("지하철 구간 제거")
    void deleteSection() {
        //지하철 라인 등록
        insert(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10), "/lines", 201);

        //지하철 구간 등록
        insert(new SectionRequest(2L, 3L, 10), "/lines/" + 1 + "/sections", 201);

        //지하철 구간 삭제
        delete("/lines/1/sections?stationId=2", 200);
    }

    @Test
    @DisplayName("지하철 구간 제거 예외 - 제거하려는 노선에서 구간이 1개인 경우")
    void deleteOneSectionException() {
        //지하철 라인 등록
        insert(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10), "/lines", 201);

        //지하철 구간 삭제 예외
        delete("/lines/1/sections?stationId=2", 400);
    }
}
