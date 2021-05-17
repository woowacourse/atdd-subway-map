package wooteco.subway.section.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.line.Line;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.section.dao.JdbcSectionDao;
import wooteco.subway.section.dao.SectionDao;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static wooteco.subway.line.LineFixture.이호선;
import static wooteco.subway.section.SectionFixture.*;

@DisplayName("지하철 구간 비즈니스 로직 테스트")
@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    private final SectionDao sectionDao = Mockito.mock(JdbcSectionDao.class);

    @InjectMocks
    private SectionService sectionService;

    @DisplayName("Line을 통해 Sections 찾기")
    @Test
    void findByLine() {
        // given
        given(sectionDao.findAllByLineId(anyLong()))
                .willReturn(Arrays.asList(
                        이호선_왕십리_잠실_거리10,
                        이호선_잠실_강남_거리5,
                        이호선_강남_구의_거리7
                ));

        // when
        Sections sections = sectionService.findByLine(이호선);

        // then
        assertThat(sections.getSections()).hasSize(3);
        verify(sectionDao).findAllByLineId(anyLong());
    }

    @DisplayName("Section을 추가한다.")
    @Test
    void add() {
        // given
        given(sectionDao.save(이호선_잠실_강남_거리5))
                .willReturn(이호선_잠실_강남_거리5);

        // when
        Section section = sectionService.add(이호선_잠실_강남_거리5);

        // then
        assertThat(section).isEqualTo(이호선_잠실_강남_거리5);
    }

    @DisplayName("노선의 Sections 상태를 DB와 동기화해준다.")
    @Test
    void synchronizeDB() {
        // given
        Line line = new Line(2L, "2호선", "green");

        // when
        sectionService.updateSectionsInLine(line);

        // then
        verify(sectionDao).deleteByLineId(anyLong());
        verify(sectionDao).batchInsert(anyList());
    }
}