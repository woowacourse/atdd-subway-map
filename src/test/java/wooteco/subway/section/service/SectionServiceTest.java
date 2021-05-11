package wooteco.subway.section.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.section.Section;
import wooteco.subway.section.dao.JdbcSectionDao;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.AddSectionForm;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.dto.response.SectionCreateResponse;
import wooteco.subway.section.dto.response.SectionResponse;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@DisplayName("지하철 구간 비즈니스 로직 테스트")
@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    private final SectionDao sectionDao = Mockito.mock(JdbcSectionDao.class);

    @InjectMocks
    private SectionService sectionService;

    @DisplayName("지하철 구간 생성")
    @Test
    void save() {
        // given
        LineCreateResponse 분당선 = new LineCreateResponse(1L, "분당선", "red");
        SectionCreateRequest section = new SectionCreateRequest(1L, 2L, 3);
        given(sectionDao.save(any(Section.class)))
                .willReturn(new Section(1L, 1L, 1L, 2L, 3));

        // when
        SectionCreateResponse newSection = sectionService.save(분당선.getId(), section);

        // then
        assertThat(newSection).usingRecursiveComparison()
                .isEqualTo(new SectionCreateResponse(1L, 1L, 1L, 2L, 3));
        verify(sectionDao).save(any(Section.class));
    }

    @DisplayName("호선 id를 통해 호선의 모든 구간 찾기")
    @Test
    void findAllByLineId() {
        // given
        Long lineId = 1L;
        given(sectionDao.findAllByLineId(any(Long.class)))
                .willReturn(Arrays.asList(
                        new Section(1L, 1L, 1L, 2L, 3),
                        new Section(2L, 1L, 2L, 3L, 4)
                ));

        // when
        List<SectionResponse> result = sectionService.findAllByLineId(lineId);

        // then
        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(Arrays.asList(
                        new SectionResponse(1L, 2L),
                        new SectionResponse(2L, 3L)
                ));
    }

    @DisplayName("존재하는 노선에 구간 종점에 추가")
    @Test
    void addSectionEndPoint() {
        // given
        given(sectionDao.findAllByLineId(1L))
                .willReturn(Arrays.asList(
                        new Section(1L, 1L, 1L, 2L, 5),
                        new Section(2L, 1L, 2L, 3L, 5)
                ));
        given(sectionDao.save(any(Section.class)))
                .willReturn(new Section());

        // when
        sectionService.addSection(new AddSectionForm(1L,
                new StationResponse(4L, "강남역"),
                new StationResponse(1L, "잠실역")
                , 5));
        // then
        verify(sectionDao).save(any(Section.class));
    }

    @DisplayName("존재하는 노선에 구간 중간에 추가")
    @Test
    void addSectionMiddlePoint() {
        // given
        given(sectionDao.findAllByLineId(1L))
                .willReturn(Arrays.asList(
                        new Section(1L, 1L, 1L, 2L, 5),
                        new Section(2L, 1L, 2L, 3L, 5)
                ));
        given(sectionDao.save(any(Section.class)))
                .willReturn(new Section());

        // when
        sectionService.addSection(new AddSectionForm(1L,
                new StationResponse(2L, "강남역"),
                new StationResponse(4L, "잠실역")
                , 1));
        // then
        verify(sectionDao).updateUpStation(any(Section.class), any(Long.class));
        verify(sectionDao).save(any(Section.class));
    }

    @DisplayName("존재하는 노선에 이미 존재하는 구간이 들어오는 경우")
    @Test
    void addSectionBothInclude() {
        // given
        given(sectionDao.findAllByLineId(1L))
                .willReturn(Arrays.asList(
                        new Section(1L, 1L, 1L, 2L, 3),
                        new Section(1L, 1L, 2L, 3L, 3)
                ));

        // when & then
        assertThatThrownBy(() -> sectionService.addSection(new AddSectionForm(1L,
                new StationResponse(1L, "강남역"),
                new StationResponse(2L, "잠실역")
                , 5)
        )).isInstanceOf(SubwayException.class);
    }

    @DisplayName("존재하는 노선에 이을 수 없는 구간이 들어오는 경우")
    @Test
    void addSectionNotInclude() {
        // given
        given(sectionDao.findAllByLineId(1L))
                .willReturn(Arrays.asList(
                        new Section(1L, 1L, 1L, 2L, 3),
                        new Section(1L, 1L, 2L, 3L, 3)
                ));

        // when & then
        assertThatThrownBy(() -> sectionService.addSection(new AddSectionForm(1L,
                new StationResponse(4L, "강남역"),
                new StationResponse(5L, "잠실역")
                , 5)
        )).isInstanceOf(SubwayException.class);
    }

    @DisplayName("지하철 종점 삭제")
    @Test
    void deleteUpEndPointSection() {
        // given
        given(sectionDao.findAllByLineId(1L))
                .willReturn(Arrays.asList(
                        new Section(1L, 1L, 1L, 2L, 3),
                        new Section(1L, 1L, 2L, 3L, 3),
                        new Section(1L, 1L, 3L, 4L, 3)
                ));
        given(sectionDao.save(any(Section.class)))
                .willReturn(new Section());

        // when
        sectionService.deleteSection(1L, 1L);
        sectionService.deleteSection(1L, 4L);

        // then
        verify(sectionDao).deleteByLineIdAndUpStationId(1L, 1L);
        verify(sectionDao).deleteByLineIdAndDownStationId(1L, 4L);
    }

    @DisplayName("지하철 중간 구간 삭제")
    @Test
    void deleteSection() {
        // given
        given(sectionDao.findAllByLineId(1L))
                .willReturn(Arrays.asList(
                        new Section(1L, 1L, 1L, 2L, 3),
                        new Section(1L, 1L, 2L, 3L, 3),
                        new Section(1L, 1L, 3L, 4L, 3)
                ));
        given(sectionDao.save(any(Section.class)))
                .willReturn(new Section());

        // when
        sectionService.deleteSection(1L, 2L);

        // then
        verify(sectionDao, atLeast(2)).deleteBySection(any(Section.class));
        verify(sectionDao).save(any(Section.class));
    }
}