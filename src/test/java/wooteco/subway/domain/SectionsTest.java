package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    @DisplayName("구간을 순서에 맞게 정렬하여 역 목록으로 반환한다.")
    void sortStations() {
        Sections sections = new Sections(List.of(
                new Section(1L, 2L, 3L, 10 ),
                new Section(1L, 1L, 2L, 10 )
        ));

        List<Long> actual = sections.sortStations();

        assertThat(actual).isEqualTo(List.of(1L, 2L, 3L));
    }

    @Test
    @DisplayName("해당 역이 상행종점인지 검사한다. - 참 ")
    void isFirstStation_True() {
        Sections sections = new Sections(List.of(
                new Section(1L, 2L, 3L, 10 ),
                new Section(1L, 1L, 2L, 10 )
        ));

        boolean actual = sections.isFirstStation(1L);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("해당 역이 상행종점인지 검사한다. - 거짓")
    void isFirstStation_False() {
        Sections sections = new Sections(List.of(
                new Section(1L, 2L, 3L, 10 ),
                new Section(1L, 1L, 2L, 10 )
        ));

        boolean actual = sections.isFirstStation(2L);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("해당 역이 하행종점인지 검사한다. - 참")
    void isLastStation_True() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 2L, 10 ),
                new Section(1L, 2L, 3L, 10 )
        ));

        boolean actual = sections.isLastStation(3L);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("해당 역이 하행종점인지 검사한다. - 거짓")
    void isLastStation_False() {
        Sections sections = new Sections(List.of(
                new Section(1L, 2L, 3L, 10 ),
                new Section(1L, 1L, 2L, 10 )
        ));

        boolean actual = sections.isLastStation(2L);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("노선 내의 구간이 하나인지 검사한다. - 참")
    void isLessThanOneSection() {
        Sections sections = new Sections(List.of(
                new Section(1L, 2L, 3L, 10 )
        ));

        boolean actual = sections.isOnlyOneSection();

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("노선 내의 구간이 하나인지 검사한다. - 거짓")
    void isLessThanOneSection_False() {
        Sections sections = new Sections(List.of(
                new Section(1L, 2L, 3L, 10 ),
                new Section(1L, 1L, 2L, 10 )
        ));

        boolean actual = sections.isOnlyOneSection();

        assertThat(actual).isFalse();
    }
}
