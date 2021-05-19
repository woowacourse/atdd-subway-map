package wooteco.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.InvalidInsertException;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {
    private final Station 첫번째역 = new Station(1L,"성수역");
    private final Station 두번째역 = new Station(2L,"건대입구");
    private final Station 세번째역 = new Station(3L,"구의역");
    private final Station 네번째역 = new Station(4L,"잠실역");

    private final Line 이호선 = new Line(1L, "이호선", "초록");

    private final Section 성수에서건대 = new Section(이호선, 첫번째역, 두번째역, 4);
    private final Section 건대에서구의 = new Section(이호선, 두번째역, 세번째역, 3);
    private final Section 구의에서잠실 = new Section(이호선, 세번째역, 네번째역, 5);

    @Test
    @DisplayName("구간의 상행과 동행이 같은 경우 예외를 발생시킨다.")
    void testCreateSection() {
        Station upStation = new Station("당산역");
        Station downStation = new Station("당산역");
        assertThatThrownBy(() -> new Section(이호선, upStation, downStation, 0))
                .isExactlyInstanceOf(InvalidInsertException.class);
    }

    @Test
    @DisplayName("연결된 구간끼리 상행역과 하행역을 비교해 구간의 순서를 구한다.")
    void testCheckSequence() {
        assertThat(성수에서건대.isBefore(건대에서구의)).isTrue();
        assertThat(건대에서구의.isAfter(성수에서건대)).isTrue();
        assertThat(건대에서구의.isBefore(구의에서잠실)).isTrue();
        assertThat(구의에서잠실.isAfter(건대에서구의)).isTrue();
    }

    @Test
    @DisplayName("상행 아이디가 같은 지 확인한다.")
    void testSameUpId() {
        Section 성수에서잠실 = new Section(이호선, 첫번째역, 네번째역, 3);

        assertThat(성수에서건대.isSameAsUpId(성수에서잠실.getUpStationId())).isTrue();
        assertThat(성수에서건대.isSameAsUpId(건대에서구의.getUpStationId())).isFalse();
    }

    @Test
    @DisplayName("하행 아이디가 같은 지 확인한다.")
    void testSameDownId() {
        Section 잠실에서건대 = new Section(이호선, 네번째역, 두번째역, 4);

        assertThat(성수에서건대.isSameAsDownId(잠실에서건대.getDownStationId())).isTrue();
        assertThat(성수에서건대.isSameAsDownId(건대에서구의.getDownStationId())).isFalse();
    }

    @Test
    @DisplayName("추가하려는 구간의 거리가 기존 구간의 거리보다 긴 경우 예외를 발생한다.")
    void testCompareDistance() {
        assertThatThrownBy(() -> 성수에서건대.subtractDistance(구의에서잠실))
                .isExactlyInstanceOf(InvalidInsertException.class)
                .hasMessage("추가하려는 구간의 거리는 기존 구간의 거리를 넘을 수 없습니다.");

        assertThatThrownBy(() -> 건대에서구의.subtractDistance(구의에서잠실))
                .isExactlyInstanceOf(InvalidInsertException.class)
                .hasMessage("추가하려는 구간의 거리는 기존 구간의 거리를 넘을 수 없습니다.");
    }

    @Test
    @DisplayName("구간끼리의 거리를 더한다.")
    void testPlusDistance() {
        assertThat(성수에서건대.plusDistance(건대에서구의)).isEqualTo(7);
        assertThat(건대에서구의.plusDistance(구의에서잠실)).isEqualTo(8);
    }

    @Test
    @DisplayName("구간끼리의 거리를 뺀다.")
    void testSubtractDistance() {
        assertThat(성수에서건대.subtractDistance(건대에서구의)).isEqualTo(1);
        assertThat(구의에서잠실.subtractDistance(건대에서구의)).isEqualTo(2);
    }
}
