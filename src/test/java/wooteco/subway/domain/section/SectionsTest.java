package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;

class SectionsTest {

    private static final Station a = new Station(1L, "a역");
    private static final Station b = new Station(2L,"b역");
    private static final Station c = new Station(3L,"c역");
    private static final Station d = new Station(4L, "d역");
    private static final Station e = new Station(5L, "e역");
    private static final Station f = new Station(6L, "f역");
    private static Long lineId = 0L;

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

    @DisplayName("구간 자동 정렬 - 정렬되지 않은 상태에서 맨 마지막 섹션이 제일 앞에, 맨 앞 섹션이 제일 뒤에 있을 경우 자동 정렬")
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
        assertThat(sections.getValues())
            .containsExactly(
                aToB, bToC, cToD, dToE
            );
    }

    @DisplayName("구간 자동 정렬 - 구간이 하나만 있을 경우 자동 정렬")
    @Test
    void sort2() {
        // given
        Section aToB = new Section(1L, lineId, a, b, 2);

        // when
        List<Section> sectionValues = Arrays.asList(aToB);
        Sections sections = new Sections(sectionValues);

        // then
        assertThat(sections.getValues())
            .containsExactly(aToB);
    }

    @DisplayName("정렬되어 있는대로 상행 종점부터 하행 종점까지 반환할 수 있다.")
    @Test
    void sortStation() {
        // given
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
        assertThat(sections.getStations())
            .containsExactly(a, b, c, d, e, f);
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

    @DisplayName("구간 삭제 - 입력받은 상행 혹은 하행역이 종점인지 확인할 수 있다.")
    @Test
    void remove1() {
        // given
        Section existedSection1 = new Section(1L, lineId, a, b, 2);
        Section existedSection2 = new Section(2L, lineId, b, c, 2);
        Section existedSection3 = new Section(3L, lineId, c, d, 2);

        // when
        Sections sections = new Sections(Arrays.asList(existedSection1, existedSection2, existedSection3));

        // then
        assertThat(sections.canRemoveEndSection(a)).isTrue();
        assertThat(sections.canRemoveEndSection(d)).isTrue();
        assertThat(sections.canRemoveEndSection(b)).isFalse();
        assertThat(sections.canRemoveEndSection(c)).isFalse();
    }

    @DisplayName("구간 삭제 - 삭제하고 싶은 지하철 역을 입력하면 해당 역이 포함된 구간을 반환할 수 있다. (종점역 삭제시 하나, 중간역 삭제시 두개)")
    @Test
    void remove2() {
        // given
        Section existedSection1 = new Section(1L, lineId, a, b, 2);
        Section existedSection2 = new Section(2L, lineId, b, c, 2);
        Section existedSection3 = new Section(3L, lineId, c, d, 2);

        // when
        Sections sections = new Sections(Arrays.asList(existedSection1, existedSection2, existedSection3));

        // then
        assertThat(sections.findUpdateAndRemoveSections(a)).containsExactly(existedSection1);
        assertThat(sections.findUpdateAndRemoveSections(b))
            .containsExactly(existedSection1, existedSection2);
        assertThat(sections.findUpdateAndRemoveSections(d)).containsExactly(existedSection3);
        assertThatThrownBy(() -> sections.findUpdateAndRemoveSections(e))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
