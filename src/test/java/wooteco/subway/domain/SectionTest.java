package wooteco.subway.domain;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.domain.fixtures.TestFixtures.강남;
import static wooteco.subway.domain.fixtures.TestFixtures.삼성;
import static wooteco.subway.domain.fixtures.TestFixtures.성수;

public class SectionTest {

    @Test
    @DisplayName("상행역을 기준으로 구간을 분리한다.")
    void splitByUpStation() {
        Line line = new Line(1L, "2호선", "green");

        Section 기존_구간 = new Section(1L, line, 삼성, 성수, 10);
        Section 추가할_구간 = new Section(line, 삼성, 강남, 4);

        List<Section> sections = 기존_구간.split(추가할_구간);
        assertThat(sections.get(0).getId()).isNotNull();
        assertThat(sections.get(0).getUpStation()).isEqualTo(삼성);
        assertThat(sections.get(0).getDownStation()).isEqualTo(강남);
        assertThat(sections.get(0).getDistance()).isEqualTo(4);

        assertThat(sections.get(1).getId()).isNull();
        assertThat(sections.get(1).getUpStation()).isEqualTo(강남);
        assertThat(sections.get(1).getDownStation()).isEqualTo(성수);
        assertThat(sections.get(1).getDistance()).isEqualTo(6);
    }

    @Test
    @DisplayName("하행역을 기준으로 구간을 분리한다.")
    void splitByDownStation() {
        Line line = new Line(1L, "2호선", "green");

        Section 기존_구간 = new Section(1L, line, 삼성, 성수, 10);
        Section 추가할_구간 = new Section(line, 강남, 성수, 4);

        List<Section> sections = 기존_구간.split(추가할_구간);
        assertThat(sections.get(0).getId()).isNotNull();
        assertThat(sections.get(0).getUpStation()).isEqualTo(삼성);
        assertThat(sections.get(0).getDownStation()).isEqualTo(강남);
        assertThat(sections.get(0).getDistance()).isEqualTo(6);

        assertThat(sections.get(1).getId()).isNull();
        assertThat(sections.get(1).getUpStation()).isEqualTo(강남);
        assertThat(sections.get(1).getDownStation()).isEqualTo (성수);
        assertThat(sections.get(1).getDistance()).isEqualTo(4);
    }

    @Test
    @DisplayName("구간에 상행역과 하행역중 하나라도 일치하는지 확인한다.")
    void isEqualToUpOrDownStation() {
        Line line = new Line(1L, "2호선", "green");

        Section 기존_구간 = new Section(1L, line, 삼성, 성수, 10);

        assertThat(기존_구간.isEqualToUpOrDownStation(삼성)).isTrue();
        assertThat(기존_구간.isEqualToUpOrDownStation(성수)).isTrue();
    }

}
