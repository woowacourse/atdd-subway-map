package wooteco.subway.controller;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.*;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;

import static org.assertj.core.api.Assertions.assertThat;

class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationService stationService;
    @Autowired
    private LineService lineService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private SectionDao sectionDao;

    private StationResponse savedStation1;
    private StationResponse savedStation2;
    private StationResponse savedStation3;
    private StationResponse savedStation4;
    private StationResponse savedStation5;
    private LineResponse savedLine;

    private String createPath;
    private String deletePath;

    @BeforeEach
    void setUpStations() {
        savedStation1 = stationService.create(new StationRequest("선릉역"));
        savedStation2 = stationService.create(new StationRequest("선정릉역"));
        savedStation3 = stationService.create(new StationRequest("한티역"));
        savedStation4 = stationService.create(new StationRequest("모란역"));
        savedStation5 = stationService.create(new StationRequest("기흥역"));

        savedLine = lineService.create(new LineRequest("분당선", "yellow", savedStation1.getId(),
                savedStation2.getId(), 10));
        sectionDao.insert(new Section(savedLine.getId(), savedStation2.getId(), savedStation3.getId(), 10));

        createPath = "/lines/" + savedLine.getId() + "/sections/";
        deletePath = "/lines/" + savedLine.getId() + "/sections?stationId=";
    }

    @DisplayName("상행 종점에 역을 등록한다.")
    @Test
    void createStationAtLastUp() {
        SectionRequest request = new SectionRequest(savedStation4.getId(), savedStation1.getId(), 10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, createPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("하행 종점에 역을 등록한다.")
    @Test
    void createStationAtLastDown() {
        SectionRequest request = new SectionRequest(savedStation3.getId(), savedStation4.getId(), 10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, createPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간 사이에 새로운 구간을 등록한다.")
    @Test
    void createStationAtMiddle() {
        SectionRequest request = new SectionRequest(savedStation1.getId(), savedStation4.getId(), 7);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, createPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 역으로 구간 등록을 시도하면 400번 코드가 반환된다.")
    @Test
    void throwsExceptionWithNonExistStation() {
        SectionRequest request = new SectionRequest(100L, savedStation2.getId(), 10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, createPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 포함되지 않은 두 역으로 구간을 만들려고 시도하면 400번 코드가 반환된다.")
    @Test
    void throwsExceptionNotContainStationInLine() {
        SectionRequest request = new SectionRequest(savedStation4.getId(), savedStation5.getId(), 10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, createPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 노선에 존재하는 두 역으로 구간을 만들려고 시도하면 400번 코드가 반환된다.")
    @Test
    void throwsExceptionContainExistTwoStationInLine() {
        SectionRequest request = new SectionRequest(savedStation3.getId(), savedStation2.getId(), 10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, createPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("0이하의 거리로 구간 생성을 시도하면 400번 코드가 반환된다.")
    @Test
    void throwsExceptionWithNegativeDistance() {
        SectionRequest request = new SectionRequest(savedStation3.getId(), savedStation2.getId(), 0);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, createPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 사이에 새로운 구간을 등록할 때, 기존 구간보다 큰 거리로 생성을 시도하면 400번 코드가 반환된다.")
    @Test
    void throwsExceptionWithLongerDistance() {
        SectionRequest request = new SectionRequest(savedStation1.getId(), savedStation2.getId(), 10);

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, MediaType.APPLICATION_JSON_VALUE, createPath);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행 종점 역이 포함된 구간을 올바르게 삭제한다.")
    @Test
    void deleteSectionAtLastUpStation() {
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest(deletePath + savedStation1.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("하행 종점 역이 포함된 구간을 올바르게 삭제한다.")
    @Test
    void deleteSectionAtLastDownStation() {
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest(deletePath + savedStation3.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("중간 역이 포함된 구간을 올바르게 삭제한다.")
    @Test
    void deleteSectionAtMiddleStation() {
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest(deletePath + savedStation2.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간이 하나만 남았을 경우 400번 코드가 반환된다.")
    @Test
    void throwsExceptionWithOneRemainSection() {
        sectionService.delete(savedLine.getId(), savedStation1.getId());

        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest(deletePath + savedStation2.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("현재 라인에 존재하지 않는 역으로 삭제 시도시 예외가 발생한다.")
    @Test
    void throwsExceptionWithNotExistStationInLine() {
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest(deletePath + savedStation4.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
