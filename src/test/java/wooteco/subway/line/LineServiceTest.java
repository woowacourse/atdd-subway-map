package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.LineEntity;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.exception.LineError;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class LineServiceTest {
    private static final Station 잠실역 = new Station(1L, "잠실역");
    private static final Station 강남역 = new Station(2L, "강남역");
    private static final Station 강변역 = new Station(3L, "강변역");

    private static final String LINE_NAME = "2호선";
    private static final String LINE_COLOR = "초록색";

    private static final LineRequest LINE_REQUEST = new LineRequest(LINE_NAME, LINE_COLOR, 잠실역.getId(), 강남역.getId(), 3);

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Mock
    private StationService stationService;

    @Mock
    private SectionService sectionService;


    @BeforeEach
    void setUp() {
        given(stationService.findById(1L)).willReturn(잠실역);
        given(stationService.findById(2L)).willReturn(강남역);
        given(stationService.findById(3L)).willReturn(강변역);
    }

    @Test
    @DisplayName("id로 노선 정보 조회")
    void findByName() {
        Long lineId = 1L;

        given(lineDao.findById(lineId)).willReturn(Optional.of(new LineEntity(1L, LINE_NAME, LINE_COLOR)));
        given(sectionService.sectionsByLineId(lineId)).willReturn(new Sections(Collections.singletonList(new Section(잠실역, 강남역, 3))));

        lineService.findById(lineId);

        verify(lineDao).findById(lineId);
    }

    @Test
    @DisplayName("노선 정상 생성")
    void createLine() {
        Long lineId = 1L;
        SectionRequest sectionRequest = SectionRequest.from(LINE_REQUEST);

        given(lineDao.save(LINE_NAME, LINE_COLOR)).willReturn(lineId);
        given(stationService.isPresent(LINE_REQUEST.getDownStationId())).willReturn(true);
        given(stationService.isPresent(LINE_REQUEST.getUpStationId())).willReturn(true);
        willDoNothing().given(sectionService)
                .initSection(lineId, sectionRequest);

        lineService.createLine(LINE_REQUEST);

        verify(lineDao).save(LINE_NAME, LINE_COLOR);
        verify(sectionService).initSection(lineId, sectionRequest);
    }

    @Test
    @DisplayName("노선 이름 중복 생성 실패")
    void createDuplicatedLine() {
        given(lineDao.findByName(LINE_NAME)).willReturn(Optional.of(new LineEntity(1L, LINE_NAME, LINE_COLOR)));

        assertThatThrownBy(() -> lineService.createLine(LINE_REQUEST))
                .isInstanceOf(LineException.class)
                .hasMessage(LineError.ALREADY_EXIST_LINE_NAME.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 역을 경로로 가지는 노선 생성 실패")
    void createLineWithNotExistStation() {
        Long notExistStationId = 9999L;
        given(stationService.isPresent(notExistStationId)).willReturn(false);

        LineRequest lineRequest = new LineRequest("2호선", "초록색", 잠실역.getId(), notExistStationId, 3);
        assertThatThrownBy(() -> lineService.createLine(lineRequest)).isInstanceOf(LineException.class)
                .hasMessage(LineError.NOT_EXIST_STATION_ON_LINE_REQUEST.getMessage());
    }

    @Test
    @DisplayName("노선 정보 업데이트 요청 확인")
    void updateLine() {
        String newName = "3호선";
        String newColor = "파란색";
        Long lineId = 3L;

        given(lineDao.findById(lineId)).willReturn(Optional.of(new LineEntity(lineId, LINE_NAME, LINE_COLOR)));

        LineRequest modifyRequest = new LineRequest(newName, newColor);
        lineService.modifyLine(lineId, modifyRequest);

        verify(lineDao).update(lineId, newName, newColor);
    }

    @Test
    @DisplayName("존재하지 않는 노선 업데이트 요청 에러 발생")
    void updateLineNotExist() {
        String newName = "3호선";
        String newColor = "파란색";

        Long notExistLindId = 9999L;
        given(lineDao.findById(notExistLindId)).willReturn(Optional.empty());

        LineRequest modifyRequest = new LineRequest(newName, newColor);

        assertThatThrownBy(() -> lineService.modifyLine(notExistLindId, modifyRequest)).isInstanceOf(LineException.class)
                .hasMessage(LineError.NOT_EXIST_LINE_ID.getMessage());
    }

    @Test
    @DisplayName("구간 추가")
    void addSection() {
        Long lineId = 1L;
        SectionRequest sectionRequest = SectionRequest.from(LINE_REQUEST);
        given(lineDao.findById(lineId)).willReturn(Optional.of(new LineEntity(lineId, LINE_NAME, LINE_COLOR)));

        lineService.addSection(lineId, sectionRequest);

        verify(sectionService).addSection(lineId, sectionRequest);
    }


    @Test
    @DisplayName("존재하지 않는 노선에 구간 추가")
    void addSectionToNotExistLine() {
        Long lineId = 1L;
        SectionRequest sectionRequest = SectionRequest.from(LINE_REQUEST);
        given(lineDao.findById(lineId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> lineService.addSection(lineId, sectionRequest)).isInstanceOf(LineException.class)
                .hasMessage(LineError.NOT_EXIST_LINE_ID.getMessage());
    }

    @Test
    @DisplayName("구간 삭제")
    void deleteSection() {
        Long lineId = 1L;
        Long stationId = 1L;
        given(lineDao.findById(lineId)).willReturn(Optional.of(new LineEntity(lineId, LINE_NAME, LINE_COLOR)));

        lineService.deleteSection(lineId, stationId);

        verify(sectionService).deleteSection(lineId, stationId);
    }


    @Test
    @DisplayName("존재하지 않는 노선에 구간 추가")
    void deleteSectionToNotExistLine() {
        Long lineId = 1L;
        Long stationId = 1L;
        given(lineDao.findById(lineId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> lineService.deleteSection(lineId, stationId)).isInstanceOf(LineException.class)
                .hasMessage(LineError.NOT_EXIST_LINE_ID.getMessage());
    }
}
