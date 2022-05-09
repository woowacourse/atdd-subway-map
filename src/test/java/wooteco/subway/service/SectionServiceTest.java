package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@Sql("/sectionTestSchema.sql")
class SectionServiceTest {

    public static final SectionRequest GIVEN_SECTION_REQ =
            new SectionRequest(1L, 2L, 6);

    private final SectionService sectionService;

    @Autowired
    public SectionServiceTest(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @Test
    @DisplayName("추가할 구간의 상행, 하행이 대상 노선에 둘 다 존재하는 경우 예외가 발생한다.")
    void saveSection() {
        // given
        sectionService.save(1L, GIVEN_SECTION_REQ);

        // when
        Throwable thrown = catchThrowable(() -> {
            sectionService.save(1L, GIVEN_SECTION_REQ);
        });

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행, 하행이 대상 노선에 둘 다 존재합니다.");
    }
}