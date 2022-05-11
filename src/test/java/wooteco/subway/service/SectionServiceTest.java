package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.BothUpAndDownStationAlreadyExistsException;
import wooteco.subway.exception.BothUpAndDownStationDoNotExistException;
import wooteco.subway.exception.CanNotInsertSectionException;
import wooteco.subway.exception.OnlyOneSectionException;

@JdbcTest
class SectionServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineService lineService;
    private StationService stationService;
    private SectionService sectionService;

    private LineResponse lineResponse;
    private StationResponse stationResponse1;
    private StationResponse stationResponse2;
    private StationResponse stationResponse3;
    private StationResponse stationResponse4;
    private StationResponse stationResponse5;

    @BeforeEach
    void setUp() {
        lineService = new LineService(new LineDao(jdbcTemplate), new SectionDao(jdbcTemplate));
        stationService = new StationService(new StationDao(jdbcTemplate));
        sectionService = new SectionService(new SectionDao(jdbcTemplate));

        stationResponse1 = stationService.createStation(new StationRequest("선릉역"));
        stationResponse2 = stationService.createStation(new StationRequest("삼성역"));
        stationResponse3 = stationService.createStation(new StationRequest("종합운동장역"));
        stationResponse4 = stationService.createStation(new StationRequest("잠실새내역"));
        stationResponse5 = stationService.createStation(new StationRequest("잠실역"));

        lineResponse = lineService.createLine(
                new LineRequest("2호선", "bg-green-600", stationResponse2.getId(), stationResponse4.getId(), 10));
    }

    @DisplayName("노선에 새로운 구간을 상행선 방향으로 추가")
    @Test
    void createSection_upStation() {
        // given
        Long lineId = lineResponse.getId();
        Long upStationId = stationResponse1.getId();
        Long downStationId = stationResponse2.getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 10);

        // when
        sectionService.createSection(lineId, sectionRequest);

        // then
        Sections sections = sectionService.getSectionsByLineId(lineId);
        boolean isCreatedSectionExisting = sections.getValue()
                .stream()
                .anyMatch(section -> section.getUpStationId().equals(sectionRequest.getUpStationId())
                        && section.getDownStationId().equals(sectionRequest.getDownStationId()));

        assertThat(isCreatedSectionExisting).isTrue();
    }

    @DisplayName("노선에 새로운 구간을 하행선 방향으로 추가")
    @Test
    void createSection_downStation() {
        // given
        Long lineId = lineResponse.getId();
        Long upStationId = stationResponse4.getId();
        Long downStationId = stationResponse5.getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 10);

        // when
        sectionService.createSection(lineId, sectionRequest);

        // then
        Sections sections = sectionService.getSectionsByLineId(lineId);
        boolean isCreatedSectionExisting = sections.getValue()
                .stream()
                .anyMatch(section -> section.getUpStationId().equals(sectionRequest.getUpStationId())
                        && section.getDownStationId().equals(sectionRequest.getDownStationId()));

        assertThat(isCreatedSectionExisting).isTrue();
    }

    @DisplayName("노선에 새로운 구간을 이미 존재하는 구간 사이에 삽입")
    @Test
    void createSection_inserting() {
        // given
        Long lineId = lineResponse.getId();
        Long upStationId = stationResponse2.getId();
        Long downStationId = stationResponse3.getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 5);

        // when
        sectionService.createSection(lineId, sectionRequest);

        // then
        Sections sections = sectionService.getSectionsByLineId(lineId);
        boolean isCreatedSectionExisting = sections.getValue()
                .stream()
                .anyMatch(section -> section.getUpStationId().equals(sectionRequest.getUpStationId())
                        && section.getDownStationId().equals(sectionRequest.getDownStationId()));

        assertThat(isCreatedSectionExisting).isTrue();
    }

    @DisplayName("삽입하려는 구간이 기존 구간보다 길이가 같거나 긴 경우 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(ints = {10, 11, 100})
    void createSection_throwsExceptionOnInsertingIfInsertedSectionIsLongerThanBaseSection(int distance) {
        // given
        Long lineId = lineResponse.getId();
        Long upStationId = stationResponse2.getId();
        Long downStationId = stationResponse3.getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);

        // when & then
        assertThatThrownBy(() -> sectionService.createSection(lineId, sectionRequest))
                .isInstanceOf(CanNotInsertSectionException.class);
    }

    @DisplayName("추가하려는 구간의 모든 역이 이미 구간 목록에 모두 존재할 경우 예외가 발생한다")
    @Test
    void createSection_throwsExceptionIfBothUpAndDownStationAreAlreadyExisting() {
        // given
        Long lineId = lineResponse.getId();
        Long upStationId = stationResponse2.getId();
        Long downStationId = stationResponse4.getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 10);

        // when & then
        assertThatThrownBy(() -> sectionService.createSection(lineId, sectionRequest))
                .isInstanceOf(BothUpAndDownStationAlreadyExistsException.class);
    }

    @DisplayName("추가하려는 구간의 모든 역이 구간 목록에 모두 존재하지 않을 경우 예외가 발생한다")
    @Test
    void createSection_throwsExceptionIfBothUpAndDownStationAreNotExisting() {
        // given
        Long lineId = lineResponse.getId();
        Long upStationId = stationResponse1.getId();
        Long downStationId = stationResponse5.getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 10);

        // when & then
        assertThatThrownBy(() -> sectionService.createSection(lineId, sectionRequest))
                .isInstanceOf(BothUpAndDownStationDoNotExistException.class);
    }

    @DisplayName("상행종점역 제거")
    @Test
    void deleteStationById_upStation() {
        // given
        Long lineId = lineResponse.getId();
        Long upStationId = stationResponse1.getId();
        Long downStationId = stationResponse2.getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 10);

        sectionService.createSection(lineId, sectionRequest);

        // when
        sectionService.deleteStationById(lineId, upStationId);

        // then
        int actual = sectionService.getSectionsByLineId(lineId).getValue().size();
        assertThat(actual).isOne();
    }

    @DisplayName("하행종점역 제거")
    @Test
    void deleteStationById_downStation() {
        // given
        Long lineId = lineResponse.getId();
        Long upStationId = stationResponse4.getId();
        Long downStationId = stationResponse5.getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 10);

        sectionService.createSection(lineId, sectionRequest);

        // when
        sectionService.deleteStationById(lineId, downStationId);

        // then
        int actual = sectionService.getSectionsByLineId(lineId).getValue().size();
        assertThat(actual).isOne();
    }

    @DisplayName("중간역 제거")
    @Test
    void deleteStationById_betweenStation() {
        // given
        Long lineId = lineResponse.getId();
        Long upStationId = stationResponse2.getId();
        Long downStationId = stationResponse3.getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 5);

        sectionService.createSection(lineId, sectionRequest);

        // when
        sectionService.deleteStationById(lineId, downStationId);

        // then
        int actual = sectionService.getSectionsByLineId(lineId).getValue().size();
        assertThat(actual).isOne();
    }

    @DisplayName("구간이 단 하나인 구간 목록에서 역을 제거하면 예외가 발생한다")
    @Test
    void deleteStationById_throwsExceptionIfSectionsSizeIsOne() {
        // given
        Long lineId = lineResponse.getId();
        Long deleteStationId = stationResponse2.getId();

        // when & then
        assertThatThrownBy(() -> sectionService.deleteStationById(lineId, deleteStationId))
                .isInstanceOf(OnlyOneSectionException.class);
    }
}
