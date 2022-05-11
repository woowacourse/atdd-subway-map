package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Section section2 = new Section(1L, 2L, 3L, 10);
        sections = Sections.of(section1, section2);
    }

    @Test
    @DisplayName("섹션들에서 역 id 찾기")
    void findStationIds() {
        // when
        List<Long> ids = sections.findStationIds();

        // then
        assertThat(ids).containsOnly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("구간들에서 특정 역과 연결된 구간 찾기")
    void findNearByStationId() {
        // when
        List<Section> nearBySections = sections.findNearByStationId(2L);

        // then
        assertThat(nearBySections).hasSize(2);
    }
}
