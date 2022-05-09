package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    private final Station sinlimStation = new Station("신림역");
    private final Station bongcheonStation = new Station("봉천역");
    private final Station jamsilStation = new Station("잠실역");
    private final Station nakseongdaeStation = new Station("낙성대역");

    @DisplayName("구간은 하나 이상 존재해야 합니다.")
    @Test
    void validateSize() {
        assertThatThrownBy(() -> new Sections(List.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("구간은 하나 이상 존재해야 합니다.");
    }

    @DisplayName("상행역, 하행역 둘 중 하나도 노선에 포함되지 않는 경우 예외를 발생시킨다.")
    @Test
    void insert_noStationException() {
        Sections sections = new Sections(new Section(sinlimStation, bongcheonStation, 5));

        assertThatThrownBy(() -> sections.insert(new Section(jamsilStation, nakseongdaeStation, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역 또는 하행역이 노선에 포함되어 있어야합니다.");
    }

    @DisplayName("기존에 노선에 해당 상행역, 하행역이 이미 등록되어 있다면 구간을 등록할 수 없다.")
    @Test
    void insert_sameStationsException() {
        Sections sections = new Sections(new Section(sinlimStation, bongcheonStation, 5));

        assertThatThrownBy(() -> sections.insert(new Section(sinlimStation, bongcheonStation, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 구간은 기존 노선에 이미 등록되어있습니다.");
    }

    @DisplayName("새로운 구간이 기존에 존재하는 구간 맨 뒤 혹은 맨 앞에 삽입하는 경우")
    @Test
    void insert_frontOrBack() {
        // given
        Section section = new Section(sinlimStation, bongcheonStation, 5);
        Section newSection = new Section(jamsilStation, sinlimStation, 5);
        Sections sections = new Sections(section);
        // when
        sections.insert(newSection);
        // then
        List<Section> results = sections.getSections();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results).contains(section, newSection);
    }

    @DisplayName("새로운 구간이 기존에 존재하는 구간 중간에 삽입하는 경우 : upStation 일치")
    @Test
    void insertMiddle_upStation() {
        // given
        Section section = new Section(sinlimStation, bongcheonStation, 5);
        Section section2 = new Section(bongcheonStation, nakseongdaeStation, 7);
        Section newSection = new Section(bongcheonStation, jamsilStation, 3);

        Sections sections = new Sections(List.of(section, section2));
        // when
        sections.insert(newSection);
        // then
        List<Section> results = sections.getSections();
        assertThat(results.size()).isEqualTo(3);
        assertThat(results).contains(
                section,
                new Section(bongcheonStation, jamsilStation, 3),
                new Section(jamsilStation, nakseongdaeStation, 4));
    }

    @DisplayName("새로운 구간이 기존에 존재하는 구간 중간에 삽입하는 경우 - downStation 일치")
    @Test
    void insertMiddle_downStation() {
        // given
        Section section = new Section(sinlimStation, bongcheonStation, 5);
        Section newSection = new Section(nakseongdaeStation, bongcheonStation, 3);
        Sections sections = new Sections(section);
        // when
        sections.insert(newSection);
        // then
        List<Section> results = sections.getSections();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results).contains(
                new Section(sinlimStation, nakseongdaeStation, 2),
                newSection);
    }

    @DisplayName("역 사이에 구간을 등록할 경우 기존 역 구간 길이보다 짧아야한다.")
    @Test
    void insertMiddle_distanceException() {
        // given
        Section section = new Section(bongcheonStation, nakseongdaeStation, 5);
        Section newSection = new Section(bongcheonStation, jamsilStation, 7);
        Sections sections = new Sections(section);
        // then
        assertThatThrownBy(() -> sections.insert(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이에 구간을 등록할 경우 기존 역 구간 길이보다 짧아야 합니다.");
    }
}
