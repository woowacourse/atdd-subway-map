package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @Test
    @DisplayName("상행 종점에 구간을 추가한다.")
    void addUpDestination() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when
        Section newSection = new Section(new Station("선릉"), new Station("역삼"), 5);
        sections.add(newSection);

        //then
        assertThat(sections.getUpDestination()).isEqualTo(new Station("선릉"));
    }

    @Test
    @DisplayName("하행 종점에 구간을 추가한다.")
    void addDownDestination() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when
        Section newSection = new Section(new Station("강남"), new Station("서초"), 6);
        sections.add(newSection);

        //then
        assertThat(sections.getDownDestination()).isEqualTo(new Station("서초"));
    }

    @Test
    @DisplayName("상행 종점 뒤에 새 구간을 추가한다.")
    void addSectionAfterUpDestination() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when
        Section newSection = new Section(new Station("역삼"), new Station("서초"), 3);
        sections.add(newSection);

        //then
        assertAll(
            () -> assertThat(sections.getValues().get(0)).isEqualTo(
                new Section(new Station("역삼"), new Station("서초"), 3)),
            () -> assertThat(sections.getValues().get(1)).isEqualTo(
                new Section(new Station("서초"), new Station("강남"), 2))
        );
    }

    @Test
    @DisplayName("하행 종점 앞에 새 구간을 추가한다.")
    void addSectionBeforeDownDestination() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when
        Section newSection = new Section(new Station("서초"), new Station("강남"), 3);
        sections.add(newSection);

        //then
        assertAll(
            () -> assertThat(sections.getValues().get(0)).isEqualTo(
                new Section(new Station("역삼"), new Station("서초"), 2)),
            () -> assertThat(sections.getValues().get(1)).isEqualTo(
                new Section(new Station("서초"), new Station("강남"), 3))
        );
    }

    @Test
    @DisplayName("추가하려는 구간 길이가 추가할 구간 사이 길이보다 크거나 같으면 예외를 던진다.")
    void addSectionWithOverDistanceException() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when
        Section newSection = new Section(new Station("역삼"), new Station("서초"), 5);

        //then
        assertThatThrownBy(() -> sections.add(newSection))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("거리는 0보다 작을 수 없습니다.");
    }
}
