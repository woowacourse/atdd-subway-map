package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Nested
public class SectionsTest {

    @Test
    @DisplayName("구간들에서 역들을 추출한다.")
    void toStationIds() {
        final Sections sections = new Sections(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 3L, 4L, 10),
                new Section(1L, 2L, 3L, 10)));

        final List<Long> stationIds = sections.toSortedStationIds();

        assertThat(stationIds).containsExactly(1L, 2L, 3L, 4L);
    }

    @Nested
    @DisplayName("지하철 구간을 추가한다.")
    class AddSectionTest {

        @Test
        @DisplayName("1-2 구간이 있을 때, 2-3 구간을 추가한다.")
        void addSection1() {
            // given
            final Section section = new Section(1L, 1L, 2L, 10);
            final Sections sections = new Sections(List.of(section));
            final Section newSection = new Section(1L, 2L, 3L, 10);

            // when
            sections.add(newSection);

            // then
            assertThat(sections.getSections()).containsOnly(section, newSection);
        }

        @Test
        @DisplayName("2-3 구간이 있을 때, 1-2 구간을 추가한다.")
        void addSection2() {
            // given
            final Section section = new Section(1L, 2L, 3L, 10);
            final Sections sections = new Sections(List.of(section));
            final Section newSection = new Section(1L, 1L, 2L, 10);

            // when
            sections.add(newSection);

            // then
            assertThat(sections.getSections()).containsOnly(section, newSection);
        }

        @Test
        @DisplayName("1-3 구간이 있을 때, 1-2 구간을 추가한다. 1-3 구간이 삭제되고, 1-2, 1-3 구간이 추가된다.")
        void addSection3() {
            // given
            final Section section = new Section(1L, 1L, 3L, 10);
            final Sections sections = new Sections(List.of(section));
            final Section newSection1 = new Section(1L, 1L, 2L, 5);
            final Section newSection2 = new Section(1L, 2L, 3L, 5);

            // when
            sections.add(newSection1);

            // then
            assertThat(sections.getSections()).containsOnly(newSection1, newSection2);
        }

        @Test
        @DisplayName("1-3 구간이 있을 때, 2-3 구간을 추가한다. 1-3 구간이 삭제되고, 1-2, 1-3 구간이 추가된다.")
        void addSection4() {
            // given
            final Section section = new Section(1L, 1L, 3L, 10);
            final Sections sections = new Sections(List.of(section));
            final Section newSection1 = new Section(1L, 1L, 2L, 5);
            final Section newSection2 = new Section(1L, 2L, 3L, 5);

            // when
            sections.add(newSection2);

            // then
            assertThat(sections.getSections()).containsOnly(newSection1, newSection2);
        }

        @Test
        @DisplayName("1-3 구간이 있을 때, 2-3 구간을 추가한다. 이때 2-3 구간의 거리가 1-3 구간의 거리보다 같거나 큰 경우, 예외를 발생키신다.")
        void exceptionAddLongSection() {
            // given
            final Section section = new Section(1L, 1L, 3L, 10);
            final Sections sections = new Sections(List.of(section));
            final Section newSection = new Section(1L, 2L, 3L, 10);

            // when & then
            assertThatThrownBy(() -> sections.add(newSection))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("구간의 길이가 너무 길어 추가할 수 없습니다.");
        }

        @Test
        @DisplayName("추가하는 구간이 이미 존재하는 경우, 예외를 발생시킨다.")
        void exceptionAddDifferentLineIdSection() {
            // given
            final Section section = new Section(1L, 1L, 3L, 10);
            final Sections sections = new Sections(List.of(section));
            final Section newSection1 = new Section(1L, 1L, 2L, 5);
            final Section newSection2 = new Section(1L, 2L, 3L, 5);
            sections.add(newSection1);

            // when & then
            assertThatThrownBy(() -> sections.add(newSection2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 있는 구간은 추가할 수 없습니다.");

        }
    }
}
