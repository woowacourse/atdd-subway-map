package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {


    @DisplayName("초기 구간을 입력 받아 구간들을 생성한다.")
    @Test
    void create() {
        List<Section> sections = List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5));
        assertThatCode(() -> new Sections(sections, 1L))
                .doesNotThrowAnyException();
    }

    @DisplayName("추가할 수 있는 구간인지 확인한다.")
    @Test
    void validate_success() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);
        Section newSection = new Section(1L, 3L, 4L, 2);

        assertThatCode(() -> sections.validateAddable(newSection))
                .doesNotThrowAnyException();
    }

    @DisplayName("입력받은 구간에 대해 같은 구간이 존재할 때에 예외가 발생한다.")
    @Test
    void validate_duplicateSectionException() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);
        Section newSection = new Section(1L, 1L, 2L, 10);

        assertThatThrownBy(() -> sections.validateAddable(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("입력받은 구간에 대해 같은 구간이 존재할 때에 예외가 발생한다.")
    @Test
    void validate_duplicateSectionException2() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 3L, 2L, 5),
                new Section(1L, 4L, 3L, 10))), 1L);
        Section newSection = new Section(1L, 4L, 2L, 10);

        assertThatThrownBy(() -> sections.validateAddable(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("입력받은 구간에 대해 상행역과 하행역 모두 구간들에 포함되어 있지 않으면 예외가 발생한다.")
    @Test
    void validate_notExistingStationException() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);
        Section newSection = new Section(1L, 5L, 6L, 10);

        assertThatThrownBy(() -> sections.validateAddable(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("입력받은 구간에 대해 같은 상행역이 존재하는 경우 구간 변경이 필요하다.")
    @Test
    void needToChange_true_sameUpStationId() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        Section newSection = new Section(1L, 2L, 4L, 2);

        assertThat(sections.needToChange(newSection)).isTrue();
    }

    @DisplayName("입력받은 구간에 대해 같은 하행역이 존재하는 경우 구간 변경이 필요하다.")
    @Test
    void needToChange_true_sameDownStationId() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        Section newSection = new Section(1L, 5L, 3L, 2);

        assertThat(sections.needToChange(newSection)).isTrue();
    }

    @DisplayName("입력받은 구간에 대해 같은 상행역과 하행역이 존재하지 않는 경우 구간 변경이 필요없다.")
    @Test
    void needToChange_false() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        Section newSection = new Section(1L, 3L, 4L, 2);

        assertThat(sections.needToChange(newSection)).isFalse();
    }

    @DisplayName("입력받은 구간에 대해 같은 상행역이 존재하면 기존의 구간들에서 변경될 구간을 반환한다.")
    @Test
    void findUpdatingSection_upStation() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        Section newSection = new Section(1L, 2L, 4L, 2);

        assertThat(sections.findUpdatingSection(newSection).getUpStationId()).isEqualTo(4L);
        assertThat(sections.findUpdatingSection(newSection).getDownStationId()).isEqualTo(3L);
        assertThat(sections.findUpdatingSection(newSection).getDistance()).isEqualTo(3);
    }

    @DisplayName("입력받은 구간에 대해 같은 하행역이 존재하면 기존의 구간들에서 변경될 구간을 반환한다.")
    @Test
    void findUpdatingSection_downStation() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        Section newSection = new Section(1L, 4L, 3L, 2);

        assertThat(sections.findUpdatingSection(newSection).getUpStationId()).isEqualTo(2L);
        assertThat(sections.findUpdatingSection(newSection).getDownStationId()).isEqualTo(4L);
        assertThat(sections.findUpdatingSection(newSection).getDistance()).isEqualTo(3);
    }
}