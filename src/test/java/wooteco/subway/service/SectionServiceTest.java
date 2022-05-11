package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    private static final Long LINE_ID = 1L;

    @Mock
    private JdbcSectionDao jdbcSectionDao;

    @Mock
    private StationService stationService;

    @InjectMocks
    private SectionService sectionService;

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void createSection_역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없음() {

        long upStationId = 2L;
        long downStationId = 3L;
        doReturn(false)
                .when(jdbcSectionDao)
                .isExistByUpStationIdAndDownStationId(upStationId, downStationId);

        doReturn(true)
                .when(jdbcSectionDao)
                .isExistByLineIdAndUpStationId(LINE_ID, upStationId);

        doReturn(new Sections(List.of(
                new Section(LINE_ID, 1L, 2L,5),
                new Section(LINE_ID, 2L,5L,5)
        )))
                .when(jdbcSectionDao)
                .findByLineIdAndStationIds(LINE_ID, upStationId, downStationId);

        assertThatThrownBy(() -> sectionService.createSection(new SectionRequest(upStationId, downStationId, 5), LINE_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void createSection_상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
        doReturn(true)
                .when(jdbcSectionDao)
                .isExistByUpStationIdAndDownStationId(1L,2L);

        assertThatThrownBy(() -> sectionService.createSection(new SectionRequest(1L, 2L, 5), LINE_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void createSection_상행역과_하행역_둘_중_하나도_포함되어있지_않는_경우() {
        doReturn(false)
                .when(jdbcSectionDao)
                .isExistByUpStationIdAndDownStationId(2L,3L);

        doReturn(false)
                .when(jdbcSectionDao)
                .isExistByLineIdAndUpStationId(LINE_ID,2L);

        doReturn(false)
                .when(jdbcSectionDao)
                .isExistByLineIdAndDownStationId(LINE_ID,3L);

        assertThatThrownBy(() -> sectionService.createSection(new SectionRequest(2L, 3L, 5), LINE_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간을 등록한다.")
    @Test
    void saveSection() {
        Section section = new Section(1L, 1L, 2L, 5);

        doReturn(1L)
                .when(jdbcSectionDao)
                .save(any(Section.class));

        Long id = sectionService.saveSection(new SectionRequest(1L, 2L, 5), LINE_ID);

        assertThat(id).isEqualTo(1L);
    }

    @DisplayName("노선에 따른 지하철역을 조회한다.")
    @Test
    void getStationsByLineId() {
        doReturn(new Sections(List.of(
                new Section(1L, 1L, 2L, 5),
                new Section(1L, 2L, 3L, 5)

        )))
                .when(jdbcSectionDao)
                .findByLineId(1L);

        doReturn(new StationResponse(1L, "강남역"))
                .when(stationService)
                .getStation(1L);

        doReturn(new StationResponse(2L, "사당역"))
                .when(stationService)
                .getStation(2L);

        doReturn(new StationResponse(3L, "잠실역"))
                .when(stationService)
                .getStation(3L);

        List<StationResponse> stationResponses = sectionService.getStationsByLineId(1L);

        assertThat(stationResponses.size()).isEqualTo(3);
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void deleteSection() {
        Long lineId = 1L;
        Long stationId = 2L;

        doReturn(new Sections(List.of(
                new Section(lineId, 1L,stationId,5),
                new Section(lineId, stationId, 5L, 5)

        )))
                .when(jdbcSectionDao)
                .findByLineIdAndStationId(lineId, stationId);

        doReturn(true)
                .when(jdbcSectionDao)
                .deleteByLineIdAndUpStationId(lineId,stationId);

        doReturn(true)
                .when(jdbcSectionDao)
                .updateDownStationIdByLineIdAndUpStationId(lineId, 1L, 5L);

        boolean isDeleted = sectionService.deleteSection(lineId, stationId);
        assertThat(isDeleted).isTrue();
    }

    @DisplayName("구간을 삭제할 때 db에 구간 정보가 한 개이면 에러가 발생한다.")
    @Test
    void deleteInCaseOfException() {
        Long lineId = 1L;
        Long stationId = 2L;

        doReturn(new Sections(List.of(
                new Section(lineId, 1L,stationId,5)

        )))
                .when(jdbcSectionDao)
                .findByLineIdAndStationId(lineId, stationId);

        assertThatThrownBy(() -> sectionService.deleteSection(lineId, stationId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}