package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.service.ServiceTestFixture.동두천역_요청;
import static wooteco.subway.service.ServiceTestFixture.선릉역_요청;
import static wooteco.subway.service.ServiceTestFixture.일호선_생성;
import static wooteco.subway.service.ServiceTestFixture.잠실역_요청;
import static wooteco.subway.service.ServiceTestFixture.지행역_요청;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.NotFoundException;

@SpringBootTest
@Transactional
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    LineService lineService;

    @Autowired
    StationService stationService;

    private long stationId1, stationId2;
    private long lineId;

    @BeforeEach
    void setUp() {
        stationId1 = stationService.insert(잠실역_요청).getId();
        stationId2 = stationService.insert(선릉역_요청).getId();

        lineId = lineService.insert(일호선_생성(stationId1, stationId2)).getId();
    }

    @Test
    @DisplayName("구간 등록 시 입력한 id값의 노선이 없으면 에러를 발생시킨다.")
    void insertErrorByLineNotExist() {
        assertThatThrownBy(() -> sectionService.insert(new SectionRequest(stationId1, stationId2, 10), lineId + 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("구간 등록 시 입력한 id값의 지하철 역이 없으면 에러를 발생시킨다.")
    void insertErrorByStationNotExist() {
        assertThatThrownBy(() -> sectionService.insert(new SectionRequest(stationId1, stationId2 + 1, 10), lineId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 지하철역입니다.");
    }

    @Test
    @DisplayName("구간 등록 시 입력한 station 값이 같으면 에러를 발생시킨다.")
    void insertErrorBySameStationId() {
        assertThatThrownBy(() -> sectionService.insert(new SectionRequest(stationId1, stationId1, 10), lineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행과 하행의 지하철 역이 같을 수 없습니다.");
    }

    @Test
    @DisplayName("구간 등록 시 입력한 distance 값이 0이라면 에러를 발생시킨다.")
    void insertErrorByDistanceUnderZero() {
        assertThatThrownBy(() -> sectionService.insert(new SectionRequest(stationId1, stationId2, -1), lineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 양수여야 합니다.");
    }

    @Test
    @DisplayName("구간 등록 시 입력한 section 내의 stationId 값이 모두 기존에 존재하는 경우 에러를 발생시킨다.")
    void insertErrorByDistanceAlreadyContainsAll() {
        assertThatThrownBy(() -> sectionService.insert(new SectionRequest(stationId1, stationId2, 10), lineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.");
    }

    @Test
    @DisplayName("구간 등록 시 입력한 section 내의 stationId 값이 모두 기존에 존재하지 않는 경우 에러를 발생시킨다.")
    void insertErrorByDistanceNotContains() {
        //given
        long stationId3 = stationService.insert(동두천역_요청).getId();
        long stationId4 = stationService.insert(지행역_요청).getId();

        //then
        assertThatThrownBy(() -> sectionService.insert(new SectionRequest(stationId3, stationId4, 10), lineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 모두 노선에 등록되어 있지 않습니다.");
    }

    @Test
    @DisplayName("구간 삭제 시 입력한 id값의 노선이 없으면 에러를 발생시킨다.")
    void deleteErrorByLineNotExist() {
        assertThatThrownBy(() -> sectionService.delete(lineId + 1, stationId1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("구간 삭제 시 입력한 id값의 지하철 역이 없으면 에러를 발생시킨다.")
    void deleteErrorByStationNotExist() {
        assertThatThrownBy(() -> sectionService.delete(lineId, stationId2 + 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 지하철역입니다.");
    }

    @Test
    @DisplayName("구간 삭제 시 구간이 하나뿐이라면 에러를 발생시킨다.")
    void deleteErrorBySectionSizeIsOne() {
        assertThatThrownBy(() -> sectionService.delete(lineId, stationId2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 노선은 더 삭제할 수 없습니다.");
    }
}