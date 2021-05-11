package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 일급컬렉션 도메인 테스트")
class SectionsTest {
    private Station station1;
    private Station station2;
    private Section section;
    private Sections sections;
    private Station station3;

    @BeforeEach
    void setUp() {
        station1 = new Station(1L, "아마역");
        station2 = new Station(2L, "마찌역");
        station3 = new Station(3L, "잠실역");
        section = new Section(1L, 1L, station1, station2, 10);
        sections = new Sections(Arrays.asList(section));
    }

    @Test
    @DisplayName("등록하려는 구간으로 등록된 구간들에 이미 존재하는 역을 찾는다.")
    void registeredStation() {
        // given
        Section newSection = new Section(1L, 1L, station1, station3, 10);

        // when
        Station station = sections.registeredStation(newSection);

        // then
        assertThat(station).isEqualTo(station1);
    }

    @Test
    @DisplayName("등록하려는 구간으로 등록된 구간들에 이미 존재하는 역을 찾는다. - 예외 : 두 역 모두 존재할 시")
    void registeredStationException1() {
        assertThatThrownBy(() -> sections.registeredStation(section))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("등록하려는 구간으로 등록된 구간들에 이미 존재하는 역을 찾는다. - 예외 : 두 역 모두 존재하지 않을 시")
    void registeredStationException2() {
        Section newSection = new Section(1L, 1L, station3, new Station(4L), 10);
        assertThatThrownBy(() -> sections.registeredStation(newSection))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("순서대로 정렬한 역 목록을 반환한다.")
    void sortedStations() {
        assertThat(sections.sortedStations()).containsExactly(station1, station2);
    }

    @Test
    @DisplayName("해당 station을 upStation으로 가지고 있는 구간을 찾는다.")
    void findSectionWithUpStation1() {
        Section findSection = sections.findSectionWithUpStation(station1);

        assertThat(findSection).isEqualTo(section);
    }

    @Test
    @DisplayName("해당 station을 upStation으로 가지고 있는 구간을 찾는다. - 존재하지 않음")
    void findSectionWithUpStation2() {
        Section findSection = sections.findSectionWithUpStation(station3);

        assertThat(findSection.id()).isEqualTo(0L);
    }

    @Test
    @DisplayName("해당 station을 downStation으로 가지고 있는 구간을 찾는다.")
    void findSectionWithDownStation1() {
        Section findSection = sections.findSectionWithDownStation(station2);

        assertThat(findSection).isEqualTo(section);
    }

    @Test
    @DisplayName("해당 station을 downStation으로 가지고 있는 구간을 찾는다. - 존재하지 않음")
    void findSectionWithDownStation2() {
        Section findSection = sections.findSectionWithDownStation(station3);

        assertThat(findSection.id()).isEqualTo(0L);
    }

    @Test
    @DisplayName("size를 확인한다.")
    void size() {
        assertThat(sections.size()).isEqualTo(1);
    }
}