package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;

class SectionsTest {

    private final Station station1 = new Station(1L, "아차산역");
    private final Station station2 = new Station(2L, "군자역");
    private final Section section = new Section(1L, station1, station2, 10, 1L);
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(List.of(section));
    }

    @DisplayName("추가하려는 구간의 역들이 기존 구간에 모두 존재하지 않으면 예외를 발생한다.")
    @Test
    void add_throwsNoStationExistException() {

        final Station newStation1 = new Station(3L, "여의도역");
        final Station newStation2 = new Station(4L, "마장역");
        final Section newSection = new Section(newStation1, newStation2, 5, 1L);

        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 구간을 추가할 수 없습니다.");
    }

    @DisplayName("추가하려는 구간의 역들이 기존 구간에 모두 존재하면 예외를 발생한다.")
    @Test
    void add_throwsAllStationExistException() {
        final Section newSection = new Section(station1, station2, 5, 1L);

        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
    }

    @DisplayName("상행 구간 등록시 역 사이 거리가 기존 구간보다 크거나 같을 경우 예외를 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 11, 15})
    void add_throwsUpSectionDistanceException(final int distance) {
        final Station newStation1 = new Station(1L, "아차산역");
        final Station newStation2 = new Station(3L, "군자역");
        final Section newSection = new Section(newStation1, newStation2, distance, 1L);

        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이에 새로운 역을 등록할 경우 기존 구간 거리보다 적어야 합니다.");
    }

    @DisplayName("하행 구간 등록시 역 사이 거리가 기존 구간보다 크거나 같을 경우 예외를 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 11, 15})
    void add_throwsDownSectionDistanceException(final int distance) {
        final Station newStation1 = new Station(3L, "마장역");
        final Station newStation2 = new Station(2L, "군자역");
        final Section newSection = new Section(newStation1, newStation2, distance, 1L);

        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이에 새로운 역을 등록할 경우 기존 구간 거리보다 적어야 합니다.");
    }

    @DisplayName("등록할 구간이 갈래길인지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,아차산역,3,장한평역,true", "3,천호역,1,아차산역,false"})
    void isBranched(final long stationId1, final String stationName1,
                    final long stationId2, final String stationName2, final boolean expected) {
        final Station newStation1 = new Station(stationId1, stationName1);
        final Station newStation2 = new Station(stationId2, stationName2);
        final Section newSection = new Section(newStation1, newStation2, 5, 1L);

        assertThat(sections.isBranched(newSection)).isEqualTo(expected);
    }

    @DisplayName("갈래길이 아닌 경우 상행 종점으로 등록된다.")
    @Test
    void addUpStation() {
        final Station newStation1 = new Station(3L, "광나루역");
        final Station newStation2 = new Station(1L, "아차산역");
        final Section newSection = new Section(newStation1, newStation2, 5, 1L);

        sections.add(newSection);

        final boolean empty = sections.getSections().stream()
                .filter(section -> section.getDownStation().equals(newStation1))
                .findAny()
                .isEmpty();

        assertThat(empty).isTrue();
    }

    @DisplayName("갈래길이 아닌 경우 하행 종점으로 등록된다.")
    @Test
    void addDownStation() {
        final Station newStation1 = new Station(2L, "군자역");
        final Station newStation2 = new Station(3L, "장한평역");
        final Section newSection = new Section(newStation1, newStation2, 5, 1L);

        sections.add(newSection);

        final boolean empty = sections.getSections().stream()
                .filter(section -> section.getUpStation().equals(newStation2))
                .findAny()
                .isEmpty();

        assertThat(empty).isTrue();
    }

    @DisplayName("(이미 존재하는 역이 하행역일 때) 갈래길인 경우 구간을 기존 구간 사이에 등록한다.")
    @Test
    void addBranchedUpSection() {
        final Station newStation1 = new Station(3L, "어린이대공원역");
        final Station newStation2 = new Station(2L, "군자역");
        final Section newSection = new Section(newStation1, newStation2, 5, 1L);

        sections.add(newSection);

        final Optional<Section> updatedSection = sections.getSections().stream()
                .filter(section -> section.getDownStation().equals(newStation1))
                .findAny();

        final Optional<Section> addedSection = sections.getSections().stream()
                .filter(section -> section.getUpStation().equals(newStation1))
                .findAny();

        assert (updatedSection.isPresent() && addedSection.isPresent());

        assertAll(
                () -> assertThat(updatedSection.get().getUpStation()).isEqualTo(station1),
                () -> assertThat(updatedSection.get().getDownStation()).isEqualTo(newStation1),
                () -> assertThat(updatedSection.get().getDistance()).isEqualTo(5),

                () -> assertThat(addedSection.get().getUpStation()).isEqualTo(newStation1),
                () -> assertThat(addedSection.get().getDownStation()).isEqualTo(newStation2),
                () -> assertThat(addedSection.get().getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("(이미 존재하는 역이 상행역일 때) 갈래길인 경우 구간을 기존 구간 사이에 등록한다.")
    @Test
    void addBranchedDownSection() {
        final Station newStation1 = new Station(1L, "아차산역");
        final Station newStation2 = new Station(3L, "어린이대공원역");
        final Section newSection = new Section(newStation1, newStation2, 5, 1L);

        sections.add(newSection);

        final Optional<Section> updatedSection = sections.getSections().stream()
                .filter(section -> section.getUpStation().equals(newStation2))
                .findAny();

        final Optional<Section> addedSection = sections.getSections().stream()
                .filter(section -> section.getDownStation().equals(newStation2))
                .findAny();

        assert (updatedSection.isPresent() && addedSection.isPresent());

        assertAll(
                () -> assertThat(updatedSection.get().getUpStation()).isEqualTo(newStation2),
                () -> assertThat(updatedSection.get().getDownStation()).isEqualTo(station2),
                () -> assertThat(updatedSection.get().getDistance()).isEqualTo(5),

                () -> assertThat(addedSection.get().getUpStation()).isEqualTo(newStation1),
                () -> assertThat(addedSection.get().getDownStation()).isEqualTo(newStation2),
                () -> assertThat(addedSection.get().getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("존재하지 않는 구간에 대해 삭제를 시도할 경우 예외를 발생한다.")
    @Test
    void delete_throwsNotFoundException() {
        final Station newStation1 = new Station(1L, "아차산역");
        final Station newStation2 = new Station(3L, "어린이대공원역");
        final Section newSection = new Section(newStation1, newStation2, 5, 1L);
        final Sections newSections = new Sections(List.of(section, newSection));
        final long stationId = 4L;

        assertThatThrownBy(() -> newSections.delete(stationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간에 존재하지 않는 지하철 역입니다.");
    }

    @DisplayName("구간이 1개일 때 삭제를 시도할 경우 예외를 발생한다.")
    @Test
    void delete_throwsSectionsSizeException() {
        assertThatThrownBy(() -> sections.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 1개 이므로 삭제할 수 없습니다.");
    }
}
