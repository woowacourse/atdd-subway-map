package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {

    @InjectMocks
    private SectionService sectionService;

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationDao stationDao;

    @DisplayName("기존에 존재하는 노선에 하행 종점 구간을 등록한다.")
    @Test
    void addSectionInLine_DownStation() {
        // given
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 2L, 10))
            ));
        sectionService.addSection(1L, sectionRequest);
        // then
        verify(sectionDao).save(new Section(2L, 3L, 10));
    }

    @DisplayName("기존에 존재하는 노선에 상행 종점 구간을 등록한다.")
    @Test
    void addSectionInLine_UpStation() {
        // given
        SectionRequest sectionRequest = new SectionRequest(3L, 1L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 2L, 10))
            ));
        sectionService.addSection(1L, sectionRequest);
        // then
        verify(sectionDao).save(new Section(3L, 1L, 10));
    }

    @DisplayName("기존에 존재하는 노선의 구간 사이에 새로운 구간을 등록한다.")
    @Test
    void addSectionInLine_way_point() {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 5);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 1L, 1L, 2L, 10))
            ));
        sectionService.addSection(1L, sectionRequest);
        // then
        verify(sectionDao).delete(1L);
        verify(sectionDao).save(new Section(1L, 3L, 5));
        verify(sectionDao).save(new Section(3L, 2L, 5));
    }

    @DisplayName("기존에 존재하는 노선의 구간 사이에 상행 기준으로 새로운 구간을 등록할 때, 길이가 크거나 같다면 예외를 발생한다.")
    @Test
    void addSectionInLine_up_way_point_exception() {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 1L, 1L, 2L, 10))
            ));
        // then
        assertThatThrownBy(() -> sectionService.addSection(1L, sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("[ERROR] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
    }

    @DisplayName("기존에 존재하는 노선의 구간 사이에 하행 기준으로 새로운 구간을 등록할 때, 길이가 크거나 같다면 예외를 발생한다.")
    @Test
    void addSectionInLine_down_way_point_exception() {
        // given
        SectionRequest sectionRequest = new SectionRequest(3L, 2L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 1L, 1L, 2L, 10))
            ));
        // then
        assertThatThrownBy(() -> sectionService.addSection(1L, sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("[ERROR] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
    }

    @DisplayName("기존에 존재하는 노선의 구간 사이에 하행 기준으로 새로운 구간을 등록할 때 일치하는 상,하행 지하철 역이 없다면 예외를 발생한다.")
    @Test
    void addSectionInLine_no_exist_station_exception() {
        // given
        SectionRequest sectionRequest = new SectionRequest(3L, 4L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 1L, 1L, 2L, 10))
            ));
        // then
        assertThatThrownBy(() -> sectionService.addSection(1L, sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("[ERROR] 상,하행 역이 모두 구간에 존재하지 않는다면 추가할 수 없습니다.");
    }

    @DisplayName("기존에 존재하는 노선의 구간 사이에 하행 기준으로 새로운 구간을 등록할 때 일치하는 상,하행 지하철 역이 없다면 예외를 발생한다.")
    @Test
    void addSectionInLine_exist_duplicate_station_exception() {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 10);
        given(sectionDao.findByLineId(1L))
            .willReturn(new Sections(
                List.of(new Section(1L, 1L, 1L, 2L, 10)
                    , new Section(2L, 1L, 2L, 3L, 10))
            ));
        // then
        assertThatThrownBy(() -> sectionService.addSection(1L, sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("[ERROR] 상,하행 역이 구간에 모두 포함된 경우 추가할 수 없습니다.");
    }
}
