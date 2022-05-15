package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionDeleteRequest;
import wooteco.subway.dto.SectionSaveRequest;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;

@SpringBootTest
@Transactional
class SectionServiceTest {

    @Autowired
    private SectionRepository sections;

    @Autowired
    private LineRepository lines;

    @Autowired
    private SectionService sectionService;

    private Long lineId;

    @BeforeEach
    void setUp() {
        Line firstLine = lines.save(new Line("1호선", "red", null));
        lineId = firstLine.getId();
        sections.save(new Section(lineId, 1L, 2L, 3));
        sections.save(new Section(lineId, 2L, 3L, 4));
        sections.save(new Section(lineId, 3L, 4L, 5));
    }

    @Test
    @DisplayName("구간 등록하기")
    void saveSection() {
        // given
        SectionSaveRequest request = new SectionSaveRequest(lineId, 2L, 10L, 1);

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
        SectionDeleteRequest request = new SectionDeleteRequest(lineId, 1L);
        // then
        assertThatCode(() -> sectionService.delete(request))
                .doesNotThrowAnyException();
    }
}
