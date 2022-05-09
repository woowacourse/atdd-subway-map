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

        sections.add(section1);
        assertThat(sections.size()).isEqualTo(2);
    }

    @DisplayName("기존에 있는 역에 연결되지 않는 구간 추가 시 에러를 응답한다.")
    @Test
    void addSection_withNoConnection() {
        Section section = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Sections sections = new Sections(List.of(section));

        Section section2 = new Section(new Station(3L, "역삼역"), new Station(4L, "잠실역"), 10);

        assertThatThrownBy(() -> sections.add(section2)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("해당 구간은 연결 지점이 없습니다");
    }

    @DisplayName("기존에 있던 노선에서 갈 수 있는 구간 추가 시 에러를 응답한다.")
    @Test
    void addSection_withAlreadyCanGo() {
        Section section = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Section section1 = new Section(new Station(2L, "선릉역"), new Station(3L, "잠실역"), 10);
        Sections sections = new Sections(List.of(section, section1));

        Section sectionToAdd = new Section(new Station(1L, "강남역"), new Station(3L, "잠실역"), 10);

        assertThatThrownBy(() -> sections.add(sectionToAdd)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("해당 구간은 이미 이동 가능합니다.");
    }
}
