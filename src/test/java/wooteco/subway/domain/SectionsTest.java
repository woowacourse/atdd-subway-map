package wooteco.subway.domain;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    @DisplayName("구간들을 생성시 정려된다.")
    void create() {
        Line line = new Line("2호선", "green");
        List<Section> sections = List.of(
            new Section(line, new Station(5L, "잠실"), new Station(4L, "노원"), 12),
            new Section(line, new Station(1L, "강남"), new Station(2L, "삼성"), 12),
            new Section(line, new Station(2L, "삼성"), new Station(3L, "성수"), 12),
            new Section(line, new Station(4L, "노원"), new Station(1L, "강남"), 12)
        );

        Sections created = new Sections(sections);

        List<Section> values = created.getValues();


        assertThat(values.get(0).getUpStation().getName()).isEqualTo("노원");
        assertThat(values.get(0).getDownStation().getName()).isEqualTo("강남");
        assertThat(values.get(1).getUpStation().getName()).isEqualTo("강남");
        assertThat(values.get(1).getDownStation().getName()).isEqualTo("삼성");
        assertThat(values.get(2).getUpStation().getName()).isEqualTo("삼성");
        assertThat(values.get(2).getDownStation().getName()).isEqualTo("성수");
    }
}
