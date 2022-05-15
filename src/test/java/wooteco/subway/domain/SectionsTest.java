package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.domain.fixtures.TestFixtures.강남;
import static wooteco.subway.domain.fixtures.TestFixtures.건대;
import static wooteco.subway.domain.fixtures.TestFixtures.삼성;
import static wooteco.subway.domain.fixtures.TestFixtures.성수;
import static wooteco.subway.domain.fixtures.TestFixtures.왕십리;
import static wooteco.subway.domain.fixtures.TestFixtures.잠실;
import static wooteco.subway.domain.fixtures.TestFixtures.합정;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("구간들을 생성시 정렬된다.")
    void create() {
        Sections sections = new Sections(getSections(new Line("2호선", "green")));

        List<Section> values = sections.getSections();

        assertThat(values.get(0).getUpStation().getName()).isEqualTo("강남");
        assertThat(values.get(1).getUpStation().getName()).isEqualTo("잠실");
        assertThat(values.get(2).getUpStation().getName()).isEqualTo("성수");
    }

    @Test
    @DisplayName("상행역을 기준으로 구간을 추가하면 분리된 구간들을 반환한다.")
    void addSectionByUpStation() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSections(line));

        Section 추가할_구간 = new Section(line, 강남, 삼성, 5);
        List<Section> 추가된_구간 = 기존_구간.findUpdateSections(추가할_구간);

        List<Section> sections = new Sections(추가된_구간).getSections();
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo("강남");
        assertThat(sections.get(0).getDistance()).isEqualTo(5);
        assertThat(sections.get(1).getUpStation().getName()).isEqualTo("삼성");
        assertThat(sections.get(1).getDistance()).isEqualTo(7);
    }

    @Test
    @DisplayName("하행역 기준으로 구간을 추가하면 분리된 구간들을 반환한다.")
    void addSectionByDownStation() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSections(line));

        Section 추가할_구간 = new Section(line, 삼성, 잠실, 5);
        List<Section> 추가된_구간 = 기존_구간.findUpdateSections(추가할_구간);

        List<Section> sections = new Sections(추가된_구간).getSections();
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo("강남");
        assertThat(sections.get(0).getDistance()).isEqualTo(7);
        assertThat(sections.get(1).getUpStation().getName()).isEqualTo("삼성");
        assertThat(sections.get(1).getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("상행 종점을 추가한다.")
    void addUpStation() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSections(line));

        Section 추가할_구간 = new Section(line, 합정, 강남, 5);
        List<Section> 추가된_구간 = 기존_구간.findUpdateSections(추가할_구간);

        List<Section> sections = new Sections(추가된_구간).getSections();
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo("합정");
        assertThat(sections.get(0).getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("하행 종점을 추가한다.")
    void addDownStation() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSections(line));

        Section 추가할_구간 = new Section(line, 왕십리, 합정, 5);
        List<Section> 추가된_구간 = 기존_구간.findUpdateSections(추가할_구간);

        List<Section> sections = new Sections(추가된_구간).getSections();
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo("왕십리");
        assertThat(sections.get(0).getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("기존에 존재하는 구간인 경우 예외를 발생한다.")
    void existSection() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSections(line));

        Section 추가할_구간 = new Section(line, 강남, 잠실, 5);

        assertThatThrownBy(() -> 기존_구간.findUpdateSections(추가할_구간))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("기존에 존재하는 구간입니다.");
    }

    @Test
    @DisplayName("기존에 존재하지 않는 역들을 추가할 경우 예외를 발생한다.")
    void notExistStations() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSections(line));

        Section 추가할_구간 = new Section(line, 건대, 합정, 5);

        assertThatThrownBy(() -> 기존_구간.findUpdateSections(추가할_구간))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("생성할 수 없는 구간입니다.");
    }

    @Test
    @DisplayName("삭제된 구간을 반환한다.")
    void deleteSectionByStation() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSavedSections(line));

        List<Section> sections = 기존_구간.findDeleteSections(line, 잠실);

        assertThat(sections.get(0).getId()).isNotNull();
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo("강남");
        assertThat(sections.get(0).getDownStation().getName()).isEqualTo("잠실");

        assertThat(sections.get(1).getId()).isNotNull();
        assertThat(sections.get(1).getUpStation().getName()).isEqualTo("잠실");
        assertThat(sections.get(1).getDownStation().getName()).isEqualTo("성수");
    }

    @Test
    @DisplayName("상행 종점을 삭제시 상행 종점을 반환한다.")
    void deleteSectionByUpStation() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSavedSections(line));

        List<Section> sections = 기존_구간.findDeleteSections(line, 강남);

        assertThat(sections.get(0).getId()).isNotNull();
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo("강남");
        assertThat(sections.get(0).getDownStation().getName()).isEqualTo("잠실");
        assertThat(sections.get(0).getDistance()).isEqualTo(12);
    }

    @Test
    @DisplayName("하행 종점을 삭제시 상행 종점을 반환한다.")
    void deleteSectionByDownStation() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSavedSections(line));

        List<Section> sections = 기존_구간.findDeleteSections(line, 왕십리);

        assertThat(sections.get(0).getId()).isNotNull();
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo("성수");
        assertThat(sections.get(0).getDownStation().getName()).isEqualTo("왕십리");
        assertThat(sections.get(0).getDistance()).isEqualTo(12);
    }

    @Test
    @DisplayName("2개의 구간을 합친다.")
    void combineSection() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSavedSections(line));
        List<Section> 합칠_구간 = List.of(
            new Section(1L, line, 강남, 잠실, 12),
            new Section(2L, line, 잠실, 성수, 12)
        );

        Section sections = 기존_구간.combine(line, 합칠_구간);

        assertThat(sections.getId()).isNull();
        assertThat(sections.getUpStation().getName()).isEqualTo("강남");
        assertThat(sections.getDownStation().getName()).isEqualTo("성수");
        assertThat(sections.getDistance()).isEqualTo(24);
    }

    @Test
    @DisplayName("3개의 구간을 합칠 경우 예외를 발생한다.")
    void combineSectionInvalidSize() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSavedSections(line));
        List<Section> 합칠_구간 = List.of(
            new Section(1L, line, 강남, 잠실, 12),
            new Section(2L, line, 잠실, 성수, 12),
            new Section(3L, line, 성수, 왕십리, 12)
        );

        assertThatThrownBy(() -> 기존_구간.combine(line, 합칠_구간))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("2개의 구간을 합칠 수 있습니다.");
    }

    @Test
    @DisplayName("연결할 수 없는 구간을 합칠 경우 예외를 발생한다.")
    void combineSectionNotConnected() {
        Line line = new Line("2호선", "green");
        Sections 기존_구간 = new Sections(getSavedSections(line));
        List<Section> 합칠_구간 = List.of(
            new Section(1L, line, 강남, 잠실, 12),
            new Section(3L, line, 성수, 왕십리, 12)
        );

        assertThatThrownBy(() -> 기존_구간.combine(line, 합칠_구간))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("연결할 수 없는 구간입니다.");
    }

    private List<Section> getSections(Line line) {
        return List.of(
            new Section(line, 강남, 잠실, 12),
            new Section(line, 잠실, 성수, 12),
            new Section(line, 성수, 왕십리, 12)
        );
    }

    private List<Section> getSavedSections(Line line) {
        return List.of(
            new Section(1L, line, 강남, 잠실, 12),
            new Section(2L, line, 잠실, 성수, 12),
            new Section(3L, line, 성수, 왕십리, 12)
        );
    }
}
