package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.LineService;

@DisplayName("섹션 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineService lineService;

    @DisplayName("섹션을 등록하면 200 Ok를 반환한다.")
    @Test
    void createSection() {
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));

        LineResponse lineResponse = lineService.create(
                new LineRequest("2호선", "bg-green-200", 강남역.getId(), 역삼역.getId(), 5));
        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 선릉역.getId(), 4);
        ExtractableResponse<Response> response = httpPostTest(sectionRequest,
                "/lines/" + lineResponse.getId() + " /sections");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("섹션을 삭제하면 200 Ok를 반환한다.")
    @Test
    void deleteSection() {
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));

        LineResponse lineResponse = lineService.create(
                new LineRequest("2호선", "bg-green-200", 강남역.getId(), 역삼역.getId(), 5));
        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 선릉역.getId(), 4);
        httpPostTest(sectionRequest, "/lines/" + lineResponse.getId() + " /sections");

        ExtractableResponse<Response> response = httpDeleteTest(
                "/lines/" + lineResponse.getId() + "/sections?stationId=" + 선릉역.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
