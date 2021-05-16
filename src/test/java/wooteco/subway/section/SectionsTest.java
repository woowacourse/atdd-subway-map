package wooteco.subway.section;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

import java.util.Arrays;
import java.util.Deque;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.fixture.DomainFixtures.*;

@DisplayName("Sections 기능 관련")
public class SectionsTest {

    static Sections sections;

    @BeforeAll
    static void beforeAll() {
        Section section1 = new Section(1L, LINE_ID, STATION_1.getId(), STATION_2.getId(), DEFAULT_SECTION_DISTANCE);
        Section section3 = new Section(3L, LINE_ID, STATION_3.getId(), STATION_4.getId(), DEFAULT_SECTION_DISTANCE);
        Section section2 = new Section(2L, LINE_ID, STATION_2.getId(), STATION_3.getId(), DEFAULT_SECTION_DISTANCE);
        sections = new Sections(Arrays.asList(section1, section3, section2));
    }

    @Test
    @DisplayName("하나의 노선에 있는 역들을 순서대로 정렬 후 id의 Deque로 반환")
    void getSortedStationIds() {
        //when
        Deque expected = sections.getSortedStationIds();

        //then
        assertThat(expected.getFirst()).isEqualTo(STATION_1.getId());
        assertThat(expected.getLast()).isEqualTo(STATION_4.getId());
    }

    @Test
    @DisplayName("이전 구간을 반환")
    void getPreviousSection() {
        //given
        Section newSection = new Section(4L, LINE_ID, STATION_3.getId(), NEW_STATION.getId(), DEFAULT_SECTION_DISTANCE);

        //when
        Section previousSection = sections.getPreviousSection(newSection);

        //then
        assertThat(previousSection.getId()).isEqualTo(3L);
        assertThat(previousSection.getUpStationId()).isEqualTo(STATION_3.getId());
        assertThat(previousSection.getDownStationId()).isEqualTo(STATION_4.getId());
    }

    @Test
    @DisplayName("다음 구간을 반환")
    void getFollowingSection() {
        //given
        Section newSection = new Section(4L, LINE_ID, NEW_STATION.getId(), STATION_2.getId(), DEFAULT_SECTION_DISTANCE);

        //when
        Section followingSection = sections.getFollowingSection(newSection);

        //then
        assertThat(followingSection.getId()).isEqualTo(1L);
        assertThat(followingSection.getUpStationId()).isEqualTo(STATION_1.getId());
        assertThat(followingSection.getDownStationId()).isEqualTo(STATION_2.getId());
    }
}
