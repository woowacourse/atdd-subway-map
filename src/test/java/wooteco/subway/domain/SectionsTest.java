package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static wooteco.subway.domain.DomainFixtures.강남역;
import static wooteco.subway.domain.DomainFixtures.분당선구간1;
import static wooteco.subway.domain.DomainFixtures.서울숲역;
import static wooteco.subway.domain.DomainFixtures.역삼역;
import static wooteco.subway.domain.DomainFixtures.왕십리역;
import static wooteco.subway.domain.DomainFixtures.호선2구간1;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("상행 역에 구간을 추가할 수 있는지 확인한다.")
    void checkAddSectionInUpStation() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        assertThat(sections.checkAddSectionInUpStation(왕십리역, 3)).isTrue();
    }

    @Test
    @DisplayName("상행 역에 구간을 추가할 때 알맞은 상행 역이 없으면 false 를 반환한다.")
    void checkAddSectionInUpStationWithNotExistUpStation() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        assertThat(sections.checkAddSectionInUpStation(서울숲역, 3)).isFalse();
    }

    @Test
    @DisplayName("상행 역에 구간을 추가할 때 거리가 기존 거리보다 멀면 예외를 반환한다.")
    void checkAddSectionInUpStationWithLongerDistance() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        assertThatThrownBy(() -> sections.checkAddSectionInUpStation(왕십리역, 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("6");
    }

    @Test
    @DisplayName("상행 역 Id 에 따라 알맞은 구간을 반환한다.")
    void getOriginUpStationSection() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        Section section = sections.getOriginUpStationSection(1L);
        assertThat(section.getUpStation().getName()).isEqualTo("왕십리역");
    }

    @Test
    @DisplayName("상행 역 Id 에 따른 알맞은 구간이 없으면 예외를 반환한다.")
    void getOriginUpStationSectionWithNotExistStationId() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        assertThatThrownBy(() -> sections.getOriginUpStationSection(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("찾을 수 없습니다");
    }

    @Test
    @DisplayName("하행 역에 구간을 추가할 수 있는지 확인한다.")
    void checkAddSectionInDownStation() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        assertThat(sections.checkAddSectionInDownStation(서울숲역, 3)).isTrue();
    }

    @Test
    @DisplayName("하행 역에 구간을 추가할 때 알맞은 하행 역이 없으면 false 를 반환한다.")
    void checkAddSectionInDownStationWithNotExistDownStation() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        assertThat(sections.checkAddSectionInDownStation(왕십리역, 3)).isFalse();
    }

    @Test
    @DisplayName("하행 역에 구간을 추가할 때 거리가 기존 거리보다 멀면 예외를 반환한다.")
    void checkAddSectionInDownStationWithLongerDistance() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        assertThatThrownBy(() -> sections.checkAddSectionInDownStation(서울숲역, 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("6");
    }

    @Test
    @DisplayName("하행 역 Id 에 따라 알맞은 구간을 반환한다.")
    void getOriginDownStationSection() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        Section section = sections.getOriginDownStationSection(2L);
        assertThat(section.getDownStation().getName()).isEqualTo("서울숲역");
    }

    @Test
    @DisplayName("하행 역 Id 에 따른 알맞은 구간이 없으면 예외를 반환한다.")
    void getOriginDownStationSectionWithNotExistStationId() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        assertThatThrownBy(() -> sections.getOriginDownStationSection(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("찾을 수 없습니다");
    }

    @Test
    @DisplayName("상행 역과 하행 역이 모두 있으면 true 를 반환한다.")
    void checkSameStations() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        assertThat(sections.checkSameStations(왕십리역, 서울숲역)).isTrue();
    }

    @Test
    @DisplayName("하행 역과 상행 역이 모두 있으면 true 를 반환한다.")
    void checkSameStationsWithReverse() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        assertThat(sections.checkSameStations(서울숲역, 왕십리역)).isTrue();
    }

    @Test
    void canAddEndOfTheLine() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        assertThat(sections.canAddEndOfTheLine(왕십리역, 강남역)).isTrue();
    }

    @Test
    void canAddEndOfTheLineWithNotExistStations() {
        Sections sections = new Sections(List.of(분당선구간1));
        assertThat(sections.canAddEndOfTheLine(강남역, 역삼역)).isFalse();
    }

    @Test
    void getSectionIds() {
        Sections sections = new Sections(List.of(분당선구간1, 호선2구간1));
        List<Long> sectionIds = sections.getSectionIds();
        assertThat(sectionIds).containsExactly(1L, 2L);
    }
}
