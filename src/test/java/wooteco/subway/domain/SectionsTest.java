package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.TestFixtures.동묘앞역;
import static wooteco.subway.TestFixtures.보문역;
import static wooteco.subway.TestFixtures.신당역;
import static wooteco.subway.TestFixtures.창신역;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.exception.SectionCreateException;
import wooteco.subway.utils.exception.SectionDeleteException;

public class SectionsTest {

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        Sections sections = createInitialSections(신당역, 동묘앞역);
        sections.add(createSection(2L, 동묘앞역, 창신역, 1));
        sections.add(createSection(3L, 창신역, 보문역, 1));

        assertThat(sections.getValues()).hasSize(3);
    }

    @DisplayName("이미 존재하는 구간 등록시 예외를 발생한다.")
    @Test
    void duplicateSectionException() {
        Sections sections = createInitialSections(신당역, 동묘앞역);
        assertThatThrownBy(() -> sections.add(createSection(2L, 신당역, 동묘앞역, 1)))
                .isInstanceOf(SectionCreateException.class)
                .hasMessageContaining("이미 존재하는 구간입니다.");
    }

    @DisplayName("상행 역 혹은 하행역이 상행, 혹은 하행에 존재하지 않으면 예외를 발생한다.")
    @Test
    void stationNotExistException() {
        Sections sections = createInitialSections(신당역, 동묘앞역);
        assertThatThrownBy(() -> sections.add(createSection(2L, new Station(5L, "안암역"), 보문역, 1)))
                .isInstanceOf(SectionCreateException.class)
                .hasMessageContaining("구간이 연결되지 않습니다");
    }

    @DisplayName("이미 연결된 역이 등록될 시 예외를 발생한다.")
    @Test
    void sectionAlreadyExistException() {
        Sections sections = createInitialSections(신당역, 동묘앞역);
        sections.add(createSection(2L, 동묘앞역, 창신역, 2));

        assertThatThrownBy(() -> sections.add(createSection(3L, 신당역, 창신역, 1)))
                .isInstanceOf(SectionCreateException.class)
                .hasMessageContaining("이미 존재하는 구간입니다.");
    }

    @DisplayName("구간이 존재하면 사이에 역을 등록한다.")
    @Test
    void cutIntSection() {
        Sections sections = createInitialSections(신당역, 창신역);
        sections.add(createSection(2L, 동묘앞역, 창신역, 2));

        assertAll(
                () -> assertThat(sections.getValues()).contains(createSection(1L, 신당역, 동묘앞역, 3)),
                () -> assertThat(sections.getValues()).contains(createSection(2L, 신당역, 동묘앞역, 2))
        );
    }

    @DisplayName("사이 거리보다 길이가 길면 역을 등록할 수 없다.")
    @Test
    void cutInException() {
        Sections sections = createInitialSections(신당역, 창신역);
        assertThatThrownBy(() -> sections.add(createSection(2L, 동묘앞역, 창신역, 6)))
                .isInstanceOf(SectionCreateException.class)
                .hasMessageContaining("기존의 구간보다 긴 구간은 넣을 수 없습니다.");
    }

    @DisplayName("업데이트한 객체를 찾는다.")
    @Test
    void pickUpdate() {
        Sections sections = new Sections(getSections());
        sections.add(new Section(2L, 1L, 동묘앞역, 창신역, 2));

        List<Section> foundSections = getSections();
        Section section = sections.pickUpdate(foundSections).get();
        assertAll(
                () -> assertThat(section.getUpStation().getName()).isEqualTo("신당역"),
                () -> assertThat(section.getDownStation().getName()).isEqualTo("동묘앞역")
        );
    }

    @DisplayName("업데이트한 객체가 없을 경우 Optional empty를 반환한다")
    @Test
    void pickUpdateEmpty() {
        Sections sections = new Sections(getSections());
        sections.add(new Section(2L, 1L, 창신역, 보문역, 2));

        List<Section> foundSections = getSections();
        Optional<Section> section = sections.pickUpdate(foundSections);
        assertThat(section.isEmpty()).isTrue();
    }

    @DisplayName("Station을 받으면 구간을 삭제한다.")
    @Test
    void deleteSection() {
        List<Section> rawSections = new ArrayList<>();
        rawSections.add(new Section(1L, 1L, 신당역, 동묘앞역, 5));
        rawSections.add(new Section(2L, 1L, 동묘앞역, 창신역, 3));
        Sections sections = new Sections(rawSections);

        List<Section> deletedSections = sections.delete(동묘앞역);
        assertThat(deletedSections).hasSize(2);
    }

    @DisplayName("구간이 하나일 경우 삭제할 수 없다.")
    @Test
    void noDeleteSectionOnlyOne() {
        List<Section> rawSections = new ArrayList<>();
        rawSections.add(new Section(1L, 1L, 신당역, 동묘앞역, 5));
        Sections sections = new Sections(rawSections);

        assertThatThrownBy(() -> sections.delete(신당역))
                .isInstanceOf(SectionDeleteException.class);
    }

    @DisplayName("역을 순서대로 정렬한다.")
    @Test
    void findNextStation() {
        Sections sections = createInitialSections(신당역, 창신역);
        sections.add(createSection(2L, 동묘앞역, 창신역, 2));

        assertThat(sections.sortSections()).containsExactly(new StationResponse(신당역),
                new StationResponse(동묘앞역),
                new StationResponse(창신역));
    }

    private List<Section> getSections() {
        List<Section> initialSections = new ArrayList<>();
        initialSections.add(createSection(1L, 신당역, 창신역, 5));
        return initialSections;
    }

    private Section createSection(Long id, Station upStation, Station downStation, int distance) {
        return new Section(id, 1L, upStation, downStation, distance);
    }

    private Sections createInitialSections(Station upStation, Station downStation) {
        List<Section> initialSections = new ArrayList<>();
        initialSections.add(createSection(1L, upStation, downStation, 5));
        return new Sections(initialSections);
    }
}
