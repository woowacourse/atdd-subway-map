package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wooteco.subway.exception.CannotConnectSectionException;
import wooteco.subway.exception.SectionDuplicateException;

class SectionsTest {

    @Test
    @DisplayName("상행과 하행 모두 존재할 때 예외를 발생한다.")
    void duplicateSection() {
        final Section section = new Section(1L, 1L, new Station(1L, "신대방역"), new Station(2L, "선릉역"), 10);
        final Sections sections = initializeSections(section);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(SectionDuplicateException.class)
                .hasMessage("중복된 구간입니다.");
    }

    @Test
    @DisplayName("상행과 하행이 존재하지 않을 때 예외를 발생한다.")
    void impossibleConnectSection() {
        final Sections sections = new Sections(Collections.emptyList());
        final Section section = new Section(1L, 1L, new Station(1L, "신대방역"), new Station(2L, "선릉역"), 10);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(CannotConnectSectionException.class)
                .hasMessage("구간을 연결할 수 없습니다.");
    }

    @Test
    @DisplayName("역 사이에 등록할 때 기존 역 사이의 길이보다 크거나 같으면 예외를 발생한다.")
    void notEnoughDistance() {
        final Section section = new Section(1L, 1L, new Station(1L, "신대방역"), new Station(2L, "선릉역"), 10);
        final Sections sections = initializeSections(section);

        final Section addSection = new Section(2L, 1L, new Station(1L, "신대방역"), new Station(3L, "잠실역"), 15);

        assertThatThrownBy(() -> sections.add(addSection))
                .isInstanceOf(CannotConnectSectionException.class)
                .hasMessage("구간을 연결할 수 없습니다.");
    }

    @ParameterizedTest
    @MethodSource("provideAddSections")
    @DisplayName("상행 종점, 하행 종점에 등록할 수 있다.")
    void addUpAndDownStation(final Section addSection) {
        final Section section = new Section(1L, 1L, new Station(1L, "신대방역"), new Station(2L, "선릉역"), 10);
        final Sections sections = initializeSections(section);

        sections.add(addSection);

        assertThat(sections.getSections()).hasSize(2);
    }

    static Stream<Arguments> provideAddSections() {
        return Stream.of(
                Arguments.of(new Section(2L, 1L, new Station(3L, "홍대역"), new Station(1L, "신대방역"), 15)),
                Arguments.of(new Section(3L, 1L, new Station(2L, "선릉역"), new Station(1L, "잠실역"), 5))
        );
    }

    @Test
    @DisplayName("상행이 같고, 구간의 거리가 기존의 거리보다 짧으면 새로운 구간을 등록할 수 있다.")
    void addBetweenSectionByUpStation() {
        final Section existingSection = new Section(1L, 1L, new Station(1L, "신대방역"), new Station(2L, "선릉역"), 10);
        final Sections sections = initializeSections(existingSection);
        final Section newSection = new Section(1L, new Station(1L, "신대방역"), new Station(3L, "신림역"), 1);
        final int expectedDistance = existingSection.getDistance() - newSection.getDistance();

        sections.add(newSection);

        assertAll(() -> {
            assertThat(existingSection.getUpStation().getName()).isEqualTo(newSection.getDownStation().getName());
            assertThat(existingSection.getDownStation().getName()).isEqualTo("선릉역");
            assertThat(existingSection.getDistance()).isEqualTo(expectedDistance);
        });
    }

    @Test
    @DisplayName("하행이 같고, 구간의 거리가 기존의 거리보다 짧으면 새로운 구간을 등록할 수 있다.")
    void addBetweenSectionByDownStation() {
        final Section existingSection = new Section(1L, 1L, new Station(1L, "신대방역"), new Station(2L, "선릉역"), 10);
        final Sections sections = initializeSections(existingSection);
        final Section newSection = new Section(1L, new Station(3L, "역삼역"), new Station(2L, "선릉역"), 1);
        final int expectedDistance = existingSection.getDistance() - newSection.getDistance();

        sections.add(newSection);

        assertAll(() -> {
            assertThat(existingSection.getUpStation().getName()).isEqualTo(newSection.getUpStation().getName());
            assertThat(existingSection.getDownStation().getName()).isEqualTo("선릉역");
            assertThat(existingSection.getDistance()).isEqualTo(expectedDistance);
        });
    }

    private Sections initializeSections(final Section section) {
        final List<Section> sections = new ArrayList<>();
        sections.add(section);
        return new Sections(sections);
    }
}
