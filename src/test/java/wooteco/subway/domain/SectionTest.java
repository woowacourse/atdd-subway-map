package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    private final Station 신림역 = new Station("신림역");
    private final Station 봉천역 = new Station("봉천역");
    private final Station 낙성대역 = new Station("낙성대역");
    private final Station 서울대입구역 = new Station("서울대입구역");

    @DisplayName("Section에 들어오는 Station은 null일 수 없다.")
    @Test
    void validateNull() {
        assertThatThrownBy(() -> new Section(null, null, 7))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("Section에 들어오는 distance는 0 이하면 안된다.")
    @Test
    void validateDistanceOverZero() {
        assertThatThrownBy(() -> new Section(new Station("신림역"), new Station("신대방역"), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 0 이하가 될 수 없습니다.");
    }

    @DisplayName("Section에 하나라도 상행 혹은 하행으로 등록된 Station이 존재하는지 확인한다.")
    @Test
    void haveAnyStation() {
        Section section = new Section(신림역, 봉천역, 5);
        Section section2 = new Section(봉천역, 낙성대역, 5);
        Section section3 = new Section(낙성대역, 서울대입구역, 5);

        assertThat(section.haveAnyStation(section2)).isTrue();
        assertThat(section.haveAnyStation(section3)).isFalse();
    }

    @DisplayName("구간이 삽입하려는 구간의 upStation을 포함하고 있는지 확인한다.")
    @Test
    void haveUpStation() {
        Section section = new Section(신림역, 봉천역, 5);
        Section insertSection = new Section(신림역, 낙성대역, 5);

        assertThat(section.haveUpStation(insertSection)).isTrue();
    }

    @DisplayName("구간이 삽입하려는 구간의 downStation을 포함하고 있는지 확인한다.")
    @Test
    void haveDownStation() {
        Section section = new Section(신림역, 봉천역, 5);
        Section insertSection = new Section(낙성대역, 신림역, 5);

        assertThat(section.haveDownStation(insertSection)).isTrue();
    }

    @DisplayName("구간이 같은 upStation 혹은 downStation을 가지는지 확인한다.")
    @Test
    void isSameUpOrDownStation() {
        Section section = new Section(신림역, 봉천역, 5);
        Section insertSection = new Section(신림역, 서울대입구역, 3);

        assertThat(section.isSameUpOrDownStation(insertSection)).isTrue();

        Section section2 = new Section(신림역, 봉천역, 5);
        Section insertSection2 = new Section(낙성대역, 봉천역, 3);

        assertThat(section2.isSameUpOrDownStation(insertSection2)).isTrue();
    }

    @DisplayName("구간이 더 짧거나 같은지 확인한다")
    @Test
    void isShortAndEqualDistanceThan() {
        Section section = new Section(신림역, 봉천역, 2);
        Section another = new Section(신림역, 봉천역, 3);

        assertThat(section.isShortAndEqualDistanceThan(another)).isTrue();
    }

    @DisplayName("역이 해당 구간에 들어있는지 확인한다.")
    @Test
    void haveStation() {
        Section section = new Section(신림역, 봉천역, 5);
        assertThat(section.haveStation(신림역)).isTrue();
        assertThat(section.haveStation(낙성대역)).isFalse();
    }

    @DisplayName("겹치는 역이 존재하는 두 구간을 합친다.")
    @Test
    void combine() {
        Section section = new Section(신림역, 봉천역, 5);
        Section section2 = new Section(봉천역, 낙성대역, 5);

        Section result = section.combine(section2, 봉천역);
        assertThat(result).isEqualTo(new Section(신림역, 낙성대역, 10));
    }

    @DisplayName("겹치는 역을 지정해주지 않고 두 구간을 합치려할 떄 예외를 발생시킨다.")
    @Test
    void combine_noConnectedStation() {
        Section section = new Section(신림역, 봉천역, 5);
        Section section2 = new Section(서울대입구역, 낙성대역, 5);

        assertThatThrownBy(() -> section.combine(section2, 서울대입구역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("양쪽 구간에 겹치는 역을 올바르게 지정해야합니다.");
    }
}
