package wooteco.subway.line.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.ValidationFailureException;

public class SectionsTest {

    private static Sections sections;

    @BeforeEach
    void setUp() {
        final List<Section> sectionGroup = new ArrayList<>();
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(3L).downStationId(2L).distance(10).build());
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(4L).downStationId(5L).distance(5).build());
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(2L).downStationId(1L).distance(6).build());
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(1L).downStationId(4L).distance(8).build());
        sections = new Sections(sectionGroup);
    }

    @DisplayName("Sections를 생성할 때 구간을 정렬한다.")
    @Test
    void create() {
        final List<Long> expected = Arrays.asList(3L, 2L, 1L, 4L, 5L);
        final List<Long> ids = sections.distinctStationIds();
        assertThat(ids).isEqualTo(expected);
    }

    @DisplayName("시작 구간을 찾을 수 없으면 Sections 생성에 실패한다.")
    @Test
    void create_fail() {
        final List<Section> sectionGroup = new ArrayList<>();
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(1L).downStationId(3L).distance(10).build());
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(3L).downStationId(2L).distance(5).build());
        sectionGroup.add(Section.Builder().lineId(1L).upStationId(2L).downStationId(1L).distance(6).build());

        assertThatThrownBy(() -> new Sections(sectionGroup))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessage("해당하는 상행역이 없습니다.");
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void validateBothExistentStation() {
        assertThatThrownBy(() -> sections.validateBothExistentStation(2L, 4L))
            .isInstanceOf(ValidationFailureException.class)
            .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void validateNoneExistentStation() {
        assertThatThrownBy(() -> sections.validateNoneExistentStation(6L, 7L))
            .isInstanceOf(ValidationFailureException.class)
            .hasMessage("상행역과 하행역 둘 다 포함되어있지 않습니다.");
    }

    @DisplayName("상행종점역을 확인한다.")
    @Test
    void isFirstStationId() {
        assertThat(sections.isFirstStationId(3L)).isTrue();
    }

    @DisplayName("하행종점역을 확인한다.")
    @Test
    void isLastStationId() {
        assertThat(sections.isLastStationId(5L)).isTrue();
    }

    @DisplayName("특정 상행역을 갖는 구간을 찾는다.")
    @Test
    void findSectionHasUpStation() {
        assertThat(sections.findSectionHasUpStation(2L))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(
                Section.Builder().lineId(1L).upStationId(2L).downStationId(1L).distance(6).build()
            );
    }

    @DisplayName("특정 하행역을 갖는 구간을 찾는다.")
    @Test
    void findSectionHasDownStation() {
        assertThat(sections.findSectionHasDownStation(2L))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(
                Section.Builder().lineId(1L).upStationId(3L).downStationId(2L).distance(10).build()
            );
    }

    @DisplayName("특정 상행역이나 하행역이 같은 구간을 찾는다.")
    @Test
    void findSameForm() {
        assertThat(sections.findSameForm(6L, 5L))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(
                Section.Builder().lineId(1L).upStationId(4L).downStationId(5L).distance(5).build()
            );
    }

    @DisplayName("구간의 개수를 확인한다.")
    @Test
    void size() {
        assertThat(sections.size()).isEqualTo(4);
    }
}
