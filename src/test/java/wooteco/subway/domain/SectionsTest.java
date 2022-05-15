package wooteco.subway.domain;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionsTest {

    @TestFactory
    @DisplayName("일반적인 경우에 대한 테스트")
    Stream<DynamicTest> dynamicTestStreamNormalCase() {
        return Stream.of(
                DynamicTest.dynamicTest("상행 종점인 구간을 추가한다.", () -> {
                    Sections sections = new Sections(new Section(0, 1L, 1L, 2L, 7));
                    sections.add(new Section(0, 1L, 3L, 1L, 5));

                    assertThat(sections.getStations().size()).isEqualTo(3);
                }),
                DynamicTest.dynamicTest("하행 종점인 구간을 추가한다.", () -> {
                    Sections sections = new Sections(new Section(0, 1L, 1L, 2L, 7));
                    sections.add(new Section(0, 1L, 2L, 3L, 5));

                    assertThat(sections.getStations().size()).isEqualTo(3);
                }),
                DynamicTest.dynamicTest("갈래길을 방지한다.", () -> {
                    Sections sections = new Sections(new Section(0, 1L, 1L, 2L, 7));
                    sections.add(new Section(0, 1L, 1L, 3L, 5));
                    Section inserted = getSectionByUpStationId(sections,1L);
                    Section separated = getSectionByUpStationId(sections,3L);

                    assertAll(
                            () -> assertThat(sections.getStations().size()).isEqualTo(3),
                            () -> assertThat(inserted.getDistance()).isEqualTo(5),
                            () -> assertThat(separated.getDistance()).isEqualTo(2)
                    );
                }),
                DynamicTest.dynamicTest("역을 삭제하는 경우, 상행과 하행에 모두 걸려 있다면 합쳐준다.", () -> {
                    Sections sections = new Sections(new Section(0, 1L, 1L, 2L, 7));
                    sections.add(new Section(0, 1L, 2L, 3L, 5));
                    sections.remove(2L);

                    assertAll(
                            () -> assertThat(sections.getSectionContainsStation(1L).get(0).getDownStationId()).isEqualTo(3),
                            () -> assertThat(sections.getSectionContainsStation(1L).get(0).getDistance()).isEqualTo(12)
                    );
                })
        );
    }

    @TestFactory
    @DisplayName("예외 사항에 대한 테스트")
    Stream<DynamicTest> dynamicTestStreamWithException() {

        return Stream.of(
                DynamicTest.dynamicTest("상행이 같은 길이면서 기존 길보다 길이보다 긴 길이 들어오는 경우 예외 처리를 한다.", () -> {
                    Sections sections = new Sections(new Section(0, 1L, 1L, 2L, 7));
                    assertThatThrownBy(
                            () -> sections.add(new Section(0, 1L, 1L, 3L, 10))
                    ).isInstanceOf(IllegalStateException.class);
                }),
                DynamicTest.dynamicTest("하행이 같은 길이면서 기존 길보다 길이보다 긴 길이 들어오는 경우 예외 처리를 한다.", () -> {
                    Sections sections = new Sections(new Section(0, 1L, 1L, 2L, 7));
                    assertThatThrownBy(
                            () -> sections.add(new Section(0, 1L, 3L, 2L, 10))
                    ).isInstanceOf(IllegalStateException.class);
                }),
                DynamicTest.dynamicTest("구간을 추가할 때 상행과 하행 모두 기존 노선에 존재하지 않는 경우 예외를 발생시킨다", () -> {
                    Sections sections = new Sections(new Section(0, 1L, 1L, 2L, 7));
                    assertThatThrownBy(
                            () -> sections.add(new Section(0, 1L, 10L, 11L, 10))
                    ).isInstanceOf(IllegalStateException.class);
                }),
                DynamicTest.dynamicTest("구간이 1개만 있는 경우 삭제할 수 없다.", () -> {
                    Sections sections = new Sections(new Section(0, 1L, 1L, 2L, 7));
                    assertThatThrownBy(
                            () -> sections.remove(1L)
                    ).isInstanceOf(IllegalStateException.class);
                })
        );
    }

    private Section getSectionByUpStationId(Sections sections, Long sectionId) {
        for (Section section : sections.getSections()) {
            if (section.isSameUpStationId(sectionId)) {
                return section;
            }
        }
        return null;
    }
}

