package wooteco.subway.domain;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {

    private final Station gangnam = new Station(1L, "강남역");
    private final Station yeoksam = new Station(2L, "역삼역");
    private final Station seolleung = new Station(3L, "선릉역");
    private final Station samsung = new Station(4L, "삼성역");

    private final Section gangnamToYeoksam = new Section(1L, gangnam, yeoksam, 10);
    private final Section yeoksamToseolleung = new Section(2L, yeoksam, seolleung, 10);

    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(gangnamToYeoksam, yeoksamToseolleung);
    }

    @DisplayName("상행 종점부터 하행 종점까지 정렬한 리스트 반환")
    @Test
    void getSortedStations() {

        // when
        final List<Station> sortedStations = sections.getSortedStations();

        // then
        assertThat(sortedStations).containsExactly(gangnam, yeoksam, seolleung);
    }

    @DisplayName("구간 생성 성공 - 빈 구간에 새로운 구간 삽입")
    @Test
    void createSectionWithEmptyLine() {

        // given
        final Sections emptySections = new Sections();

        // when
        final Sections sections = emptySections.insert(gangnamToYeoksam);

        // then
        assertThat(sections.getSortedStations()).containsExactly(gangnam, yeoksam);
        assertThat(sections.getSections()).extracting("distance").containsExactly(10);
    }

    @DisplayName("구간 생성 성공 - 상행 종점역")
    @Test
    void createSectionWithTopStation() {

        // given
        Section newSection = new Section(3L, samsung, gangnam, 10);

        // when
        final Sections sections = this.sections.insert(newSection);

        // then
        assertThat(sections.getSortedStations()).containsExactly(samsung, gangnam, yeoksam, seolleung);
        assertThat(sections.getSections()).extracting("distance").containsExactly(10, 10, 10);
    }

    @DisplayName("구간 생성 성공 - 하행 종점역")
    @Test
    void createSectionWithBottomStation() {

        // given
        Section newSection = new Section(3L, seolleung, samsung, 10);

        // when
        final Sections sections = this.sections.insert(newSection);

        // then
        assertThat(sections.getSortedStations()).containsExactly(gangnam, yeoksam, seolleung, samsung);
        assertThat(sections.getSections()).extracting("distance").containsExactly(10, 10, 10);
    }

    @DisplayName("구간 생성 성공 - 상행 중간역에 추가")
    @Test
    void createSectionWithUpStationOfMiddleStation() {

        // given
        Section newSection = new Section(3L, yeoksam, samsung, 5);

        // when
        final Sections sections = this.sections.insert(newSection);

        // then
        assertThat(sections.getSortedStations()).containsExactly(gangnam, yeoksam, samsung, seolleung);
        assertThat(sections.getSections()).extracting("distance").containsExactly(10, 5, 5);
    }

    @DisplayName("구간 생성 성공 - 하행 중간역에 추가")
    @Test
    void createSectionWithDownStationOfMiddleStation() {

        // given
        Section newSection = new Section(3L, samsung, seolleung, 5);

        // when
        final Sections sections = this.sections.insert(newSection);

        // then
        assertThat(sections.getSortedStations()).containsExactly(gangnam, yeoksam, samsung, seolleung);
        assertThat(sections.getSections()).extracting("distance").containsExactly(10, 5, 5);
    }
}
