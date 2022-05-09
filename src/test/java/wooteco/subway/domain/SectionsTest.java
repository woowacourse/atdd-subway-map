package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {
    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        Sections sections = new Sections(List.of());
        Section section = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        sections.add(section);
        assertThat(sections.size()).isEqualTo(1);
    }
}
