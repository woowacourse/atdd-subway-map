package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.service.ServiceTestFixture.동두천역_요청;
import static wooteco.subway.service.ServiceTestFixture.일호선_생성;
import static wooteco.subway.service.ServiceTestFixture.잠실역_요청;

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

    @Test
    @DisplayName("입력한 id값의 노선이 없으면 에러를 발생시킨다.")
    void insertErrorByLineNotExist() {
        //given
        Long id1 = stationService.insert(잠실역_요청).getId();
        Long id2 = stationService.insert(동두천역_요청).getId();

        Long lineId = lineService.insert(일호선_생성(id1, id2)).getId();

        //when & then
        assertThatThrownBy(() -> sectionService.insert(new SectionRequest(id1, id2, 10), lineId + 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("입력한 id값의 지하철 역이 없으면 에러를 발생시킨다.")
    void insertErrorByStationNotExist() {
        //given
        Long id1 = stationService.insert(잠실역_요청).getId();
        Long id2 = stationService.insert(동두천역_요청).getId();

        Long lineId = lineService.insert(일호선_생성(id1, id2)).getId();

        //when & then
        assertThatThrownBy(() -> sectionService.insert(new SectionRequest(id1, id2 + 1, 10), lineId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 지하철역입니다.");
    }
}