package wooteco.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;

@SuppressWarnings("NonAsciiCharacters")
class LinesTest {

    private final LineInfo LINE1 = new LineInfo(1L, "노선", "색상");
    private final LineInfo LINE2 = new LineInfo(2L, "노선2", "색상");
    private final LineInfo LINE3 = new LineInfo(3L, "노선2", "색상");

    private final Station STATION1 = new Station(1L, "역1");
    private final Station STATION2 = new Station(2L, "역2");
    private final Station STATION3 = new Station(3L, "역3");

    private final Section LINE1_SECTION1 = new Section(STATION1, STATION2, 10);
    private final Section LINE1_SECTION2 = new Section(STATION2, STATION3, 30);
    private final Section LINE2_SECTION = new Section(STATION1, STATION2, 10);
    private final Section LINE3_SECTION = new Section(STATION2, STATION3, 30);

    @Test
    void toSortedList_메서드는_노선의_id_순서대로_정렬된_노선들을_반환() {
        List<LineInfo> lineInfos = List.of(LINE1, LINE2, LINE3);
        List<Section> lineSections = List.of(LINE2_SECTION, LINE1_SECTION1, LINE1_SECTION2, LINE3_SECTION);
        Lines lines = Lines.of(lineInfos, lineSections);

        List<Line> actual = lines.toSortedList();
        List<Line> expected = List.of(
                new Line(LINE1, new Sections(List.of(LINE1_SECTION1, LINE1_SECTION2))),
                new Line(LINE2, new Sections(List.of(LINE2_SECTION))),
                new Line(LINE3, new Sections(List.of(LINE3_SECTION))));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("노선이 전혀 존재하지 않는 경우도 존재하므로, 빈 배열을 받으면 예외 미발생")
    @Test
    void emptyListAllowed() {
        assertThatNoException()
                .isThrownBy(() -> Lines.of(List.of(), List.of()));
    }
}
