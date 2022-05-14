package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static wooteco.subway.Fixtures.강남_삼성;
import static wooteco.subway.Fixtures.강남_선릉;
import static wooteco.subway.Fixtures.강남_역삼;
import static wooteco.subway.Fixtures.강남역;
import static wooteco.subway.Fixtures.삼성역;
import static wooteco.subway.Fixtures.선릉_삼성;
import static wooteco.subway.Fixtures.선릉역;
import static wooteco.subway.Fixtures.역삼_삼성;
import static wooteco.subway.Fixtures.역삼_선릉;
import static wooteco.subway.Fixtures.역삼역;

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
        sectionList.add(강남_역삼);

        // when
        SectionsV2 sections = new SectionsV2(sectionList);

        // then
        assertThat(sections.getValues()).hasSize(1)
                .extracting(SectionV2::getUpStation, SectionV2::getDownStation)
                .containsAll(
                        List.of(tuple(강남역, 역삼역))
                );
    }

    @Test
    @DisplayName("구간이 이미 있을때 구간을 추가할 수 있다.")
    void addFinalUpSection() {
        // given
        SectionsV2 sections = createSections(역삼_선릉);

        // when
        sections.add(강남_역삼);

        // then
        assertThat(sections.getValues()).hasSize(2)
                .extracting(SectionV2::getUpStation, SectionV2::getDownStation)
                .containsAll(
                        List.of(tuple(강남역, 역삼역), tuple(역삼역, 선릉역))
                );
    }

    @Test
    @DisplayName("구간들의 상행역 혹은 하행역이 추가될 상행역 혹은 하행역에 둘다 존재하는 경우 예외가 발생한다.")
    void invalidOfStationOneExistSection() {
        // given
        SectionsV2 sections = createSections(선릉_삼성);
        sections.add(역삼_선릉);

        // when & then
        assertThatThrownBy(() -> sections.add(역삼_삼성))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 연결되어 있는 구간입니다.");
    }

    @Test
    @DisplayName("구간들에 추가될 상행역과 하행역 구간이 존재하는 경우 예외가 발생한다.")
    void invalidOfStationDuplicateSection() {
        // given
        SectionsV2 sections = createSections(역삼_선릉);

        // when & then
        assertThatThrownBy(() -> sections.add(역삼_선릉))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 동일한 구간입니다.");
    }

    @Test
    @DisplayName("구간들 중에 추가될 상행역과 하행역이 존재하지 않는 경우 예외가 발생한다.")
    void invalidOfStationNotExistSection() {
        // given
        SectionsV2 sections = createSections(강남_역삼);

        // when & then
        assertThatThrownBy(() -> sections.add(선릉_삼성))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간에 존재하지 않는 역입니다.");
    }

    @Test
    @DisplayName("구간이 1개있을때 구간의 중간에 구간을 추가할 수 있다.")
    void updateMiddleSectionOfSectionSizeOne() {
        // given
        SectionsV2 sections = createSections(강남_선릉);

        // when
        sections.add(강남_역삼);

        // then
        assertThat(sections.getValues()).hasSize(2)
                .extracting(SectionV2::getUpStation, SectionV2::getDownStation, SectionV2::getDistance)
                .containsAll(
                        List.of(tuple(강남역, 역삼역, 5), tuple(역삼역, 선릉역, 5))
                );
    }

    @Test
    @DisplayName("구간의 2개있을때 중간에 구간을 추가할 수 있다.")
    void updateMiddleSectionOfSectionSizeTwoOrMore() {
        // given
        SectionsV2 sections = createSections(강남_삼성);
        sections.add(강남_역삼);

        // when
        sections.add(역삼_선릉);

        // then
        assertThat(sections.getValues()).hasSize(3)
                .extracting(SectionV2::getUpStation, SectionV2::getDownStation, SectionV2::getDistance)
                .containsAll(
                        List.of(tuple(강남역, 역삼역, 5), tuple(역삼역, 선릉역, 5), tuple(선릉역, 삼성역, 5))
                );
    }

    @Test
    @DisplayName("변경할 구간을 찾을 수 있다.")
    void findUpdateSection() {
        // given
        SectionsV2 newSections = createSections(강남_삼성);
        newSections.add(강남_역삼);

        // when
        final Optional<SectionV2> updateSection = newSections.findUpdate(List.of(강남_삼성));

        // then
        assertThat(updateSection.get()).isEqualTo(강남_역삼);
    }

    private SectionsV2 createSections(SectionV2 section) {
        List<SectionV2> sections = new ArrayList<>();
        sections.add(section);

        return new SectionsV2(sections);
    }
}
