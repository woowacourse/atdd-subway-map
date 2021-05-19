package wooteco.subway.acceptance;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.controller.dto.SectionResponse;
import wooteco.subway.controller.dto.StationResponse;
import wooteco.subway.exception.response.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionCreateAcceptanceTest extends AcceptanceTest {

    private final StationResponse gangnam = new StationResponse(1L, "강남역");
    private final StationResponse yeoksam = new StationResponse(2L, "역삼역");
    private final StationResponse seolleung = new StationResponse(3L, "선릉역");

    @DisplayName("구간 생성 성공 - 상행 종점역")
    @Test
    void createSectionWithTopStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "1");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        final SectionResponse section = response.body().as(SectionResponse.class);
        assertThat(section.getLineId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(1L);
        assertThat(section.getUpStationId()).isEqualTo(3L);
        assertThat(section.getDistance()).isEqualTo(10);

        final LineResponse lineResponse = RestAssuredHelper.jsonGet("/lines/1").body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(seolleung, gangnam, yeoksam);
    }

    @DisplayName("구간 생성 성공 - 하행 종점역")
    @Test
    void createSectionWithBottomStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "2");
        params.put("downStationId", "3");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        final SectionResponse section = response.body().as(SectionResponse.class);
        assertThat(section.getLineId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(3L);
        assertThat(section.getUpStationId()).isEqualTo(2L);
        assertThat(section.getDistance()).isEqualTo(10);

        final LineResponse lineResponse = RestAssuredHelper.jsonGet("/lines/1").body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(gangnam, yeoksam, seolleung);
    }

    @DisplayName("구간 생성 성공 - 종점이 아닌 중간 상행에 구간 추가")
    @Test
    void createSectionAtUpStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "1");
        params.put("downStationId", "3");
        params.put("distance", "7");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        final String location = response.header("Location");
        assertThat(location).isNotBlank();

        final SectionResponse section = response.body().as(SectionResponse.class);
        assertThat(section.getLineId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(3L);
        assertThat(section.getUpStationId()).isEqualTo(1L);
        assertThat(section.getDistance()).isEqualTo(7);

        final LineResponse lineResponse = RestAssuredHelper.jsonGet("/lines/1").body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(gangnam, seolleung, yeoksam);
    }

    @DisplayName("구간 생성 성공 - 종점이 아닌 중간 하행에 구간 추가")
    @Test
    void createSectionAtBottomStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "2");
        params.put("distance", "7");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        final String location = response.header("Location");
        assertThat(location).isNotBlank();

        final SectionResponse section = response.body().as(SectionResponse.class);
        assertThat(section.getLineId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(2L);
        assertThat(section.getUpStationId()).isEqualTo(3L);
        assertThat(section.getDistance()).isEqualTo(7);

        RestAssuredHelper.jsonGet("/lines/1");
    }

    @DisplayName("구간 생성 실패 - 거리가 음수이면 예외 발생")
    @Test
    void createSectionWithMinusDistance() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("downStationId", "3");
        params.put("upStationId", "4");
        params.put("distance", "-1");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("0보다 커야 합니다");
    }

    @DisplayName("구간 생성 실패 - 저장되어 있지 않은 역을 입력할 경우 예외 발생")
    @Test
    void createSectionWithDidNotPersistStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "5");
        params.put("downStationId", "1");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("해당 ID와 일치하는 역이 존재하지 않습니다.");
    }

    @DisplayName("구간 생성 실패 - 이미 저장되어 있는 구간일 경우 예외 발생")
    @Test
    void createSectionWithAlreadyPersistedStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("이미 저장되어 있는 구간입니다.");
    }

    @DisplayName("구간 생성 실패 - 요청 구간의 역이 노선에 하나도 존재하지 않을 경우 예외 발생")
    @Test
    void createSectionWithMismatchStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "4");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason())
                .isEqualTo("적어도 구간의 하나의 역은 이미 다른 구간에 저장되어 있어야 합니다.");
    }

    @DisplayName("구간 생성 실패 - 추가하려는 구간의 길이가 기존 구간의 길이 이상일 경우 예외 발생")
    @Test
    void createSectionWithLessDistance() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "1");
        params.put("downStationId", "3");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("추가하려는 구간의 길이는 기존 구간의 길이보다 작아야 합니다.");
    }
}
