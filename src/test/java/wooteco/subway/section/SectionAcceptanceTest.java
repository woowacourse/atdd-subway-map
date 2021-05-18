package wooteco.subway.section;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

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
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.SectionDto;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;
import wooteco.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    SectionDao sectionDao;

    @Autowired
    LineDao lineDao;

    @Autowired
    StationService stationService;

    private Long upId;
    private Long middleId;
    private Long downId;
    private Long lineId;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        upId = 강남역_response.as(StationResponse.class).getId();
        middleId = 역삼역_response.as(StationResponse.class).getId();
        downId = 도곡역_response.as(StationResponse.class).getId();

        LineRequest lineRequest = new LineRequest("분당선", "bg-yellow-600", upId, downId, 5);
        ExtractableResponse<Response> response = postLine(lineRequest);
        lineId = response.as(LineResponse.class).getId();

        SectionRequest sectionRequest = new SectionRequest(upId, middleId, 3);
        postSection(sectionRequest, lineId);
    }

    @DisplayName("종점이 아닌 역을 제거하면, 양끝에 역간의 거리가 합해진 새 구간이 생긴다.")
    @Test
    void deleteSectionNotEndPoint() {
        deleteSection(middleId, lineId);
        assertThat(sectionDao.findSectionsByLineId(lineId).get(0).getDistance())
            .isEqualTo(5);
    }

    @DisplayName("구간이 2개 이상일 때, 종점인 역을 제거한다.")
    @Test
    void deleteSectionEndPoint() {
        deleteSection(upId, lineId);

        List<SectionDto> sectionList = sectionDao.findSectionsByLineId(lineId);
        Sections sections = convertToSection(sectionList);
        List<Long> actual = convertToIds(sections.sortedStations());
        List<Long> expected = new LinkedList<>(Arrays.asList(middleId, downId));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("구간이 1개일 때, 종점인 역을 제거할 수 없다.")
    @Test
    void deleteUniqueSectionEndPoint() {
        deleteSection(upId, lineId);
        ExtractableResponse<Response> actualResponse = deleteSection(downId, lineId);
        assertThat(actualResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Sections convertToSection(List<SectionDto> sectionList) {
        return sectionList.stream()
            .map(response -> new Section(response.getId()
                , lineDao.findById(response.getLineId())
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
}
