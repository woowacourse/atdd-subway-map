package wooteco.subway.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.fake.FakeLineDao;
import wooteco.subway.service.fake.FakeSectionDao;

class SectionServiceTest {

    private static final long LINE_ID = 1L;
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        sectionService = new SectionService(new FakeSectionDao(), new FakeLineDao());
    }

    @DisplayName("구간을 등록할 수 있다.")
    @Test
    public void save() {
        //given
        final SectionRequest request = new SectionRequest(1L, 2L, 10);

        // when & then
        assertDoesNotThrow(() -> sectionService.save(LINE_ID, request));
    }

    @DisplayName("구간을 제거할 수 있다.")
    @Test
    public void delete() {
        //given
        final SectionRequest request1 = new SectionRequest(1L, 2L, 10);
        final SectionRequest request2 = new SectionRequest(2L, 3L, 10);

        sectionService.save(LINE_ID, request1);
        sectionService.save(LINE_ID, request2);

        //when & then
        assertDoesNotThrow(() -> sectionService.delete(LINE_ID, 2L));
    }
}