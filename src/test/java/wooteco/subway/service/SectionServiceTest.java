package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionDeleteRequest;
import wooteco.subway.dto.SectionSaveRequest;

@SpringBootTest
@Transactional
class SectionServiceTest {

    @Mock
    private JdbcSectionDao sectionDao;

    @InjectMocks
    private SectionService sectionService;

    private List<Section> sections;

    @BeforeEach
    void setUp() {
        sections = List.of(
                new Section(1L, 1L, 2L, 3),
                new Section(1L, 2L, 3L, 4),
                new Section(1L, 3L, 4L, 5));
    }

    @Test
    @DisplayName("구간 등록하기")
    void saveSection() {
        // given
        SectionSaveRequest request = new SectionSaveRequest(1L, 2L, 10L, 1);
        Section section = new Section(request.getLineId(),
                request.getUpStationId(), request.getDownStationId(), request.getDistance());

        given(sectionDao.findByLineId(request.getLineId()))
                .willReturn(sections);
        given(sectionDao.update(section))
                .willReturn(1);
        given(sectionDao.save(section))
                .willReturn(section);

        Section savedSection = sectionService.save(request);
        assertAll(() -> {
            assertThat(savedSection.getUpStationId()).isEqualTo(request.getUpStationId());
            assertThat(savedSection.getDownStationId()).isEqualTo(request.getDownStationId());
        });
    }

    @Test
    @DisplayName("구간 삭제하기")
    void deleteSection() {
        // given
        SectionDeleteRequest request = new SectionDeleteRequest(1L, 1L);

        given(sectionDao.findByLineId(request.getLineId()))
                .willReturn(sections);
        given(sectionDao.deleteById(anyLong()))
                .willReturn(1);
        given(sectionDao.update(any(Section.class)))
                .willReturn(1);

        // then
        assertThatCode(() -> sectionService.delete(request));
    }
}
