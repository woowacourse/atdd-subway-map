package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private static final Section SECTION_LINE_1_STATION_1_2_10 = new Section(1L, 1L, 2L, 10);
    private static final Section SECTION_LINE_1_STATION_2_3_12 = new Section(1L, 2L, 3L, 12);
    private static final Section SECTION_LINE_1_STATION_1_3_22 = new Section(1L, 1L, 1L, 3L, 22);

    @DisplayName("특정 노선에 속한 구간정보를 생성한다")
    @Test
    void create_success() {
        final List<Section> sections = List.of(SECTION_LINE_1_STATION_1_2_10, SECTION_LINE_1_STATION_2_3_12);

        assertDoesNotThrow(() -> new Sections(sections));
    }

    @DisplayName("특정 노선에 구간이 존재하지 않는다면 구간정보를 생성할 수 없다.")
    @Test
    void create_fail() {
        assertThatThrownBy(() -> new Sections(new ArrayList<>()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 존재하지 않는 구간입니다.");
    }

    @DisplayName("기존 구간정보와 상행, 하행 종점 중 하나만 같은 구간은 구간 등록이 가능하다.")
    @Test
    void addSection_valid_only_one_station_same() {
        final Sections sections = new Sections(List.of(SECTION_LINE_1_STATION_1_2_10, SECTION_LINE_1_STATION_2_3_12));
        final Section targetSection = new Section(1L, 1L, 4L, 8);

        assertDoesNotThrow(() -> sections.addSection(targetSection));
    }

    @DisplayName("기존 구간정보와 같은, 상행/하행 종점을 포함하는 경우 예외가 발생한다.")
    @Test
    void addSection_invalid_all_same_up_and_down_station() {
        final Sections sections = new Sections(List.of(SECTION_LINE_1_STATION_1_2_10, SECTION_LINE_1_STATION_2_3_12));
        final Section targetSection = new Section(1L, 1L, 2L, 12);

        assertThatThrownBy(() -> sections.addSection(targetSection))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("[ERROR] 추가할 구간 속 지하철역이 기존 구간에 이미 존재합니다.");
    }

    @DisplayName("기존 구간정보와 상행, 하행 종점 모두 다른 구간은 추가시 예외가 발생한다.")
    @Test
    void addSection_invalid_all_not_same_up_and_down_station() {
        final Sections sections = new Sections(List.of(SECTION_LINE_1_STATION_1_2_10, SECTION_LINE_1_STATION_2_3_12));
        final Section targetSection = new Section(1L, 2L, 4L, 12);

        assertThatThrownBy(() -> sections.addSection(targetSection))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("[ERROR] 구간을 추가하기 위해선 상행 혹은 하행 종점 둘 중 하나만 포함해야 합니다.");
    }

    @DisplayName("상행 종점이 같은 구간을 추가할 때, 거리가 더 클 경우 예외가 발생한다.")
    @Test
    void addSection_invalid_distance() {
        final Sections sections = new Sections(List.of(SECTION_LINE_1_STATION_1_2_10, SECTION_LINE_1_STATION_2_3_12));
        final Section targetSection = new Section(1L, 1L, 4L, 15);

        assertThatThrownBy(() -> sections.addSection(targetSection))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("[ERROR] 기존 구간보다 긴 구간을 추가할 순 없습니다.");
    }

    @DisplayName("상행종점이 같은 구간에 대해 역 사이에 새로운 역을 등록할 경우, 기존 구간을 제거하고 쪼개진 구간을 추가하여 등록한다.")
    @Test
    void addSection_same_up_station() {
        final Sections sections = new Sections(List.of(SECTION_LINE_1_STATION_1_3_22));
        final Section targetSection = new Section(3L, 1L, 1L, 2L, 15);

        final List<Section> actual = sections.addSection(targetSection);

        assertThat(actual).hasSize(2);
    }

    @DisplayName("하행 종점이 같은 구간에 대해 역 사이에 새로운 역을 등록할 경우, 기존 구간을 제거하고 쪼개진 구간을 추가하여 등록한다.")
    @Test
    void addSection_same_down_station() {
        final Sections sections = new Sections(List.of(SECTION_LINE_1_STATION_1_3_22));
        final Section targetSection = new Section(3L, 1L, 2L, 3L, 15);

        final List<Section> actual = sections.addSection(targetSection)
            .stream()
            .sorted(Comparator.comparing(Section::getUpStationId))
            .collect(Collectors.toList());

        assertAll(
            () -> assertThat(actual).hasSize(2),
            () -> assertThat(actual.get(0).getUpStationId()).isEqualTo(1L),
            () -> assertThat(actual.get(actual.size() - 1).getDownStationId()).isEqualTo(3L)
        );

    }

    @DisplayName("구간을 추가할 때, 상행/하행 종점으로 새로운 역이 추가될 수 있다.")
    @Test
    void addSection_end_station() {
        final Sections sections = new Sections(List.of(SECTION_LINE_1_STATION_2_3_12));
        final Section targetSection = new Section(3L, 1L, 1L, 2L, 15);

        final List<Section> actual = sections.addSection(targetSection)
            .stream()
            .sorted(Comparator.comparing(Section::getUpStationId))
            .collect(Collectors.toList());

        System.out.println("actual = " + actual);

        assertAll(
            () -> assertThat(actual).hasSize(2),
            () -> assertThat(actual.get(0).getUpStationId()).isEqualTo(1L),
            () -> assertThat(actual.get(actual.size() - 1).getDownStationId()).isEqualTo(3L)
        );
    }
}
