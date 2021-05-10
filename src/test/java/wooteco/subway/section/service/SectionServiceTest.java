package wooteco.subway.section.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.line.dto.response.LineResponse;
import wooteco.subway.section.Section;
import wooteco.subway.section.dao.JdbcSectionDao;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.dto.response.SectionCreateResponse;

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
        LineResponse 분당선 = new LineResponse(1L, "분당선", "red");
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
}