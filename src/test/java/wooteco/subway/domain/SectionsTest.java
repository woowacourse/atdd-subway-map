package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static wooteco.subway.domain.SectionFixtures.*;
import static wooteco.subway.domain.StationFixtures.*;

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
        Section basedSection = 역곡역_부천역_5;
        Sections sections = new Sections(List.of(basedSection));

        return Stream.of(
                dynamicTest("상행 종점을 등록한다.", () -> {
                    Section appendSection = 온수역_역곡역_5;

                    assertDoesNotThrow(() -> sections.append(appendSection));
                }),

                dynamicTest("상행 종점 등록 중 추가하기 위한 구간의 상행이 기존 구간에 존재하면 예외를 던진다.", () -> {
                    Section appendSection = 부천역_역곡역_5;

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("하행 종점을 등록한다.", () -> {
                    Section appendSection = 부천역_중동역_5;

                    assertDoesNotThrow(() -> sections.append(appendSection));
                }),

                dynamicTest("하행 종점 등록 중 추가하기 위한 구간의 하행이 기존 구간에 존재하면 예외를 던진다.", () -> {
                    Section appendSection = 중동역_역곡역_5;

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @DisplayName("갈래길 방지")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromForkedLoad() {
        Section basedSection = 신도림역_부천역_15;
        Sections sections = new Sections(List.of(basedSection));

        return Stream.of(
                dynamicTest("상행이 같은 구간이 추가될 때 기존 가장 앞단 구간의 길이 보다 작은 경우 추가한다.", () -> {
                    Section appendSection = 신도림역_온수역_5;

                    assertDoesNotThrow(() -> sections.append(appendSection));
                }),

                dynamicTest("상행이 같은 구간이 추가될 때 기존 가장 앞단 구간의 길이와 같거나 큰 경우 예외를 던진다.", () -> {
                    Section appendSection = 신도림역_개봉역_10;

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("하행이 같은 구간이 추가될 때 기존 가장 뒷단 구간의 길이 보다 작은 경우 추가한다.", () -> {
                    Section appendSection = 역곡역_부천역_5;

                    assertDoesNotThrow(() -> sections.append(appendSection));
                }),

                dynamicTest("하행이 같은 구간이 추가될 때 기존 가장 뒷간 구간의 길이와 같거나 큰 경우 예외를 던진다.", () -> {
                    Section appendSection = 소사역_부천역_5;

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @DisplayName("상행역 하행역 중복 검증")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromDuplicateStation() {
        Section basedSection1 = 신도림역_온수역_5;
        Section basedSection2 = 온수역_역곡역_5;
        Sections sections = new Sections(List.of(basedSection1, basedSection2));

        return Stream.of(
                dynamicTest("1 - 2 구간 등록 시 상행역과 하행역이 모두 중복이므로 예외를 던진다.", () -> {
                    Section appendSection = 신도림역_온수역_5;

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("2 - 3 구간 등록 시 상행역과 하행역이 모두 중복이므로 예외를 던진다.", () -> {
                    Section appendSection = 온수역_역곡역_5;

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("상행역 하행역이 모두 포함되지 않는 경우 예외를 던진다.", () -> {
                    Section appendSection = 부천역_중동역_5;

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("상행역 하행역이 모두 포함하는 경우 예외를 던진다.", () -> {
                    Section appendSection = 신도림역_역곡역_13;

                    assertThatThrownBy(() -> sections.append(appendSection))
                            .isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @DisplayName("구간 삭제 기능")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromRemoveSection() {
        Section basedSection1 = 신도림역_온수역_5;
        Section basedSection2 = 온수역_역곡역_5;
        Section basedSection3 = 역곡역_부천역_5;
        Section basedSection4 = 부천역_중동역_5;

        Sections sections = new Sections(List.of(basedSection1, basedSection2, basedSection3, basedSection4));

        return Stream.of(
                dynamicTest("중간에 위치한 역을 삭제한다.", () -> {
                    assertDoesNotThrow(() -> sections.remove(온수역));
                }),

                dynamicTest("상행 종점의 구간을 삭제한다.", () -> {
                    assertDoesNotThrow(() -> sections.remove(신도림역));
                }),

                dynamicTest("존재하지 않는 역을 삭제할 경우 예외를 던진다.", () -> {
                    assertThatThrownBy(() -> sections.remove(신도림역))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("하행 종점의 구간을 삭제한다.", () -> {
                    assertDoesNotThrow(() -> sections.remove(중동역));
                }),

                dynamicTest("구간이 한개 뿐인 경우 예외를 던진다.", () -> {
                    assertThatThrownBy(() -> sections.remove(역곡역))
                            .isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @DisplayName("조회 시 상행역 부터 하행역 순으로 정렬한다.")
    @Test
    void 구간_조회() {
        Section basedSection1 = 신도림역_온수역_5;
        Section basedSection2 = 온수역_역곡역_5;
        Section basedSection3 = 역곡역_부천역_5;
        Sections sections = new Sections(List.of(basedSection1, basedSection2, basedSection3));

        List<Section> value = sections.getSortedValue();

        assertAll(
                () -> assertThat(value.get(0).getUpStation()).isEqualTo(신도림역),
                () -> assertThat(value.get(1).getUpStation()).isEqualTo(온수역),
                () -> assertThat(value.get(2).getUpStation()).isEqualTo(역곡역)
        );
    }

    @DisplayName("상행역 부터 하행역까지 정렬된 지하철역을 조회한다.")
    @Test
    void 지하철역_조회() {
        Section basedSection1 = 신도림역_온수역_5;
        Section basedSection2 = 온수역_역곡역_5;
        Section basedSection3 = 역곡역_부천역_5;
        Sections sections = new Sections(List.of(basedSection1, basedSection2, basedSection3));

        List<Station> stations = sections.getSortedStations();

        assertAll(
                () -> assertThat(stations.size()).isEqualTo(4),
                () -> assertThat(stations).containsExactly(신도림역, 온수역, 역곡역, 부천역)
        );
    }
}
