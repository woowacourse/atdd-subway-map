package wooteco.subway.section;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.line.LineAcceptanceTest.addLine;
import static wooteco.subway.line.LineAcceptanceTest.getLineResponses;
import static wooteco.subway.station.StationAcceptanceTest.addStation;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.dto.SectionResponse;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;
import wooteco.subway.station.dto.StationResponse;

@Sql("/truncate.sql")
@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    SectionDao sectionDao;

    @Autowired
    StationService stationService;

    private ExtractableResponse<Response> response;
    private Long upId;
    private Long middleId;
    private Long downId;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        upId = addStation("강남역").as(StationResponse.class).getId();
        middleId = addStation("도곡역").as(StationResponse.class).getId();
        downId = addStation("역삼역").as(StationResponse.class).getId();

        LineRequest lineRequest = new LineRequest("분당선", "bg-yellow-600", upId, downId, 5);
        response = addLine(lineRequest);

        SectionRequest sectionRequest = new SectionRequest(upId, middleId, 3);
        addSection(sectionRequest);
    }

    @DisplayName("종점이 아닌 역을 제거하면, 양끝에 역간의 거리가 합해진 새 구간이 생긴다.")
    @Test
    void deleteSectionNotEndPoint() {
        deleteStationOfSection(middleId);

        LineResponse lineResponse = getLineResponses().get(0);

        assertThat(sectionDao.findSectionsByLineId(lineResponse.getId()).get(0).getDistance())
            .isEqualTo(5);
    }

    @DisplayName("구간이 2개 이상일 때, 종점인 역을 제거한다.")
    @Test
    void deleteSectionEndPoint() {
        deleteStationOfSection(upId);

        LineResponse lineResponse = getLineResponses().get(0);
        List<SectionResponse> sectionList = sectionDao.findSectionsByLineId(lineResponse.getId());
        Sections sections = convertToSection(sectionList);
        List<Long> actual = convertToIds(sections.sortedStations());
        List<Long> expected = new LinkedList<>(Arrays.asList(middleId, downId));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("구간이 1개일 때, 종점인 역을 제거할 수 없다.")
    @Test
    void deleteUniqueSectionEndPoint() {
        deleteStationOfSection(upId);
        ExtractableResponse<Response> actualResponse = deleteStationOfSection(downId);
        assertThat(actualResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Sections convertToSection(List<SectionResponse> sectionList) {
        return sectionList.stream()
            .map(response -> new Section(response.getId()
                , stationService.findById(response.getUpStationId())
                , stationService.findById(response.getDownStationId())
                , response.getDistance()))
            .collect(collectingAndThen(toList(), Sections::new));
    }

    private List<Long> convertToIds(List<Station> sortedStations) {
        return sortedStations.stream()
            .map(Station::getId)
            .collect(toList());
    }

    private void addSection(SectionRequest sectionRequest) {
        RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/{id}/sections", response.header("Location").split("/")[2])
            .then().log().all();
    }

    private ExtractableResponse<Response> deleteStationOfSection(Long id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("stationId", id)
            .when()
            .delete("/lines/{id}/sections", response.header("Location").split("/")[2])
            .then().log().all()
            .extract();
    }
}
