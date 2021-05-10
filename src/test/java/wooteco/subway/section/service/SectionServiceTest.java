package wooteco.subway.section.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.section.Section;
import wooteco.subway.section.dao.JdbcSectionDao;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.dto.response.SectionCreateResponse;
import wooteco.subway.section.dto.response.SectionResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
        SectionCreateResponse newSection = sectionService.save(분당선, section);

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
}