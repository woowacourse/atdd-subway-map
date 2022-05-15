package wooteco.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.section.RegisteredSection;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
class LineTest {

    private final LineInfo LINE1 = new LineInfo(1L, "노선", "색상");
    private final LineInfo LINE2 = new LineInfo(2L, "노선2", "색상");
    private final Station STATION1 = new Station(1L, "역1");
    private final Station STATION2 = new Station(2L, "역2");
    private final Station STATION3 = new Station(3L, "역3");
    private final Station STATION4 = new Station(4L, "역4");

    @Test
    void getSortedStations_메서드는_노선에_등록된_지하철역들을_상행종점부터_하행종점까지_정렬된_상태로_반환() {
        List<RegisteredSection> registeredSections = List.of(
                new RegisteredSection(LINE1, new Section(STATION3, STATION4, 20)),
                new RegisteredSection(LINE1, new Section(STATION1, STATION2, 10)),
                new RegisteredSection(LINE1, new Section(STATION2, STATION3, 30)));

        List<Station> actual = Line.of(registeredSections).getSortedStations();
        List<Station> expected = List.of(STATION1, STATION2, STATION3, STATION4);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void of_메서드는_빈_배열을_받으면_예외발생() {
        assertThatThrownBy(() -> Line.of(List.of()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void of_메서드는_서로_다른_노선에_등록된_구간들을_받으면_예외발생() {
        List<RegisteredSection> sectionsRegisteredAtMultipleLines = List.of(
                new RegisteredSection(LINE2, new Section(STATION3, STATION4, 20)),
                new RegisteredSection(LINE1, new Section(STATION1, STATION2, 10)),
                new RegisteredSection(LINE1, new Section(STATION2, STATION3, 30)));

        assertThatThrownBy(() -> Line.of(sectionsRegisteredAtMultipleLines))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
