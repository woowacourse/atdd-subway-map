package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {
    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        Section section = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Sections sections = new Sections(List.of(section));
        Section section1 = new Section(new Station(2L, "선릉역"), new Station(3L, "역삼역"), 10);

        sections.updateToAdd(section1);
    }

    @DisplayName("기존에 있는 역에 연결되지 않는 구간 추가 시 에러를 응답한다.")
    @Test
    void addSection_withNoConnection() {
        Section section = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Sections sections = new Sections(List.of(section));

        Section section2 = new Section(new Station(3L, "역삼역"), new Station(4L, "잠실역"), 10);

        assertThatThrownBy(() -> sections.updateToAdd(section2)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("해당 구간은 연결 지점이 없습니다");
    }

    @DisplayName("기존에 있던 노선에서 갈 수 있는 구간 추가 시 에러를 응답한다.")
    @Test
    void addSection_withAlreadyCanGo() {
        Section section = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Section section1 = new Section(new Station(2L, "선릉역"), new Station(3L, "잠실역"), 10);
        Sections sections = new Sections(List.of(section, section1));

        Section sectionToAdd = new Section(new Station(1L, "강남역"), new Station(3L, "잠실역"), 10);

        assertThatThrownBy(() -> sections.updateToAdd(sectionToAdd)).isInstanceOf(
            IllegalArgumentException.class)
            .hasMessage("해당 구간은 이미 이동 가능합니다.");
    }

    @DisplayName("기존에 있던 구간 내부에 구간 추가 시 추가하려는 distance가 더 크면 에러를 응답한다.")
    @Test
    void addSection_withInnerSectionIsBigger() {
        Section section = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Sections sections = new Sections(List.of(section));

        Section sectionToAdd = new Section(new Station(1L, "강남역"), new Station(3L, "역삼역"), 11);
        assertThatThrownBy(() -> sections.updateToAdd(sectionToAdd)).isInstanceOf(
            IllegalArgumentException.class)
            .hasMessage("해당 구간은 추가될 수 없습니다.");
    }

    @DisplayName("기존에 있던 구간 내부에 구간 추가 시 기존 구간이 정상적으로 업데이트 되는지 테스트 한다.")
    @Test
    void addSection_withUpdateSection() {
        Section section = new Section(1L, new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Sections sections = new Sections(List.of(section));

        Section sectionToAdd = new Section(2L, new Station(1L, "강남역"), new Station(3L, "역삼역"), 7);
        sections.updateToAdd(sectionToAdd);

        Section resultSection = sections.findById(1L);
        assertThat(resultSection.getDistance()).isEqualTo(3);
        assertThat(resultSection.getUpStation()).isEqualTo(new Station(3L, "역삼역"));
    }
}
