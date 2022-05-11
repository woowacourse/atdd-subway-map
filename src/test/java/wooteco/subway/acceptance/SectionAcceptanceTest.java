package wooteco.subway.acceptance;

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

    @Test
    @DisplayName("지하철 구간 등록")
    void createSection() {
        // 지하철 등록
        insert(new StationRequest("강남역"), "/stations");
        insert(new StationRequest("역삼역"), "/stations");
        insert(new StationRequest("선릉역"), "/stations");

        //지하철 라인 등록
        insert(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10), "/lines");

        //지하철 구간 등록
        ExtractableResponse<Response> response = insert(new SectionRequest(2L, 3L, 10), "/lines/" + 1 + "/sections");

        //확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("지하철 구간 제거")
    void deleteSection() {
        // 지하철 등록
        insert(new StationRequest("강남역"), "/stations");
        insert(new StationRequest("역삼역"), "/stations");
        insert(new StationRequest("선릉역"), "/stations");

        //지하철 라인 등록
        insert(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10), "/lines");

        //지하철 구간 등록
        insert(new SectionRequest(2L, 3L, 10), "/lines/" + 1 + "/sections");

        //지하철 구간 삭제
        delete("/lines/1/sections?stationId=2");
    }
}
