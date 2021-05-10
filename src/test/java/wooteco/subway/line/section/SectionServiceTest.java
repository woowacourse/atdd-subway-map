package wooteco.subway.line.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {
//
//    @InjectMocks
//    private SectionService sectionService;
//
//    @Mock
//    private SectionDao sectionDao;
//
//    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
//    @Test
//    void createSectionFail_alreadyExistent() {
//        final Long lineId = 1L;
//        final Long upStationId = 3L;
//        final Long downStationId = 2L;
//        final Section section = new Section(lineId, upStationId, downStationId, 10);
//        given(sectionDao.findByLineId(lineId)).willReturn(Collections.singletonList(section));
//
//        final SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 10);
//
//        assertThatThrownBy(() -> sectionService.createSection(lineId, sectionRequest))
//            .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.")
//            .isInstanceOf(RuntimeException.class);
//
//        verify(sectionDao, times(1)).findByLineId(lineId);
//    }
//
//    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
//    @Test
//    void createSectionFail_notExistent() {
//        final Long lineId = 1L;
//        final Long upStationId = 3L;
//        final Long downStationId = 2L;
//        final Section section = new Section(lineId, upStationId, downStationId, 10);
//        given(sectionDao.findByLineId(lineId)).willReturn(Collections.singletonList(section));
//
//        final SectionRequest sectionRequest = new SectionRequest(5L, 6L, 10);
//
//        assertThatThrownBy(() -> sectionService.createSection(lineId, sectionRequest))
//            .hasMessage("상행역과 하행역 둘 다 포함되어있지 않습니다.")
//            .isInstanceOf(RuntimeException.class);
//
//        verify(sectionDao, times(1)).findByLineId(lineId);
//    }
//
//    @DisplayName("새로운 구간이 종점 앞에 위치할 수 있는지 확인한다.")
//    @Test
//    void createSection_end_point() {
//        final Long lineId = 1L;
//        final Long upStationId = 3L;
//        final Long downStationId = 2L;
//        final Section section = new Section(lineId, upStationId, downStationId, 10);
//        given(sectionDao.findByLineId(lineId)).willReturn(Collections.singletonList(section));
//
//        final Section expected = new Section(2L, 5L, 3L, 10);
//        final SectionRequest sectionRequest = new SectionRequest(5L, 3L, 10);
//        given(sectionDao.save(sectionRequest.toEntity(lineId))).willReturn(expected);
//
//        assertThat(sectionService.createSection(lineId, sectionRequest).getUpStationId())
//            .isEqualTo(expected.getUpStationId());
//
//        verify(sectionDao, times(1)).findByLineId(lineId);
//        verify(sectionDao, times(1)).save(sectionRequest.toEntity(lineId));
//    }

    /*
    @DisplayName("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void createSection_distance_over() {
        final Long lineId = 1L;
        final Long upStationId = 3L;
        final Long downStationId = 2L;
        final Section section = new Section(lineId, upStationId, downStationId, 10);
        given(sectionDao.findByLineId(lineId)).willReturn(Collections.singletonList(section));

        final Section expected = new Section(2L, 3L, 5L, 12);
        final SectionRequest sectionRequest = new SectionRequest(3L, 5L, 12);
        given(sectionDao.save(sectionRequest.toEntity(lineId))).willReturn(expected);

        assertThatThrownBy(() -> sectionService.createSection(lineId, sectionRequest))
            .hasMessage("새로 추가할 구간의 거리가 기존 구간의 거리보다 크거나 같으면 안 됩니다.")
            .isInstanceOf(RuntimeException.class);

        verify(sectionDao, times(1)).findByLineId(lineId);
        verify(sectionDao, times(1)).save(sectionRequest.toEntity(lineId));
    }
    */
}
