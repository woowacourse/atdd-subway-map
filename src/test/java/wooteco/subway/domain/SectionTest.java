package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    private final Station sinlimStation = new Station("신림역");
    private final Station bongcheonStation = new Station("봉천역");
    private final Station nakseongdaeStation = new Station("낙성대역");
    private final Station seouldaeStation = new Station("서울대입구역");

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
        Section section = new Section(sinlimStation, bongcheonStation, 5);
        Section section2 = new Section(bongcheonStation, nakseongdaeStation, 5);
        Section section3 = new Section(nakseongdaeStation, seouldaeStation, 5);

        assertThat(section.haveAnyStation(section2)).isTrue();
        assertThat(section.haveAnyStation(section3)).isFalse();
    }

    @DisplayName("구간이 삽입하려는 구간의 upStation을 포함하고 있는지 확인한다.")
    @Test
    void haveUpStation() {
        Section section = new Section(sinlimStation, bongcheonStation, 5);
        Section insertSection = new Section(sinlimStation, nakseongdaeStation, 5);

        assertThat(section.haveUpStation(insertSection)).isTrue();
    }

    @DisplayName("구간이 삽입하려는 구간의 downStation을 포함하고 있는지 확인한다.")
    @Test
    void haveDownStation() {
        Section section = new Section(sinlimStation, bongcheonStation, 5);
        Section insertSection = new Section(nakseongdaeStation, sinlimStation, 5);

        assertThat(section.haveDownStation(insertSection)).isTrue();
    }

    @DisplayName("구간이 같은 upStation 혹은 downStation을 가지는지 확인한다.")
    @Test
    void isSameUpOrDownStation() {
        Section section = new Section(sinlimStation, bongcheonStation, 5);
        Section insertSection = new Section(sinlimStation, seouldaeStation, 3);

        assertThat(section.isSameUpOrDownStation(insertSection)).isTrue();

        Section section2 = new Section(sinlimStation, bongcheonStation, 5);
        Section insertSection2 = new Section(nakseongdaeStation, bongcheonStation, 3);

        assertThat(section2.isSameUpOrDownStation(insertSection2)).isTrue();
    }

    @DisplayName("구간이 더 짧거나 같은지 확인한다")
    @Test
    void isShortAndEqualDistanceThan() {
        Section section = new Section(sinlimStation, bongcheonStation, 2);
        Section another = new Section(sinlimStation, bongcheonStation, 3);

        assertThat(section.isShortAndEqualDistanceThan(another)).isTrue();
    }
}
