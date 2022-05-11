package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionSaveRequest;

@SpringBootTest
@Transactional
class SectionServiceTest {

    @InjectMocks
    private SectionService sectionService;

    @Mock
    private JdbcSectionDao sectionDao;

    @Test
    @DisplayName("구간 등록하기")
    void saveSection() {
        // given
        SectionSaveRequest request = new SectionSaveRequest(1L, 2L, 4L, 1);
        Section section = new Section(request.getLineId(), request.getUpStationId(), request.getDownStationId(), request.getDistance());
        List<Section> sections = List.of(new Section(1L, 1L, 2L, 3),
                new Section(2L, 2L, 3L, 4));
        BDDMockito.given(sectionDao.findByLineId(1L)).willReturn(sections);
        BDDMockito.given(sectionDao.update(section)).willReturn(1);
        BDDMockito.given(sectionDao.save(section)).willReturn(section);
        Section savedSection = sectionService.save(request);
        assertAll(() -> {
            assertThat(savedSection.getUpStationId()).isEqualTo(2L);
            assertThat(savedSection.getDownStationId()).isEqualTo(4L);
        });
    }
}
