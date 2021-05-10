package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;

class SectionsTest {

    private static Long lineId = 0L;
    private static Station a;
    private static Station b;
    private static Station c;
    private static Station d;
    private static Station e;
    private static Station f;

    @BeforeAll
    static void beforeAll() {
        a = new Station("a역");
        b = new Station("b역");
        c = new Station("c역");
        d = new Station("d역");
        e = new Station("e역");
        f = new Station("f역");
    }

    @DisplayName("빈 구간 리스트를 생성할 수 없습니다.")
    @Test
    void empty() {
        assertAll(
            () -> assertThatThrownBy(() -> new Sections(Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> new Sections(null))
                .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("정렬되어 있지 않은 여러 구간을 통해 생성시 상행 종점부터 하행 종점까지 자동 정렬합니다. - 1")
    @Test
    void sort1() {
        // given
        Section aToB = new Section(1L, lineId, a, b, 2);
        Section bToC = new Section(2L, lineId, b, c, 2);
        Section cToD = new Section(3L, lineId, c, d, 2);
        Section dToE = new Section(4L, lineId, d, e, 2);

        // when
        List<Section> sectionValues = Arrays.asList(dToE, bToC, cToD, aToB);
        Sections sections = new Sections(sectionValues);

        // then
//        sections.getValues().forEach(section -> System.out.print(section.getUpStation().getName() + " - " + section.getDownStation().getName() + ", "));
        assertThat(sections.getValues())
            .containsExactly(
                aToB, bToC, cToD, dToE
            );
    }

    @DisplayName("정렬되어 있지 않은 여러 구간을 통해 생성시 상행 종점부터 하행 종점까지 자동 정렬합니다. - 2")
    @Test
    void sort2() {
        // given
        Section aToB = new Section(1L, lineId, a, b, 2);
        Section bToC = new Section(2L, lineId, b, c, 2);
        Section cToD = new Section(3L, lineId, c, d, 2);
        Section dToE = new Section(4L, lineId, d, e, 2);
        Section etoF = new Section(5L, lineId, e, f, 2);

        // when
        List<Section> sectionValues = Arrays.asList(cToD, etoF, bToC, dToE, aToB);
        Sections sections = new Sections(sectionValues);

        // then
        assertThat(sections.getValues())
            .containsExactly(
                aToB, bToC, cToD, dToE, etoF
            );
    }

    @DisplayName("정렬되어 있지 않은 여러 구간을 통해 생성시 상행 종점부터 하행 종점까지 자동 정렬합니다. - 3")
    @Test
    void sort3() {
        // given
        Section aToB = new Section(1L, lineId, a, b, 2);

        // when
        List<Section> sectionValues = Arrays.asList(aToB);
        Sections sections = new Sections(sectionValues);

        // then
        assertThat(sections.getValues())
            .containsExactly(aToB);
    }

    @DisplayName("구간 추가 - 입력받은 상행 혹은 하행역이 종점인지 확인할 수 있다.")
    @Test
    void add1() {
        // given
        Section existedSection1 = new Section(1L, lineId, a, b, 2);
        Section existedSection2 = new Section(2L, lineId, b, c, 2);
        Section addToTopRequest = new Section(3L, lineId, d, a, 2);
        Section addToBottomRequest = new Section(4L, lineId, c, e, 2);
        Section addToBetweenRequest = new Section(5L, lineId, b, f, 1);

        // when
        Sections sections = new Sections(Arrays.asList(existedSection1, existedSection2));

        // then
        assertAll(
            () -> assertThat(sections.canAddToEndSection(addToTopRequest)).isTrue(),
            () -> assertThat(sections.canAddToEndSection(addToBottomRequest)).isTrue(),
            () -> assertThat(sections.canAddToEndSection(addToBetweenRequest)).isFalse()
        );
    }

    @DisplayName("구간 추가 - 구간 사이의 중간 삽입이 되고 수정이 필요한 구간을 반환할 수 있다. (상행을 이용한 추가)")
    @Test
    void add2() {
        // given
        Section existedSection1 = new Section(1L, lineId, a, b, 2);
        Section existedSection2 = new Section(2L, lineId, b, c, 2);
        Section addToBetweenRequest1 = new Section(3L, lineId, b, d, 1);

        // when
        Sections sections = new Sections(Arrays.asList(existedSection1, existedSection2));
        Section updateSection = sections.addToBetweenExistedSection(addToBetweenRequest1);

        // then
        assertAll(
            () -> assertThat(updateSection.getUpStation()).isEqualTo(d),
            () -> assertThat(updateSection.getDownStation()).isEqualTo(c)
        );
    }

    @DisplayName("구간 추가 - 구간 사이의 중간 삽입이 되고 수정이 필요한 구간을 반환할 수 있다. (하행을 이용한 추가)")
    @Test
    void add3() {
        // given
        Section existedSection1 = new Section(1L, lineId, a, b, 2);
        Section existedSection2 = new Section(2L, lineId, b, c, 2);
        Section addToBetweenRequest1 = new Section(3L, lineId, d, b, 1);

        // when
        Sections sections = new Sections(Arrays.asList(existedSection1, existedSection2));
        Section updateSection = sections.addToBetweenExistedSection(addToBetweenRequest1);

        // then
        assertAll(
            () -> assertThat(updateSection.getUpStation()).isEqualTo(a),
            () -> assertThat(updateSection.getDownStation()).isEqualTo(d)
        );
    }

    @DisplayName("구간 추가 - 역 사이에 새로운 역을 등록할 경우 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void add4() {
        // given
        Section existedSection1 = new Section(1L, lineId, a, b, 2);
        Section existedSection2 = new Section(2L, lineId, b, c, 2);
        Section addToBetweenRequest1 = new Section(3L, lineId, d, b, 3);
        Section addToBetweenRequest2 = new Section(4L, lineId, d, b, 2);
        Section addToBetweenRequest3 = new Section(5L, lineId, d, b, 1);

        // when
        Sections sections = new Sections(Arrays.asList(existedSection1, existedSection2));

        // then
        assertThatThrownBy(() -> sections.addToBetweenExistedSection(addToBetweenRequest1))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> sections.addToBetweenExistedSection(addToBetweenRequest2))
            .isInstanceOf(IllegalArgumentException.class);
        assertDoesNotThrow(() -> sections.addToBetweenExistedSection(addToBetweenRequest3));
    }

    @DisplayName("구간 추가 - 상행역과 하행역이 모두 이미 해당 노선에 등록되어 있는 경우 추가할 수 없다.")
    @Test
    void add5() {
        // given
        Section existedSection1 = new Section(1L, lineId, a, b, 2);
        Section existedSection2 = new Section(2L, lineId, b, c, 2);
        Section addRequest = new Section(3L, lineId, a, c, 2);

        // when
        Sections sections = new Sections(Arrays.asList(existedSection1, existedSection2));

        // then
        assertThatThrownBy(() -> sections.addToBetweenExistedSection(addRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 추가 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void add6() {
        // given
        Section existedSection1 = new Section(1L, lineId, a, b, 2);
        Section existedSection2 = new Section(2L, lineId, b, c, 2);
        Section addRequest = new Section(3L, lineId, d, e, 2);

        // when
        Sections sections = new Sections(Arrays.asList(existedSection1, existedSection2));

        // then
        assertThatThrownBy(() -> sections.addToBetweenExistedSection(addRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
