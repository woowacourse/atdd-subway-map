package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class SectionsTest {

    @DisplayName("구간 추가 기능")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromAppendSection() {
        Section basedSection = new Section(1L, 1L, 3L, 7);
        Sections sections = new Sections(List.of(basedSection));

        return Stream.of(
                dynamicTest("상행 종점을 등록한다.", () -> {
                    Section appendSection = new Section(1L, 2L, 1L, 4);

                    assertDoesNotThrow(() -> sections.append(appendSection));
                }),

                dynamicTest("상행 종점 등록 중 추가하기 위한 구간의 상행이 기존 구간에 존재하면 예외를 던진다.", () -> {
                    Section appendSection = new Section(1L, 3L, 2L, 4);

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("하행 종점을 등록한다.", () -> {
                    Section appendSection = new Section(1L, 3L, 4L, 3);

                    assertDoesNotThrow(() -> sections.append(appendSection));
                }),

                dynamicTest("하행 종점 등록 중 추가하기 위한 구간의 하행이 기존 구간에 존재하면 예외를 던진다.", () -> {
                    Section appendSection = new Section(1L, 4L, 1L, 3);

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @DisplayName("갈래길 방지")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromForkedLoad() {
        Section basedSection = new Section(1L, 1L, 3L, 7);
        Sections sections = new Sections(List.of(basedSection));

        return Stream.of(
                dynamicTest("상행이 같은 구간이 추가될 때 기존 가장 앞단 구간의 길이 보다 작은 경우 추가한다.", () -> {
                    Section appendSection = new Section(1L, 1L, 2L, 4);

                    assertDoesNotThrow(() -> sections.append(appendSection));
                }),

                dynamicTest("상행이 같은 구간이 추가될 때 기존 가장 앞단 구간의 길이와 같거나 큰 경우 예외를 던진다.", () -> {
                    Section appendSection = new Section(1L, 1L, 2L, 7);

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("하행이 같은 구간이 추가될 때 기존 가장 뒷단 구간의 길이 보다 작은 경우 추가한다.", () -> {
                    Section appendSection = new Section(1L, 4L, 3L, 2);

                    assertDoesNotThrow(() -> sections.append(appendSection));
                }),

                dynamicTest("하행이 같은 구간이 추가될 때 기존 가장 뒷간 구간의 길이와 같거나 큰 경우 예외를 던진다.", () -> {
                    Section appendSection = new Section(1L, 4L, 3L, 7);

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @DisplayName("상행역 하행역 중복 검증")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromDuplicateStation() {
        Section basedSection1 = new Section(1L, 2L, 1L, 4);
        Section basedSection2 = new Section(1L, 1L, 3L, 7);
        Sections sections = new Sections(List.of(basedSection1, basedSection2));

        return Stream.of(
                dynamicTest("2 - 1 구간 등록 시 상행역과 하행역이 모두 중복이므로 예외를 던진다.", () -> {
                    Section appendSection = new Section(1L, 2L, 1L, 3);

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("1 - 3 구간 등록 시 상행역과 하행역이 모두 중복이므로 예외를 던진다.", () -> {
                    Section appendSection = new Section(1L, 1L, 3L, 3);

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("상행역 하행역이 모두 포함되지 않는 경우 예외를 던진다.", () -> {
                    Section appendSection = new Section(1L, 4L, 5L, 3);

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("상행역 하행역이 모두 포함하는 경우 예외를 던진다.", () -> {
                    Section appendSection = new Section(1L, 1L, 2L, 3);

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @DisplayName("구간 삭제 기능")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromRemoveSection() {
        Long stationId1 = 1L;
        Long stationId2 = 2L;
        Long stationId3 = 3L;
        Long stationId4 = 4L;
        Long stationId5 = 5L;

        Section basedSection1 = new Section(1L, 1L, 2L, 10);
        Section basedSection2 = new Section(1L, 2L, 3L, 10);
        Section basedSection3 = new Section(1L, 3L, 4L, 10);
        Section basedSection4 = new Section(1L, 4L, 5L, 10);

        Sections sections = new Sections(List.of(basedSection1, basedSection2, basedSection3, basedSection4));

        return Stream.of(
                dynamicTest("중간에 위치한 역을 삭제한다.", () -> {
                    assertDoesNotThrow(() -> sections.remove(stationId2));
                }),

                dynamicTest("상행 종점의 구간을 삭제한다.", () -> {
                    assertDoesNotThrow(() -> sections.remove(stationId1));
                }),

                dynamicTest("존재하지 않는 역을 삭제할 경우 예외를 던진다.", () -> {
                    assertThatThrownBy(() -> sections.remove(stationId1))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("하행 종점의 구간을 삭제한다.", () -> {
                    assertDoesNotThrow(() -> sections.remove(stationId5));
                }),

                dynamicTest("구간이 한개 뿐인 경우 예외를 던진다.", () -> {
                    assertThatThrownBy(() -> sections.remove(stationId2))
                            .isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @DisplayName("조회 시 상행역 부터 하행역 순으로 정렬한다.")
    @Test
    void 구간_조회() {
        Section basedSection1 = new Section(1L, 2L, 1L, 4);
        Section basedSection2 = new Section(1L, 1L, 3L, 7);
        Section basedSection3 = new Section(1L, 4L, 2L, 7);
        Sections sections = new Sections(List.of(basedSection1, basedSection2, basedSection3));

        List<Section> value = sections.getValue();

        assertAll(
                () -> assertThat(value.get(0).getUpStationId()).isEqualTo(4),
                () -> assertThat(value.get(1).getUpStationId()).isEqualTo(2),
                () -> assertThat(value.get(2).getUpStationId()).isEqualTo(1)
        );
    }

    @DisplayName("상행역 부터 하행역까지 정렬된 지하철역을 조회한다.")
    @Test
    void 지하철역_조회() {
        Section basedSection1 = new Section(1L, 2L, 1L, 4);
        Section basedSection2 = new Section(1L, 1L, 3L, 7);
        Section basedSection3 = new Section(1L, 4L, 2L, 7);
        Sections sections = new Sections(List.of(basedSection1, basedSection2, basedSection3));

        List<Long> stationIds = sections.getStationIds();

        System.out.println(stationIds);

        assertAll(
                () -> assertThat(stationIds.size()).isEqualTo(4),
                () -> assertThat(stationIds.get(0)).isEqualTo(4),
                () -> assertThat(stationIds.get(1)).isEqualTo(2),
                () -> assertThat(stationIds.get(2)).isEqualTo(1),
                () -> assertThat(stationIds.get(3)).isEqualTo(3)
        );
    }
}
