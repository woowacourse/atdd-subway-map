package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("구간들을 생성시 정렬된다.")
    void create() {
        Sections sections = new Sections(getSections());

        List<Section> values = sections.getValues();

        assertThat(values.get(0).getUpStation().getName()).isEqualTo("노원");
        assertThat(values.get(1).getUpStation().getName()).isEqualTo("강남");
        assertThat(values.get(2).getUpStation().getName()).isEqualTo("삼성");
    }

    private List<Section> getSections() {
        Line line = new Line("2호선", "green");
        return List.of(
                new Section(line, new Station(1L, "강남"), new Station(2L, "삼성"), 12),
                new Section(line, new Station(2L, "삼성"), new Station(3L, "성수"), 12),
                new Section(line, new Station(4L, "노원"), new Station(1L, "강남"), 12)
        );
    }
}
