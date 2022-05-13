package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptance.AcceptanceTestFixture.delete;
import static wooteco.subway.acceptance.AcceptanceTestFixture.getLineRequest;
import static wooteco.subway.acceptance.AcceptanceTestFixture.getSectionRequest;
import static wooteco.subway.acceptance.AcceptanceTestFixture.getStationRequest;
import static wooteco.subway.acceptance.AcceptanceTestFixture.insert;
import static wooteco.subway.acceptance.AcceptanceTestFixture.line1Post;
import static wooteco.subway.acceptance.AcceptanceTestFixture.sectionBetweenOneAndTwo;
import static wooteco.subway.acceptance.AcceptanceTestFixture.sectionBetweenThreeAndFour;
import static wooteco.subway.acceptance.AcceptanceTestFixture.sectionBetweenTwoAndThree;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setStations() {
        insert(getStationRequest("name1"), "/stations");
        insert(getStationRequest("name2"), "/stations");

        insert(getLineRequest(line1Post), "/lines");
    }

    @DisplayName("지하철 구간을 추가한다.")
    @Test
    void createSection() {
        // given
        insert(getStationRequest("name3"), "/stations");

        // when
        ExtractableResponse<Response> response = insert(getSectionRequest(sectionBetweenTwoAndThree),
                "/lines/1/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 노선의 id값을 입력한 경우 404에러를 발생시킨다.")
    @Test
    void createSectionLineNotExist() {
        // given
        insert(getStationRequest("name3"), "/stations");

        // when
        ExtractableResponse<Response> response = insert(getSectionRequest(sectionBetweenTwoAndThree),
                "/lines/2/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    @DisplayName("존재하지 않는 지하철역의 id값을 입력한 경우 404에러를 발생시킨다.")
    @Test
    void createSectionStationNotExist() {
        // given & when
        ExtractableResponse<Response> response = insert(getSectionRequest(sectionBetweenTwoAndThree),
                "/lines/1/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("기존에 존재하는 지하철 구간을 추가할 경우 400에러를 발생시킨다.")
    @Test
    void createSectionByStationExist() {
        // given & when
        ExtractableResponse<Response> response = insert(getSectionRequest(sectionBetweenOneAndTwo),
                "/lines/1/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하지 않는 지하철 구간을 추가할 경우 400에러를 발생시킨다.")
    @Test
    void createSectionByStationNotExist() {
        // given
        insert(getStationRequest("name3"), "/stations");
        insert(getStationRequest("name4"), "/stations");

        //when
        ExtractableResponse<Response> response = insert(getSectionRequest(sectionBetweenThreeAndFour),
                "/lines/1/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        insert(getStationRequest("name3"), "/stations");
        insert(getSectionRequest(sectionBetweenTwoAndThree), "/lines/1/sections");

        // when
        ExtractableResponse<Response> response = delete("/lines/1/sections", 1L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 line의 id값을 입력할 경우 404에러를 발생시킨다.")
    @Test
    void deleteSectionNotExistLine() {
        // given
        insert(getStationRequest("name3"), "/stations");
        insert(getSectionRequest(sectionBetweenTwoAndThree), "/lines/1/sections");

        // when
        ExtractableResponse<Response> response = delete("/lines/2/sections", 1L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 구간에 해당하는 역이 두개 뿐일 경우 400코드를 보낸다.")
    @Test
    void deleteSectionWhenSectionIsOnlyOne() {
        // given & when
        ExtractableResponse<Response> response = delete("/lines/1/sections", 1L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
