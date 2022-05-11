package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@Sql("/sectionInitSchema.sql")
class SectionServiceTest {

    public static final SectionRequest GIVEN_SECTION_REQ =
        new SectionRequest(1L, 2L, 6);

    private final SectionService sectionService;

    @Autowired
    public SectionServiceTest(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @Test
    @DisplayName("초기 등록이면 추가할 구간의 상행, 하행이 대상 노선에 둘 다 존재하지 않아도 예외가 발생하지 않는다.")
    void firstSave() {
        // given

        // when, then
        assertThatCode(() ->
            sectionService.firstSave(1L, GIVEN_SECTION_REQ))
            .doesNotThrowAnyException();
    }
}