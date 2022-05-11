package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.section.DuplicatedSectionException;
import wooteco.subway.exception.section.NonexistentSectionStationException;
import wooteco.subway.exception.section.NonexistentStationSectionException;
import wooteco.subway.exception.section.OnlyOneSectionException;
import wooteco.subway.exception.section.SectionLengthExcessException;

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
                .isInstanceOf(DuplicatedSectionException.class);
    }

    @DisplayName("입력받은 구간에 대해 같은 구간이 존재할 때에 예외가 발생한다.")
    @Test
    void validate_duplicateSectionException2() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 3L, 2L, 5),
                new Section(1L, 4L, 3L, 10))), 1L);
        Section newSection = new Section(1L, 4L, 2L, 10);

        assertThatThrownBy(() -> sections.validateAddable(newSection))
                .isInstanceOf(DuplicatedSectionException.class);
    }

    @DisplayName("입력받은 구간에 대해 상행역과 하행역 모두 구간들에 포함되어 있지 않으면 예외가 발생한다.")
    @Test
    void validate_notExistingStationException() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);
        Section newSection = new Section(1L, 5L, 6L, 10);

        assertThatThrownBy(() -> sections.validateAddable(newSection))
                .isInstanceOf(NonexistentSectionStationException.class);
    }

    @DisplayName("입력받은 구간에 대해 같은 상행역이 존재하는 경우 구간 변경이 필요하다.")
    @Test
    void needToChange_true_sameUpStationId() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        Section newSection = new Section(1L, 2L, 4L, 2);

        assertThat(sections.needToChangeExistingSection(newSection)).isTrue();
    }

    @DisplayName("입력받은 구간에 대해 같은 하행역이 존재하는 경우 구간 변경이 필요하다.")
    @Test
    void needToChange_true_sameDownStationId() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        Section newSection = new Section(1L, 5L, 3L, 2);

        assertThat(sections.needToChangeExistingSection(newSection)).isTrue();
    }

    @DisplayName("입력받은 구간에 대해 같은 상행역과 하행역이 존재하지 않는 경우 구간 변경이 필요없다.")
    @Test
    void needToChange_false() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        Section newSection = new Section(1L, 3L, 4L, 2);

        assertThat(sections.needToChangeExistingSection(newSection)).isFalse();
    }

    @DisplayName("입력받은 구간에 대해 같은 상행역이 존재하면 기존의 구간들에서 변경될 구간을 반환한다.")
    @Test
    void findUpdatingSection_upStation() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        Section newSection = new Section(1L, 2L, 4L, 2);

        assertThat(sections.findNeedUpdatingSection(newSection).getUpStationId()).isEqualTo(4L);
        assertThat(sections.findNeedUpdatingSection(newSection).getDownStationId()).isEqualTo(3L);
        assertThat(sections.findNeedUpdatingSection(newSection).getDistance()).isEqualTo(3);
    }

    @DisplayName("입력받은 구간에 대해 같은 하행역이 존재하면 기존의 구간들에서 변경될 구간을 반환한다.")
    @Test
    void findUpdatingSection_downStation() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        Section newSection = new Section(1L, 4L, 3L, 2);

        assertThat(sections.findNeedUpdatingSection(newSection).getUpStationId()).isEqualTo(2L);
        assertThat(sections.findNeedUpdatingSection(newSection).getDownStationId()).isEqualTo(4L);
        assertThat(sections.findNeedUpdatingSection(newSection).getDistance()).isEqualTo(3);
    }

    @DisplayName("새로운 구간의 길이가 기존 역 사이 길이보다 크거나 같으면 예외가 발생한다.")
    @Test
    void findUpdatingSection_exception() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        assertThatThrownBy(() -> sections.findNeedUpdatingSection(new Section(1L, 4L, 3L, 5)))
                .isInstanceOf(SectionLengthExcessException.class);
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거하려고 하면 예외가 발생한다.")
    @Test
    void validateRemovable_onlySection() {
        Sections sections = new Sections(List.of(new Section(1L, 1L, 2L, 10)), 1L);

        assertThatThrownBy(() -> sections.validateRemovable(1L))
                .isInstanceOf(OnlyOneSectionException.class);
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거하려고 하면 예외가 발생한다.")
    @Test
    void validateRemovable_notExist() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        assertThatThrownBy(() -> sections.validateRemovable(10L))
                .isInstanceOf(NonexistentStationSectionException.class);
    }

    @DisplayName("입력받은 지하철 역이 상행종착역일 때 참을 반환한다.")
    @Test
    void isEndStation_endUpStation_true() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        assertThat(sections.isEndStation(1L)).isTrue();
    }

    @DisplayName("입력받은 지하철 역이 상행종착역일 때 참을 반환한다.")
    @Test
    void isEndStation_endDownStation_true() {
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5))), 1L);

        assertThat(sections.isEndStation(3L)).isTrue();
    }

    @DisplayName("입력받은 지하철 역이 상행종착역일 때 참을 반환한다.")
    @Test
    void isEndStation_false() {
        Sections sections = new Sections(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 5)), 1L);

        assertThat(sections.isEndStation(2L)).isFalse();
    }

    @DisplayName("입력받은 지하철 역에 대해 제거할 상행 구간 아이디를 반환한다.")
    @Test
    void findEndSectionIdToRemove_upStation() {
        Sections sections = new Sections(List.of(new Section(1L, 1L, 1L, 2L, 10),
                new Section(2L, 1L, 2L, 3L, 5)), 1L);

        assertThat(sections.findEndSectionIdToRemove(1L)).isEqualTo(1L);
    }

    @DisplayName("입력받은 지하철 역에 대해 제거할 하행 구간 아이디를 반환한다.")
    @Test
    void findEndSectionIdToRemove_downStation() {
        Sections sections = new Sections(List.of(new Section(1L, 1L, 1L, 2L, 10),
                new Section(2L, 1L, 2L, 3L, 5)), 1L);

        assertThat(sections.findEndSectionIdToRemove(3L)).isEqualTo(2L);
    }

    @DisplayName("중간역을 제거하려는 경우, 양 쪽의 역으로 새로운 구간을 생성한다. ")
    @Test
    void makeNewSection() {
        Sections sections = new Sections(List.of(new Section(1L, 1L, 1L, 2L, 10),
                new Section(2L, 1L, 2L, 3L, 5),
                new Section(3L, 1L, 3L, 4L, 7)), 1L);

        Section section = sections.makeNewSection(2L);

        assertThat(section.getUpStationId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(3L);
        assertThat(section.getDistance()).isEqualTo(15);
    }

    @DisplayName("중간역을 제거하려는 경우, 제거할 양쪽 구간의 id를 반환한다.")
    @Test
    void findSectionIdsToRemove() {
        Sections sections = new Sections(List.of(new Section(1L, 1L, 1L, 2L, 10),
                new Section(2L, 1L, 2L, 3L, 5),
                new Section(3L, 1L, 3L, 4L, 7)), 1L);

        List<Long> sectionIdsToRemove = sections.findSectionIdsToRemove(2L);

        assertThat(sectionIdsToRemove).containsExactly(1L, 2L);
    }

    @DisplayName("Line의 종착 상행구간을 입력받으면, 종착 상행역부터 종착 하행역까지를 순서대로 반환한다.")
    @Test
    void findArrangedStationIds1() {
        Sections sections = new Sections(List.of(new Section(1L, 1L, 1L, 2L, 10),
                new Section(2L, 1L, 2L, 3L, 5),
                new Section(3L, 1L, 3L, 4L, 7)), 1L);

        int endUpSectionIndex = 0;
        Section endUpSection = sections.findEndSections().get(endUpSectionIndex);
        List<Long> arrangedStationIdsByLineId = sections.findArrangedStationIds(endUpSection);

        assertThat(arrangedStationIdsByLineId).containsExactly(1L, 2L, 3L, 4L);
    }

    @DisplayName("Line의 종착 상행구간을 입력받으면, 종착 상행역부터 종착 하행역까지를 순서대로 반환한다. - shuffle된 경우")
    @Test
    void findArrangedStationIds2() {
        List<Section> rawSections = new ArrayList<>(List.of(new Section(1L, 1L, 2L, 1L, 10),
                new Section(2L, 1L, 3L, 4L, 5),
                new Section(3L, 1L, 1L, 3L, 7)));
        Collections.shuffle(rawSections);

        Sections sections = new Sections(rawSections, 1L);

        int endUpSectionIndex = 0;
        Section endUpSection = sections.findEndSections().get(endUpSectionIndex);
        List<Long> arrangedStationIdsByLineId = sections.findArrangedStationIds(endUpSection);

        assertThat(arrangedStationIdsByLineId).containsExactly(2L, 1L, 3L, 4L);
    }
}