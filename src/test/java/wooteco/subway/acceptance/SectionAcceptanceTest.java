package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.StationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 관련 Api")
public class SectionAcceptanceTest extends AcceptanceTest {

    private long 강남역_ID;
    private long 역삼역_ID;
    private long 선릉역_ID;
    private long 노선_ID;

    @BeforeEach
    public void setUpData() {
        강남역_ID = createStation("강남역");
        역삼역_ID = createStation("역삼역");
        선릉역_ID = createStation("선릉역");
        createStation("잠실역");
        노선_ID = createLine("2호선", "bg-red-600",  강남역_ID, 역삼역_ID, 10);
    }

    @Nested
    @DisplayName("구간 생성 Api는")
    class Describe_section_create_api {

        @Nested
        @DisplayName("정상적으로 구간을 연결한다면")
        class Context_add_section_down_station {

            @Test
            @DisplayName("성공적으로 구간이 생성된다.")
            void it_create_new_section() {
                List<Section> beforeSection = getSections();
                ExtractableResponse<Response> response = createSection(역삼역_ID, 선릉역_ID, 5);
                List<Section> afterSection = getSections();

                assertThat(afterSection.size()).isEqualTo(beforeSection.size() + 1);
            }

            @Test
            @DisplayName("200 응답 코드를 반환한다.")
            void it_return_ok_status_code() {
                ExtractableResponse<Response> response = createSection(역삼역_ID, 선릉역_ID, 5);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            }
        }

        @Nested
        @DisplayName("이미 등록된 상행과 하행을 연결하는 구간을 추가한다면")
        class Context_add_duplicate {
            @Test
            @DisplayName("400 응답 코드를 반환한다.")
            void it_return_bad_status_code() {
                ExtractableResponse<Response> response = createSection(강남역_ID, 역삼역_ID, 5);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 상행과 하행을 연결하는 구간을 추가한다면")
        class Context_add_no_exist {
            @Test
            @DisplayName("400 응답 코드를 반환한다.")
            void it_return_bad_status_code() {
                ExtractableResponse<Response> response = createSection(10L, 11L, 5);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }

        @Nested
        @DisplayName("갈래길을 추가한다면")
        class Context_add_with_fork {
            @Test
            @DisplayName("200 응답 코드를 반환한다.")
            void it_return_ok_status_code() {
                ExtractableResponse<Response> response = createSection(강남역_ID, 선릉역_ID, 5);

                Section section = getSections().get(1);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            }

            @Test
            @DisplayName("기존 구간에 해당 구간을 넣는다.")
            void it_insert_section() {
                ExtractableResponse<Response> response = createSection(강남역_ID, 선릉역_ID, 5);

                Section section = getSections().get(1);
                assertAll(
                        () -> assertThat(section.getUpStationId()).isEqualTo(선릉역_ID),
                        () -> assertThat(section.getDownStationId()).isEqualTo(역삼역_ID)
                );
            }
        }

        @Nested
        @DisplayName("길이가 더 긴 갈래길이 추가된다면")
        class Context_add_with_fork_exceed_distance {
            @Test
            @DisplayName("400 응답 코드를 반환한다.")
            void it_return_bad_request_status_code() {
                ExtractableResponse<Response> response = createSection(강남역_ID, 선릉역_ID, 1000);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }
    }

    @Nested
    @DisplayName("구간 제거 Api는")
    class Describe_Section_Delete_Api {
        @Nested
        @DisplayName("종점인 역을 제거하는 경우")
        class Context_delete_no_endPoint {
            @Test
            @DisplayName("200 응답 코드를 반환한다.")
            void it_return_ok_status_code() {
                createSection(역삼역_ID, 선릉역_ID, 1);
                ExtractableResponse<Response> response = deleteSection(강남역_ID);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            }

            @Test
            @DisplayName("해당 역을 제거한다.")
            void it_delete_stations() {
                createSection(역삼역_ID, 선릉역_ID, 1);
                ExtractableResponse<Response> response = deleteSection(강남역_ID);
                Section section = getSections().get(0);
                assertAll(
                        () -> assertThat(getSections().size()).isEqualTo(1),
                        () -> assertThat(section.getUpStationId()).isEqualTo(2),
                        () -> assertThat(section.getDownStationId()).isEqualTo(3)
                );
            }
        }

        @Nested
        @DisplayName("단 2개의 역만 있는 경우 삭제한다면")
        class Context_delete_only_two_stations {
            @Test
            @DisplayName("400 응답 코드를 반환한다.")
            @DirtiesContext
            void it_return_bad_request_status_code() {
                ExtractableResponse<Response> response = deleteSection(강남역_ID);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 역을 삭제한다면")
        class Context_delete_not_exist_stations {
            @Test
            @DisplayName("400 응답 코드를 반환한다.")
            @DirtiesContext
            void it_return_bad_request_status_code() {
                ExtractableResponse<Response> response = deleteSection(강남역_ID);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }

        @Nested
        @DisplayName("상행과 하행에 모두 걸쳐있는 역을 삭제한다면")
        class Context_delete_Overlap_up_and_down {
            @Test
            @DisplayName("200 응답 코드를 반환한다.")
            @DirtiesContext
            void it_return_ok_status_code() {
                createSection(역삼역_ID, 선릉역_ID, 5);
                ExtractableResponse<Response> response = deleteSection(역삼역_ID);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            }

            @Test
            @DisplayName("삭제된 역을 기준으로 두 구간을 통합한다.")
            @DirtiesContext
            void it_combine_sections() {
                createSection(역삼역_ID, 선릉역_ID, 5);
                ExtractableResponse<Response> response = deleteSection(역삼역_ID);
                Section section = getSections().get(0);
                assertAll(
                        () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                        () -> assertThat(section.getUpStationId()).isEqualTo(강남역_ID),
                        () -> assertThat(section.getDownStationId()).isEqualTo(선릉역_ID)
                );
            }
        }
    }

    private List<Section> getSections() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + 노선_ID)
                .then().log().all()
                .extract();

        List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);

        List<Section> sections = new ArrayList<>();

        for (int i = 1; i < stations.size(); i++) {
            sections.add(new Section(강남역_ID, stations.get(i - 1).getId(), stations.get(i).getId(), 5));
        }

        return sections;
    }

    private ExtractableResponse<Response> deleteSection(final Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + 노선_ID + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createSection(final Long upStationId, final Long downStationId, final Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/"+노선_ID+"/sections")
                .then().log().all()
                .extract();
    }

    private Long createLine(final String name, final String color, final Long upStationId,
                            final Long downStationId, final Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        return Long.parseLong(response.header("location").split("/")[2]);
    }

    private Long createStation(final String name) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        return Long.parseLong(response.header("location").split("/")[2]);
    }
}
