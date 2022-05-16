package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static wooteco.subway.test.TestFixture.낙성대역;
import static wooteco.subway.test.TestFixture.봉천역;
import static wooteco.subway.test.TestFixture.서울대입구역;
import static wooteco.subway.test.TestFixture.신림역;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private StationService stationService;

    @Mock
    private SectionDao sectionDao;

    @InjectMocks
    private SectionService sectionService;

    @DisplayName("지하철 구간을 저장한다")
    @Test
    void save() {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 1L, 2L, 5);
        when(stationService.getById(1L))
                .thenReturn(신림역);
        when(stationService.getById(2L))
                .thenReturn(봉천역);
        // when
        Section savedSection = sectionService.save(sectionRequest);
        // then
        assertAll(
                () -> verify(sectionDao).save(anyLong(), any(Section.class)),
                () -> assertThat(savedSection.getUpStation()).isEqualTo(신림역),
                () -> assertThat(savedSection.getDownStation()).isEqualTo(봉천역)
        );
    }

    @DisplayName("SectionRequest를 이용해 Section 객체를 만든다.")
    @Test
    void makeSectionByRequest() {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 1L, 2L, 5);
        when(stationService.getById(1L))
                .thenReturn(신림역);
        when(stationService.getById(2L))
                .thenReturn(봉천역);
        // when
        Section section = sectionService.makeSectionByRequest(sectionRequest);
        // then
        assertThat(section).isEqualTo(new Section(신림역, 봉천역, 5));
    }

    @DisplayName("변경 이전의 Sections와 변경 이후의 Sections를 비교하여 지울 것은 지우고 저장할 것은 저장한다.")
    @Test
    void deleteAndSaveSections() {
        // given
        Sections origin = new Sections(
                List.of(new Section(신림역, 봉천역, 5),
                        new Section(봉천역, 서울대입구역, 5),
                        new Section(서울대입구역, 낙성대역, 5)));
        Sections result = new Sections(
                List.of(new Section(신림역, 서울대입구역, 10),
                        new Section(서울대입구역, 낙성대역, 5)));
        // when
        sectionService.deleteAndSaveSections(1L, origin, result);
        // then
        verify(sectionDao, times(2)).remove(any(Section.class));
        verify(sectionDao, times(1)).save(anyLong(), any(Section.class));
    }
}
