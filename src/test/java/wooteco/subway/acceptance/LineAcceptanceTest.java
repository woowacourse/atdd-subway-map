package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    Station station1;
    Station station2;
    Station station3;
    Station station4;
    Station station5;
    Station station6;
    Station station7;
    Station station8;
    Station station9;
    Station station10;

    @BeforeEach
    void setUpData() {

        station1 = createStation("강남역").as(Station.class);
        station2 = createStation("선릉역").as(Station.class);
        station3 = createStation("잠실역").as(Station.class);
        station4 = createStation("걸포역").as(Station.class);
        station5 = createStation("사우역").as(Station.class);
        station6 = createStation("홍대입구역").as(Station.class);
        station7 = createStation("서울역").as(Station.class);
        station8 = createStation("김포공항역").as(Station.class);
        station9 = createStation("당산역").as(Station.class);
        station10 = createStation("신촌역").as(Station.class);
    }


    @DisplayName("노선 생성을 관리한다")
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromCollection() {
        return Stream.of(
                dynamicTest("새로운 노선 이름으로 노선을 생성한다.", () -> {
                    // when
                    ExtractableResponse<Response> response = createLine("2호선", "green", station1.getId(), station2.getId(), 10);

                    // then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    assertThat(response.header("Location")).isNotBlank();
                }),

                dynamicTest("기존의 노선 이름으로 노선을 생성시 실패한다.", () -> {
                    // when
                    ExtractableResponse<Response> response = createLine("2호선", "green", station2.getId(), station3.getId(), 10);

                    // then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

//                dynamicTest("기존과 동일한 구간을 갖는 노선 추가 시 실패한다.", () -> {
//                    //when
//                    ExtractableResponse<Response> response = createLine("2호선", "green", station1.getId(), station2.getId(), 10);
//
//                    // then
//                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
//                }),

                dynamicTest("구간 생성시 예외 발생한 경우 노선도 생성되지 않음을 확인한다.", () -> {
                    //when
                    ExtractableResponse<Response> response = get("/lines");

                    //then
                    Long count = response.jsonPath().getList(".", LineResponse.class)
                            .stream()
                            .count();
                    assertThat(count).isOne();
                })
        );
    }

    @DisplayName("구간 생성을 관리한다")
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromSection() {
        AtomicReference<Long> lineId = new AtomicReference<>();
        return Stream.of(
                dynamicTest("[2호선 생성] 새로운 노선 이름으로 노선을 생성한다.", () -> {
                    // when
                    ExtractableResponse<Response> response = createLine("2호선", "green", station1.getId(), station2.getId(), 10);

                    // then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    assertThat(response.header("Location")).isNotBlank();
                    lineId.set(Long.parseLong(response.header("Location").split("/")[2]));
                }),

                dynamicTest("[2호선 구간 생성] 상행 종점에 구간을 추가한다.", () -> {
                    //when
                    ExtractableResponse<Response> response = createSection(lineId.get(), station3.getId(), station1.getId(), 10);

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("[2호선 구간 생성] 하행 종점에 구간을 추가한다.", () -> {
                    //when
                    ExtractableResponse<Response> response = createSection(lineId.get(), station2.getId(), station4.getId(), 8);

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("[2호선 구간 생성] 상행 갈림길로 구간을 추가한다.", () -> {
                    //when
                    ExtractableResponse<Response> response = createSection(lineId.get(), station1.getId(), station5.getId(), 8);

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("[2호선 구간 생성] 하행 갈림길로 구간을 추가한다.", () -> {
                    //when
                    ExtractableResponse<Response> response = createSection(lineId.get(), station6.getId(), station5.getId(), 5);

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("[2호선 구간 생성] 상-하행역이 구간에 존재하지 않는 경우 구간을 추가할 수 없다.", () -> {
                    //when
                    ExtractableResponse<Response> response = createSection(lineId.get(), station7.getId(), station8.getId(), 5);

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

                dynamicTest("[2호선 구간 생성] 기존 구간 길이보다 긴 길이의 구간을 추가할 수 없다.(상행 방향)", () -> {
                    //when
                    ExtractableResponse<Response> response = createSection(lineId.get(), station5.getId(), station7.getId(), 5);

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

                dynamicTest("[2호선 구간 생성] 기존 구간 길이보다 긴 길이의 구간을 추가할 수 없다.(하행 방향)", () -> {
                    //when
                    ExtractableResponse<Response> response = createSection(lineId.get(), station7.getId(), station2.getId(), 5);

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

                dynamicTest("[2호선 구간 생성] 기존 구간과 겹치는 구간은 추가할 수 없다.", () -> {
                    //when
                    ExtractableResponse<Response> response = createSection(lineId.get(), station1.getId(), station2.getId(), 5);

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

                dynamicTest("[2호선 구간 생성] 기존 구간과 동일한 구간은 추가할 수 없다.", () -> {
                    //when
                    ExtractableResponse<Response> response = createSection(lineId.get(), station1.getId(), station2.getId(), 10);

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }),

                dynamicTest("[3호선 생성] 다른 노선과 겹치는 구간을 생성할 수 있다.", () -> {
                    // when
                    ExtractableResponse<Response> response = createLine("3호선", "orange", station1.getId(), station2.getId(), 10);

                    // then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    assertThat(response.header("Location")).isNotBlank();
                }),

                dynamicTest("[2호선 구간 삭제] 상행 종점을 삭제한다.", () -> {
                    //when
                    ExtractableResponse<Response> response = deleteSection(lineId.get(), station3.getId());

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("[2호선 구간 삭제] 하행 종점을 삭제한다.", () -> {
                    //when
                    ExtractableResponse<Response> response = deleteSection(lineId.get(), station4.getId());

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),

                dynamicTest("[2호선 구간 삭제] 중간 지하철 역을 삭제한다.", () -> {
                    //when
                    ExtractableResponse<Response> response = deleteSection(lineId.get(), station6.getId());

                    //then
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                })
        );
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLine("2호선", "green", station1.getId(), station2.getId(), 10);
        ExtractableResponse<Response> createResponse2 = createLine("3호선", "orange", station4.getId(), station5.getId(), 10);

        // when
        ExtractableResponse<Response> response = get("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2)
                .stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = getLResultLineIds(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private List<Long> getLResultLineIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        ExtractableResponse<Response> createResponse = createLine("2호선", "green", station1.getId(), station2.getId(), 10);

        // when
        long expectedLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        ExtractableResponse<Response> response = get("/lines/" + expectedLineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        ExtractableResponse<Response> response = createLine("2호선", "green", station1.getId(), station2.getId(), 10);
        long savedLineId = Long.parseLong(response.header("Location").split("/")[2]);

        //when
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("name", "3호선");
        updateParams.put("color", "orange");

        ExtractableResponse<Response> updateResponse = put("/lines/" + savedLineId, updateParams);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine("2호선", "green", station1.getId(), station2.getId(), 10);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = delete(uri);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("제거할 지하철 노선이 없는 경우 예외가 발생한다.")
    @Test
    void deleteNotExistLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine("2호선", "green", station1.getId(), station2.getId(), 10);
        String uri = createResponse.header("Location");
        delete(uri);

        // when
        ExtractableResponse<Response> response = delete(uri);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> createLine(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return post("/lines", params);
    }

    private ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return post("/lines/" + lineId + "/sections", params);
    }

    private ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        return deleteWithQueryParam("/lines/" + lineId + "/sections", stationId);
    }
}
