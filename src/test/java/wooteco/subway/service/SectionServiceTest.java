package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionDeleteRequest;
import wooteco.subway.dto.SectionSaveRequest;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.SectionEntity;

@SpringBootTest
@Transactional
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private LineDao lineDao;

    private Long lineId;

    @BeforeEach
    void setUp() {
        LineEntity firstLine = lineDao.save(new LineEntity(null, "1호선", "red"));
        lineId = firstLine.getId();
        sectionDao.save(new SectionEntity(null, lineId, 1L, 2L, 3));
        sectionDao.save(new SectionEntity(null, lineId, 2L, 3L, 4));
        sectionDao.save(new SectionEntity(null, lineId, 3L, 4L, 5));
    }

    @Test
    @DisplayName("구간 등록하기")
    void saveSection() {
        // given
        SectionSaveRequest request = new SectionSaveRequest(lineId, 2L, 10L, 1);

        assertThatCode(() -> sectionService.save(request))
                .doesNotThrowAnyException();
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
