package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("기존 구간과 상행역이 일치하는 새로운 구간을 추가하면 거리가 알맞게 조정이 된다.")
    void addDownStation() {
        Section section1 = new Section(1L,1L, 1L, 3L, 10);
        Section section2 = new Section(2L,1L, 1L, 2L, 3);
        Sections sections = new Sections(List.of(section1));
        Section addedSection = sections.add(section2);

        assertAll(
                () -> assertThat(addedSection.getId()).isEqualTo(1L),
                () -> assertThat(addedSection.getUpStationId()).isEqualTo(2L),
                () -> assertThat(addedSection.getDownStationId()).isEqualTo(3L),
                () -> assertThat(addedSection.getDistance()).isEqualTo(7)
        );
    }

    @Test
    @DisplayName("기존 구간과 상행역이 일치하고 길이가 더 긴 새로운 구간을 추가하면 예외를 발생한다.")
    void addLongDownStation() {
        Section section1 = new Section(1L, 1L, 3L, 10);
        Section section2 = new Section(1L, 1L, 2L, 11);
        Sections sections = new Sections(List.of(section1));
        assertThatThrownBy(
                () -> sections.add(section2)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간보다 긴 역을 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("기존 구간과 하행역이 일치하는 새로운 구간을 추가하면 거리가 알맞게 조정이 된다.")
    void addUpStation() {
        Section section1 = new Section(1L, 1L, 3L, 10);
        Section section2 = new Section(1L, 2L, 3L, 6);
        Sections sections = new Sections(List.of(section1));
        Section addedSection = sections.add(section2);

        assertAll(
                () -> assertThat(addedSection.getUpStationId()).isEqualTo(1L),
                () -> assertThat(addedSection.getDownStationId()).isEqualTo(2L),
                () -> assertThat(addedSection.getDistance()).isEqualTo(4)
        );
    }

    @Test
    @DisplayName("기존 구간과 하행역이 일치하고 길이가 더 긴 새로운 구간을 추가하면 예외를 발생한다.")
    void addLongUpStation() {
        Section section1 = new Section(1L, 1L, 3L, 10);
        Section section2 = new Section(1L, 2L, 3L, 11);
        Sections sections = new Sections(List.of(section1));
        assertThatThrownBy(
                () -> sections.add(section2)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간보다 긴 역을 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("상행종점역을 삭제했을 때 종점구간이 삭제된다.")
    void deleteTopStation() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Section section2 = new Section(1L, 2L, 3L, 5);
        Sections sections = new Sections(List.of(section1, section2));
        List<Section> deletedSections = sections.delete(1L);
        Section deletedSection = deletedSections.get(0);
        assertAll(
                () -> assertThat(deletedSections.size()).isEqualTo(1),
                () -> assertThat(deletedSection.getUpStationId()).isEqualTo(1L),
                () -> assertThat(deletedSection.getDownStationId()).isEqualTo(2L),
                () -> assertThat(deletedSection.getDistance()).isEqualTo(10)
        );
    }

    @Test
    @DisplayName("하행종점역을 삭제했을 때 종점구간이 삭제된다.")
    void deleteBottomStation() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Section section2 = new Section(1L, 2L, 3L, 5);
        Sections sections = new Sections(List.of(section1, section2));
        List<Section> deletedSections = sections.delete(3L);
        Section deletedSection = deletedSections.get(0);
        assertAll(
                () -> assertThat(deletedSections.size()).isEqualTo(1),
                () -> assertThat(deletedSection.getUpStationId()).isEqualTo(2L),
                () -> assertThat(deletedSection.getDownStationId()).isEqualTo(3L),
                () -> assertThat(deletedSection.getDistance()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("구간이 하나밖에 없다면 삭제할 수 없다.")
    void deleteOnlySection() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Sections sections = new Sections(List.of(section1));
        assertThatThrownBy(
                () -> sections.delete(1L)
        ).isInstanceOf(IllegalStateException.class)
                .hasMessage("상행 종점과 하행 종점밖에 존재하지 않아 구간을 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("노선 구간에 존재하지 않는 역은 삭제할 수 없다.")
    void deleteNoExistStation() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Section section2 = new Section(1L, 2L, 3L, 5);
        Sections sections = new Sections(List.of(section1, section2));
        assertThatThrownBy(
                () -> sections.delete(4L)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선 구간에 존재하지 않는 역입니다.");
    }

    @Test
    @DisplayName("중간역을 삭제했을 때 중간역이 포함된 구간을 모두 반환한다.")
    void deleteMiddleStation() {
        Section section1 = new Section(1L, 1L, 2L, 10);
        Section section2 = new Section(1L, 2L, 3L, 5);
        Sections sections = new Sections(List.of(section1, section2));
        List<Section> deletedSections = sections.delete(2L);
        System.out.println(deletedSections);
        Section deletedUpSection = deletedSections.get(0);
        Section deletedDownSection = deletedSections.get(1);
        assertAll(
                () -> assertThat(deletedSections.size()).isEqualTo(2),
                () -> assertThat(deletedUpSection.getUpStationId()).isEqualTo(1L),
                () -> assertThat(deletedUpSection.getDownStationId()).isEqualTo(2L),
                () -> assertThat(deletedUpSection.getDistance()).isEqualTo(10),
                () -> assertThat(deletedDownSection.getUpStationId()).isEqualTo(2L),
                () -> assertThat(deletedDownSection.getDownStationId()).isEqualTo(3L),
                () -> assertThat(deletedDownSection.getDistance()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("순서대로 역을 반환한다.")
    void getAllSortedIds() {
        Section section1 = new Section(1L, 2L, 5L, 10);
        Section section2 = new Section(1L, 1L, 2L, 5);
        Section section3 = new Section(1L, 3L, 1L, 5);
        Sections sections = new Sections(List.of(section1, section2, section3));
        List<Long> allSortedIds = sections.getAllSortedIds();
        assertAll(
                () -> assertThat(allSortedIds.get(0)).isEqualTo(3L),
                () -> assertThat(allSortedIds.get(1)).isEqualTo(1L),
                () -> assertThat(allSortedIds.get(2)).isEqualTo(2L),
                () -> assertThat(allSortedIds.get(3)).isEqualTo(5L)
        );
    }
}
