package wooteco.subway.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.dto.SectionRequest;

@DisplayName("지하철 구간 관련 기능")
@Sql("classpath:stations.sql")
public class SectionAcceptanceTest extends AcceptanceTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @DisplayName("구간 생성 - 성공(up-middle)")
    @Test
    public void createSection_1() throws Exception {
        //given
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 5);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(OBJECT_MAPPER.writeValueAsString(sectionRequest))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("구간 생성 - 성공(middle-down)")
    @Test
    public void createSection_2() throws Exception {
        //given
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 5);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(OBJECT_MAPPER.writeValueAsString(sectionRequest))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("구간 생성 - 성공(last)")
    @Test
    public void createSection_3() throws Exception {
        //given
        SectionRequest sectionRequest = new SectionRequest(3L, 4L, 5);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(OBJECT_MAPPER.writeValueAsString(sectionRequest))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("구간 생성 - 실패(이미 등록된 구간)")
    @Test
    public void createSectionFail_1() throws Exception {
        //given
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 10);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(OBJECT_MAPPER.writeValueAsString(sectionRequest))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 생성 - 실패(지하철 역이 등록되지 않은 구간)")
    @Test
    public void createSectionFail_2() throws Exception {
        //given
        SectionRequest sectionRequest = new SectionRequest(3L, 7L, 10);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(OBJECT_MAPPER.writeValueAsString(sectionRequest))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 생성 - 실패(동일한 역 간의 구간)")
    @Test
    public void createSectionFail_3() throws Exception {
        //given
        SectionRequest sectionRequest = new SectionRequest(4L, 4L, 10);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(OBJECT_MAPPER.writeValueAsString(sectionRequest))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 생성 - 실패(기존 거리를 초과한 구간)")
    @Test
    public void createSectionFail_4() throws Exception {
        //given
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 15);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(OBJECT_MAPPER.writeValueAsString(sectionRequest))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 삭제 성공")
    @Test
    public void deleteSection()throws Exception{
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 5);
        RestAssured.given().body(OBJECT_MAPPER.writeValueAsString(sectionRequest))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/1/sections")
            .then().log().all();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/1/sections?stationId=2")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("구간 삭제 실패")
    @Test
    public void deleteSectionFail() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/1/sections?stationId=1")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}