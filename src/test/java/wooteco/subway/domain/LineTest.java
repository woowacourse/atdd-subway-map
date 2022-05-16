package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.domain.LineException;
import wooteco.subway.exception.domain.SectionException;

class LineTest {

    private Line line;

    private Station one;
    private Station two;
    private Station three;
    private Station four;

    @BeforeEach
    void setUp() {
        one = new Station(1L, "one");
        two = new Station(2L, "two");
        three = new Station(3L, "three");
        four = new Station(4L, "four");
        Section section1 = new Section(1L, four, two, 10);
        Section section2 = new Section(1L, two, three, 10);
        Section section3 = new Section(1L, three, one, 10);
        line = new Line("1호선", "red", List.of(section1, section2, section3));
    }

    @ParameterizedTest
    @DisplayName("노선 이름이 공백이면 예외가 발생한다")
    @ValueSource(strings = {"", " ", "    "})
    void newLine_blankName(String name) {
        assertThatThrownBy(() -> new Line(name, "bg-red-600", new ArrayList<>()))
                .isInstanceOf(LineException.class)
                .hasMessage("노선의 이름이 공백이 되어서는 안됩니다.");
    }

    @Test
    @DisplayName("노선 객체 생성에 성공한다.")
    void newLine() {
        // when
        Line line = new Line("7호선", "bg-red-600", new ArrayList<>());

        // then
        assertThat(line).isNotNull();
    }

    @Test
    @DisplayName("구간들에서 역 id 찾기")
    void findStationIds() {
        // when
        List<Long> ids = line.getSortedStations().stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        // then
        assertThat(ids).containsExactly(4L, 2L, 3L, 1L);
    }

    @Test
    @DisplayName("구간들에서 특정역에 따라 구간 삭제")
    void findNearByStationId() {
        // when
        line.deleteSectionNearBy(two);

        // then
        assertThat(line.getSections()).hasSize(2);
    }

    @Test
    @DisplayName("구간이 하나일 때 특정역에 따라 삭제할 구간 찾으려 하면 예외")
    void findNearByStationId_invalid() {
        // when
        Line onlyOneLine = new Line("onlyOne", "red", List.of(new Section(1L, one, two, 10)));

        // then
        assertThatThrownBy(() -> onlyOneLine.deleteSectionNearBy(one))
                .isInstanceOf(SectionException.class)
                .hasMessage(ExceptionMessage.SECTIONS_NOT_DELETABLE.getContent());
    }

    @Test
    @DisplayName("인접한 구간이 2개이면 구간을 합쳐서 반환")
    void mergeSections() {
        // when
        line.deleteSectionNearBy(three);

        // then
        assertThat(line.getSections()).hasSize(2);
    }

    @Test
    @DisplayName("삭제될 구간이 2개이면 그 구간만 제거")
    void deleteSections() {
        // when
        line.deleteSectionNearBy(two);

        // then
        assertThat(line.getSections()).hasSize(2);
    }
}
