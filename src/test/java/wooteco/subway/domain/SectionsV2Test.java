package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsV2Test {

    @Test
    @DisplayName("한개의 구간을 등록할 수 있다.")
    void add() {
        // given
        List<SectionV2> sectionList = new ArrayList<>();
        sectionList.add(new SectionV2(1L, new Station(1L,"강남역"), new Station(2L, "역삼역"), 5));

        // when
        SectionsV2 sections = new SectionsV2(sectionList);

        // then
        assertThat(sections.getValues()).hasSize(1)
                .extracting(SectionV2::getUpStation, SectionV2::getDownStation)
                .containsAll(
                        List.of(tuple(new Station(1L,"강남역"), new Station(2L, "역삼역")))
                );
    }

    @Test
    @DisplayName("구간이 이미 있을때 구간을 추가할 수 있다.")
    void addFinalUpSection() {
        // given
        SectionsV2 sections = createSections(new SectionV2(1L, new Station(2L, "역삼역"), new Station(3L,"선릉역"), 5));

        // when
        sections.add(new SectionV2(1L, new Station(1L,"강남역"), new Station(2L, "역삼역"), 5));

        // then
        assertThat(sections.getValues()).hasSize(2)
                .extracting(SectionV2::getUpStation, SectionV2::getDownStation)
                .containsAll(
                        List.of(tuple(new Station(1L,"강남역"), new Station(2L, "역삼역")), tuple(new Station(2L, "역삼역"), new Station(3L,"선릉역")))
                );
    }

    @Test
    @DisplayName("구간들의 상행역 혹은 하행역이 추가될 상행역 혹은 하행역에 둘다 존재하는 경우 예외가 발생한다.")
    void invalidOfStationOneExistSection() {
        // given
        SectionsV2 sections = createSections(new SectionV2(1L, new Station(3L,"선릉역"), new Station(4L,"삼성역"), 5));
        sections.add(new SectionV2(1L, new Station(2L, "역삼역"), new Station(3L,"선릉역"), 5));

        // when & then
        assertThatThrownBy(() -> sections.add(new SectionV2(1L, new Station(2L, "역삼역"), new Station(4L,"삼성역"), 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 연결되어 있는 구간입니다.");
    }

    @Test
    @DisplayName("구간들에 추가될 상행역과 하행역 구간이 존재하는 경우 예외가 발생한다.")
    void invalidOfStationDuplicateSection() {
        // given
        SectionsV2 sections = createSections(new SectionV2(1L, new Station(2L, "역삼역"), new Station(3L,"선릉역"), 5));

        // when & then
        assertThatThrownBy(() -> sections.add(new SectionV2(1L, new Station(2L, "역삼역"), new Station(3L,"선릉역"), 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 동일한 구간입니다.");
    }

    @Test
    @DisplayName("구간들 중에 추가될 상행역과 하행역이 존재하지 않는 경우 예외가 발생한다.")
    void invalidOfStationNotExistSection() {
        // given
        SectionsV2 sections = createSections(new SectionV2(1L, new Station(1L,"강남역"), new Station(2L, "역삼역"), 5));

        // when & then
        assertThatThrownBy(() -> sections.add(new SectionV2(1L, new Station(3L,"선릉역"), new Station(4L,"삼성역"), 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간에 존재하지 않는 역입니다.");
    }

    @Test
    @DisplayName("구간이 1개있을때 구간의 중간에 구간을 추가할 수 있다.")
    void updateMiddleSectionOfSectionSizeOne() {
        // given
        SectionsV2 sections = createSections(new SectionV2(1L, new Station(1L,"강남역"), new Station(3L,"선릉역"), 10));

        // when
        sections.add(new SectionV2(1L, new Station(1L,"강남역"),  new Station(2L, "역삼역"), 5));

        // then
        assertThat(sections.getValues()).hasSize(2)
                .extracting(SectionV2::getUpStation, SectionV2::getDownStation, SectionV2::getDistance)
                .containsAll(
                        List.of(tuple(new Station(1L,"강남역"), new Station(2L, "역삼역"), 5), tuple(new Station(2L, "역삼역"), new Station(3L,"선릉역"), 5))
                );
    }

    @Test
    @DisplayName("구간의 2개있을때 중간에 구간을 추가할 수 있다.")
    void updateMiddleSectionOfSectionSizeTwoOrMore() {
        // given
        SectionsV2 sections = createSections(new SectionV2(1L, new Station(1L,"강남역"), new Station(4L,"삼성역"), 15));
        sections.add(new SectionV2(1L, new Station(1L,"강남역"), new Station(2L, "역삼역"), 5));

        // when
        sections.add(new SectionV2(1L, new Station(2L, "역삼역"), new Station(3L,"선릉역"), 5));

        // then
        assertThat(sections.getValues()).hasSize(3)
                .extracting(SectionV2::getUpStation, SectionV2::getDownStation, SectionV2::getDistance)
                .containsAll(
                        List.of(tuple(new Station(1L,"강남역"), new Station(2L, "역삼역"), 5), tuple(new Station(2L, "역삼역"), new Station(3L,"선릉역"), 5), tuple(new Station(3L,"선릉역"), new Station(4L,"삼성역"), 5))
                );
    }

    @Test
    @DisplayName("변경할 구간을 찾을 수 있다.")
    void findUpdateSection() {
        // given
        SectionsV2 newSections = createSections(new SectionV2(1L, new Station(1L,"강남역"), new Station(4L,"삼성역"), 15));
        newSections.add(new SectionV2(1L, new Station(1L,"강남역"), new Station(2L, "역삼역"), 5));

        // when
        final Optional<SectionV2> updateSection = newSections.findUpdate(List.of(new SectionV2(1L, new Station(1L,"강남역"), new Station(4L,"삼성역"), 15)));

        // then
        assertThat(updateSection.get()).isEqualTo(new SectionV2(1L, new Station(1L,"강남역"), new Station(2L, "역삼역"), 5));
    }

    @Test
    @DisplayName("노선의 첫번째 구간을 찾을 수 있다.")
    void findFirstStation() {
        // given
        SectionsV2 sections = createSections(new SectionV2(1L, new Station(1L,"강남역"), new Station(4L,"삼성역"), 15));
        sections.add(new SectionV2(1L, new Station(1L,"강남역"), new Station(2L, "역삼역"), 5));
        sections.add(new SectionV2(1L, new Station(2L, "역삼역"), new Station(3L,"선릉역"), 5));

        // when
        final Station firstStation = sections.findFirstStation();

        // then
        assertThat(firstStation).isEqualTo(new Station(1L,"강남역"));
    }

    @Test
    @DisplayName("노선의 다음역을 찾을 수 있다.")
    void nextStation() {
        // given
        SectionsV2 sections = createSections(new SectionV2(1L, new Station(1L,"강남역"), new Station(4L,"삼성역"), 15));
        sections.add(new SectionV2(1L, new Station(1L,"강남역"), new Station(2L, "역삼역"), 5));
        sections.add(new SectionV2(1L, new Station(2L, "역삼역"), new Station(3L,"선릉역"), 5));

        // when
        final Optional<Station> nextStation = sections.nextStation(new Station(1L,"강남역"));

        // then
        assertThat(nextStation.get()).isEqualTo(new Station(2L, "역삼역"));
    }

    @Test
    @DisplayName("구간을 역 이름을 통해서 상행, 하행 종점을 삭제할 수 있다.")
    void deleteFinalSection() {
        // given
        SectionsV2 sections = createSections(new SectionV2(1L, 1L, new Station(1L,"강남역"), new Station(3L,"선릉역"), 10));
        sections.add(new SectionV2(2L, 1L, new Station(1L,"강남역"), new Station(2L, "역삼역"), 5));

        // when
        sections.delete(new Station(3L,"선릉역"));

        // then
        assertThat(sections.getValues()).hasSize(1)
                .extracting(SectionV2::getUpStation, SectionV2::getDownStation, SectionV2::getDistance)
                .containsAll(
                        List.of(tuple(new Station(1L, "강남역"), new Station(2L,"역삼역"), 5))
                );
    }

    @Test
    @DisplayName("구간을 역 이름을 통해서 구간의 중간역을 삭제할 수 있다.")
    void deleteMiddleSection() {
        // given
        SectionsV2 sections = createSections(new SectionV2(1L,1L, new Station(1L,"강남역"), new Station(3L,"선릉역"), 10));
        sections.add(new SectionV2(2L,1L, new Station(1L,"강남역"), new Station(2L, "역삼역"), 5));

        // when
        sections.delete(new Station(2L, "역삼역"));

        // then
        assertThat(sections.getValues()).hasSize(0);
    }

    @Test
    @DisplayName("구간을 삭제할 때 현재 구간이 1개 등록되어 있는 경우이면 예외가 발생한다.")
    void invalidDeleteSection() {
        // given
        SectionsV2 sections = createSections(new SectionV2(1L,1L, new Station(1L,"강남역"), new Station(3L,"선릉역"), 10));

        // when & then
        assertThatThrownBy(() -> sections.delete(new Station(1L,"강남역")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 1개만 등록되어 있을 경우에는 삭제할 수 없습니다.");
    }

    private SectionsV2 createSections(SectionV2 section) {
        List<SectionV2> sections = new ArrayList<>();
        sections.add(section);

        return new SectionsV2(sections);
    }
}
