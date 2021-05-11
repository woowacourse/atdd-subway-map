package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 일급컬렉션 도메인 테스트")
class SectionsTest {
    private Station station1;
    private Station station2;
    private Section section;
    private Sections sections;

    @BeforeEach
    void setUp() {
        station1 = new Station(1L, "아마역");
        station2 = new Station(2L, "마찌역");
        section = new Section(1L, 1L, station1, station2, 10);
        sections = new Sections(Arrays.asList(section));
    }

    @Test
    @DisplayName("노선에 역이 하나만 등록되어 있는지 확인한다. - 두개다 이미 등록되어 있음")
    void isOnlyOneRegistered1() {
        boolean isOnlyOneRegistered = sections.isOnlyOneRegistered(section);

        assertThat(isOnlyOneRegistered).isFalse();
    }

    @Test
    @DisplayName("노선에 역이 하나만 등록되어 있는지 확인한다. - 하나만 등록되어 있음")
    void isAlreadyRegistered2() {
        Section newSection = new Section(new Station(1L), new Station(3L), 10);
        boolean alreadyRegistered = sections.isOnlyOneRegistered(newSection);

        assertThat(alreadyRegistered).isTrue();
    }

    @Test
    @DisplayName("순서대로 정렬한 역 목록을 반환한다.")
    void sortedStations() {
        assertThat(sections.sortedStations()).containsExactly(station1, station2);
    }

//    @Test
//    @DisplayName("구간을 추가한 sections를 반환시 노선에 등록할 구간의 역이 하나만 등록되어 있지 않은 경우 예외가 발생한다. - 둘다 등록 안돼있음")
//    void addedSectionsException1() {
//        Station station3 = new Station(3L, "잠실역");
//        Station station4 = new Station(4L, "강남역");
//        Section newSection = new Section(station4, station3, 5);
//        assertThatThrownBy(() -> sections.addedSection(newSection))
//                .isInstanceOf(IllegalStateException.class);
//    }
//
//    @Test
//    @DisplayName("구간을 추가한 sections를 반환시 노선에 등록할 구간의 역이 하나만 등록되어 있지 않은 경우 예외가 발생한다. - 둘다 등록돼있음")
//    void addedSectionsException2() {
//        Section newSection = new Section(station1, station2, 5);
//        assertThatThrownBy(() -> sections.addedSection(newSection))
//                .isInstanceOf(IllegalStateException.class);
//    }
//
//    @Test
//    @DisplayName("구간을 추가한 sections를 반환시 노선에 등록할 구간의 역이 하나만 등록되어 있지 않은 경우 예외가 발생한다. - 둘다 등록돼있음")
//    void addedSectionsException3() {
//        Section newSection = new Section(station1, station2, 5);
//        assertThatThrownBy(() -> sections.addedSection(newSection))
//                .isInstanceOf(IllegalStateException.class);
//    }
//
//    @ParameterizedTest
//    @ValueSource(ints = {10, 15})
//    @DisplayName("구간을 추가한 sections를 반환시 거리가 기존 구간 길이보다 크거나 같으면 예외가 발생한다.")
//    void addedSectionsException4(int distance) {
//        Station station3 = new Station(3L, "잠실역");
//        Section newSection = new Section(station1, station3, distance);
//        assertThatThrownBy(() -> sections.addedSection(newSection))
//                .isInstanceOf(IllegalArgumentException.class);
//    }

//    @Test
//    @DisplayName("")
//    void affectedSection() {
//        // 1아마 - 2마찌
//        // 1아마- 3잠실 추가
//        // affectedSection =
//        Station station3 = new Station(3L, "잠실역");
//        Section newSection = new Section(2L,1L,  station1, station3, 5);
//        Sections addedSections = sections.addedSections(newSection);
//        assertThat(addedSections.sortedStations()).hasSize(3);
//        assertThat(sections.sortedStations()).hasSize(2);
//        assertThat(addedSections.affectedSection(sections).id()).isEqualTo(10);
//    }
}