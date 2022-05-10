package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @Test
    @DisplayName("섹션들에서 역 id 찾기")
    void findStationIds() {
        // given
        Section section1 = new Section(1, 2, 10);
        Section section2 = new Section(2, 3, 10);
        Sections sections = Sections.of(section1, section2);

        // when
        List<Long> ids = sections.findIds();

        // then
        assertThat(ids).containsOnly(1L, 2L, 3L);
    }
}
