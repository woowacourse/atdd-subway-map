package wooteco.subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

@DisplayName("지하철노선")
class LineTest {

    private static final Long LINE_ID = 1L;
    private static final String LINE_NAME = "2호선";
    private static final String LINE_COLOR = "blue";
    private static final Station 강남역 = new Station(1L, "강남역");
    private static final Station 역삼역 = new Station(2L, "역삼역");
    private static final Station 선릉역 = new Station(3L, "선릉역");
    private static final Station 삼성역 = new Station(4L, "삼성역");
    private static final Section FIRST_SECTION = new Section(1L, 강남역, 역삼역, 10);
    private static final Section SECOND_SECTION = new Section(2L, 역삼역, 선릉역, 10);
    private static final Section THIRD_SECTION = new Section(3L, 선릉역, 삼성역, 10);
    private static final List<Section> SECTIONS = List.of(FIRST_SECTION, SECOND_SECTION);

    private Line line;

    @BeforeEach
    void setUp() {
        this.line = new Line(LINE_ID, SECTIONS, LINE_NAME, LINE_COLOR);
    }

    @DisplayName("이름과 색상을 변경한다.")
    @ParameterizedTest
    @CsvSource(value = {"1호선,red", "신분당선,yellow"})
    void update(String name, String color) {
        line.update(name, color);

        assertAll(
                () -> assertThat(line.getName()).isEqualTo(name),
                () -> assertThat(line.getColor()).isEqualTo(color)
        );
    }

    @DisplayName("구간을 추가한다")
    @Test
    void appendSection() {
        int expected = line.getSections().size();
        line.appendSection(THIRD_SECTION);

        List<Section> actual = line.getSections();
        assertThat(actual).hasSize(expected + 1);
    }

    @DisplayName("역을 제거한다")
    @Test
    void removeStation() {
        int expected = line.getSections().size();
        line.removeStation(역삼역);

        List<Section> actual = line.getSections();
        assertThat(actual).hasSize(expected - 1);
    }

    @DisplayName("식별자를 반환한다.")
    @Test
    void getId() {
        Long id = line.getId();
        assertThat(id).isEqualTo(LINE_ID);
    }

    @DisplayName("노선의 지하철구간들을 정렬하여 반환한다.")
    @Test
    void getSections() {
        List<Section> sections = line.getSections();
        assertThat(sections).containsExactly(FIRST_SECTION, SECOND_SECTION);
    }

    @DisplayName("노선의 지하철역들을 정렬하여 반환한다.")
    @Test
    void getStations() {
        List<Station> stations = line.getStations();
        assertThat(stations).containsExactly(강남역, 역삼역, 선릉역);
    }

    @DisplayName("이름을 반환한다.")
    @Test
    void getName() {
        String name = line.getName();
        assertThat(name).isEqualTo(LINE_NAME);
    }

    @DisplayName("색상을 반환한다.")
    @Test
    void getColor() {
        String color = line.getColor();
        assertThat(color).isEqualTo(LINE_COLOR);
    }
}
