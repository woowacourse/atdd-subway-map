package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.InvalidInsertException;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    private final Station 성수역 = new Station(1L,"성수역");
    private final Station 건대입구역 = new Station(2L,"건대입구");
    private final Station 구의역 = new Station(3L,"구의역");
    private final Station 잠실역 = new Station(4L,"잠실역");

    private final Line 이호선 = new Line(1L, "이호선", "초록");

    private final Section 성수에서건대 = new Section(이호선, 성수역, 건대입구역, 4);
    private final Section 건대에서구의 = new Section(이호선, 건대입구역, 구의역, 3);
    private final Section 구의에서잠실 = new Section(이호선, 구의역, 잠실역, 5);

    private List<Section> 이호선구간;

    @BeforeEach
    void setUp() {
        이호선구간 = new ArrayList<>();
        이호선구간.add(성수에서건대);
        이호선구간.add(건대에서구의);
        이호선구간.add(구의에서잠실);
    }

    @Test
    @DisplayName("추가하려는 구간이 기존 구간과 연결되지 않은 경우 예외를 발생한다.")
    void 추가하려는구간이연결안된테스트() {
        Station 등촌역 = new Station(5L, "등촌역");
        Station 염창역 = new Station(6L, "염창역");

        Section 등촌에서염창 = new Section(이호선, 등촌역, 염창역, 4);

        assertThatThrownBy(() -> new Sections(이호선구간, 등촌에서염창))
                .isExactlyInstanceOf(InvalidInsertException.class)
                .hasMessage("해당 구간에는 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("추가하려는 구간의 역들이 이미 다른 구간에 등록된 경우 예외를 발생한다.")
    void 추가하려는구간이이미등록된경우테스트() {
        Section 성수에서잠실 = new Section(이호선, 성수역, 잠실역, 4);

        assertThatThrownBy(() -> new Sections(이호선구간, 성수에서잠실))
                .isExactlyInstanceOf(InvalidInsertException.class)
                .hasMessage("해당 구간에는 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("추가하려는 구간이 기존 구간의 종점과 연결되는지 테스트한다.")
    void isOnEdge() {
        Station 시청역 = new Station(5L, "시청역");
        Station 사당역 = new Station(6L, "사당역");

        Section 시청에서성수 = new Section(이호선, 시청역, 성수역, 4);
        Section 잠실에서사당 = new Section(이호선, 잠실역, 사당역, 3);
        Sections 이호선 = new Sections(이호선구간);

        assertThat(이호선.isOnEdge(시청에서성수)).isTrue();
        assertThat(이호선.isOnEdge(잠실에서사당)).isTrue();
    }

    @Test
    @DisplayName("추가하려는 구간이 기존 구간의 상행종점에 연결되는지 테스트한다.")
    void isOnUpEdge() {
        Station 시청역 = new Station(5L, "시청역");

        Section 시청에서성수 = new Section(이호선, 시청역, 성수역, 4);
        Sections 이호선 = new Sections(이호선구간);

        assertThat(이호선.isOnUpEdge(시청에서성수.getDownStationId())).isTrue();
    }

    @Test
    @DisplayName("추가하려는 구간이 기존 구간의 하행종점에 연결되는지 테스트한다.")
    void isOnDownEdge() {
        Station 사당역 = new Station(6L, "사당역");

        Section 잠실에서사당 = new Section(이호선, 잠실역, 사당역, 3);
        Sections 이호선 = new Sections(이호선구간);

        assertThat(이호선.isOnDownEdge(잠실에서사당.getUpStationId())).isTrue();
    }

    @Test
    @DisplayName("추가하려는 구간이 기존에 중간 구간의 상행역과 연결되는지 테스트한다.")
    void appendToForward() {
        Station 강변역 = new Station(5L, "강변역");
        Station 잠실나루 = new Station(6L, "잠실나루역");

        Section 구의에서강변 = new Section(이호선, 구의역, 강변역, 3);
        Section 강변에서잠실나루 = new Section(이호선, 강변역, 잠실나루, 2);
        Sections 이호선 = new Sections(이호선구간);

        assertThat(이호선.appendToForward(구의에서강변)).isTrue();
        assertThat(이호선.appendToForward(강변에서잠실나루)).isFalse();
        assertThat(이호선.appendToForward(성수에서건대)).isFalse();
    }

    @Test
    @DisplayName("추가하려는 구간이 기존에 있는 중간 구간의 하행역과 연결되는지 테스트한다.")
    void appendToBackward() {
        Station 강변역 = new Station(5L, "강변역");
        Station 잠실나루 = new Station(6L, "잠실나루역");

        Section 구의에서강변 = new Section(이호선, 구의역, 강변역, 3);

        Section 잠실나루에서잠실 = new Section(이호선, 잠실나루, 잠실역, 3);
        Sections 이호선 = new Sections(이호선구간);

        assertThat(이호선.appendToBackward(잠실나루에서잠실)).isTrue();
        assertThat(이호선.appendToBackward(구의에서강변)).isFalse();
        assertThat(이호선.appendToBackward(건대에서구의)).isFalse();
    }

    @Test
    @DisplayName("연결된 순서대로 구간의 아이디를 반환하는지 테스트")
    void toSortedStationIds() {
        Station 시청역 = new Station(5L, "시청역");
        Section 시청에서성수 = new Section(이호선, 시청역, 성수역, 3);
        이호선구간.add(시청에서성수);

        Collections.shuffle(이호선구간);

        Sections 이호선 = new Sections(이호선구간);
        assertThat(이호선.toSortedStationIds()).isEqualTo(Arrays.asList(5L, 1L, 2L, 3L, 4L));
    }

    @Test
    @DisplayName("연결된 구간이 한 개 이하인 경우 삭제할 수 없는지 테스트한다.")
    void validateDeletable() {
        List<Section> 성수_건대 = Arrays.asList(성수에서건대);

        Sections 건대주변 = new Sections(성수_건대);

        assertThatThrownBy(() -> 건대주변.removeSection(이호선, 성수역))
                .isExactlyInstanceOf(InvalidInsertException.class)
                .hasMessage("구간이 한 개 이하라 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("구간 삭제 테스트")
    void testRemoveSection() {
        Sections sections = new Sections(이호선구간);
        sections.removeSection(이호선, 구의역);

        assertThat(sections.toSortedSections()).hasSize(2);
        assertThat(sections.toSortedSections().get(0)).isEqualTo(성수에서건대);
        assertThat(sections.toSortedSections().get(1))
                .usingRecursiveComparison()
                .isEqualTo(new Section(이호선, 건대입구역, 잠실역, 8));
    }
}