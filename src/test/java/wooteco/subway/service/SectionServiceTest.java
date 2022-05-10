package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.service.ServiceTestFixture.동두천역_요청;
import static wooteco.subway.service.ServiceTestFixture.일호선_생성;
import static wooteco.subway.service.ServiceTestFixture.잠실역_요청;

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

    private Long stationId1, stationId2;
    private Long lineId;

    @BeforeEach
    void setUp() {
        stationId1 = stationService.insert(잠실역_요청).getId();
        stationId2 = stationService.insert(동두천역_요청).getId();

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
    @DisplayName("구간 삭제 시 입력한 id값의 노선이 없으면 에러를 발생시킨다.")
    void deleteErrorByLineNotExist() {
        assertThatThrownBy(() -> sectionService.delete(lineId+ 1, stationId1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("구간 삭제 시 입력한 id값의 지하철 역이 없으면 에러를 발생시킨다.")
    void deleteErrorByStationNotExist() {
        assertThatThrownBy(() -> sectionService.delete(lineId, stationId2 +1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 지하철역입니다.");
    }
}