package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.AddSectionException;
import wooteco.subway.exception.DeleteSectionException;

class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        final Station firstStation = new Station(1L, "1번역");
        final Station secondStation = new Station(2L, "2번역");
        final Section section = new Section(1L, firstStation, secondStation, 10);
        sections = new Sections(new ArrayList<>(List.of(section)));
    }

    @DisplayName("상행역 부터 하행역으로 정렬된 역 정보들을 가져온다.")
    @Test()
    void getSortedStations() {
        final Station firstStation = new Station(2L, "2번역");
        final Station secondStation = new Station(4L, "4번역");
        final Section section = new Section(1L, firstStation, secondStation, 7);

        sections.addSection(section);

        assertThat(sections.getSortedStations().size()).isEqualTo(3);
    }

    @DisplayName("기존의 구간 사이에 새로운 구간을 추가한다.")
    @Test()
    void addSection() {
        final Station firstStation = new Station(1L, "1번역");
        final Station thirdStation = new Station(3L, "3번역");
        final Section section = new Section(1L, firstStation, thirdStation, 7);

        sections.addSection(section);

        assertThat(sections.getSections().get(0).getUpStation().getName()).isEqualTo("3번역");
        assertThat(sections.getSections().get(0).getDistance()).isEqualTo(3);
    }

    @DisplayName("구간 정보를 합치고 구간 하나를 제거한다.")
    @Test
    void deleteSection() {
        final Station firstStation = new Station(1L, "1번역");
        final Station thirdStation = new Station(3L, "3번역");
        final Section section = new Section(1L, firstStation, thirdStation, 7);

        sections.addSection(section);
        sections.deleteSection(thirdStation);

        assertThat(sections.getSortedStations().size()).isEqualTo(2);
    }

    @DisplayName("가장 높은 상행역을 가진 구간 하나를 제거한다.")
    @Test
    void deleteSection_top() {
        final Station firstStation = new Station(1L, "1번역");
        final Station thirdStation = new Station(3L, "3번역");
        final Section section = new Section(1L, firstStation, thirdStation, 7);

        sections.addSection(section);
        sections.deleteSection(firstStation);

        assertThat(sections.getSortedStations().size()).isEqualTo(2);
    }

    @DisplayName("가장 낮은 하행역을 가진 구간 하나를 제거한다.")
    @Test
    void deleteSection_last() {
        final Station firstStation = new Station(1L, "1번역");
        final Station secondStation = new Station(2L, "2번역");
        final Station thirdStation = new Station(3L, "3번역");
        final Section section = new Section(1L, firstStation, thirdStation, 7);

        sections.addSection(section);
        sections.deleteSection(secondStation);

        assertThat(sections.getSortedStations().size()).isEqualTo(2);
    }

    @DisplayName("추가할 구간과 연결 가능한 역이 없으면 예외를 발생한다.")
    @Test
    void addSection_not_connect_exception() {
        final Station firstStation = new Station(3L, "3번역");
        final Station secondStation = new Station(4L, "4번역");
        final Section section = new Section(1L, firstStation, secondStation, 10);

        assertThatThrownBy(() -> sections.addSection(section))
                .isInstanceOf(AddSectionException.class)
                .hasMessage("연결 할 수 있는 상행역 또는 하행역이 없습니다.");
    }

    @DisplayName("입력한 구간의 상행역과 하행역이 이미 모두 연결되어 있으면 예외를 발생한다.")
    @Test
    void addSection_already_connect_exception() {
        final Station firstStation = new Station(1L, "1번역");
        final Station secondStation = new Station(2L, "2번역");
        final Section section = new Section(1L, firstStation, secondStation, 10);

        assertThatThrownBy(() -> sections.addSection(section))
                .isInstanceOf(AddSectionException.class)
                .hasMessage("입력한 구간의 상행역과 하행역이 이미 모두 연결되어 있습니다.");
    }

    @DisplayName("입력한 구간의 상행역과 하행역의 길이가 기존 구간의 길이와 같거나 더 크면 예외를 발생한다.")
    @Test
    void addSection_longer_than_base_section_exception() {
        final Station firstStation = new Station(1L, "1번역");
        final Station thirdStation = new Station(3L, "3번역");
        final Section section = new Section(1L, firstStation, thirdStation, 10);

        assertThatThrownBy(() -> sections.addSection(section))
                .isInstanceOf(AddSectionException.class)
                .hasMessage("현재 구간의 길이가 추가하려는 구간의 길이보다 작거나 같습니다.");
    }

    @DisplayName("구간이 한개 있을때, 구간을 삭제하려고 하면 예외를 발생한다.")
    @Test
    void deleteSection_not_enough_size_exception() {
        final Station firstStation = new Station(1L, "1번역");
        final Station thirdStation = new Station(3L, "3번역");
        final Section section = new Section(1L, firstStation, thirdStation, 10);

        assertThatThrownBy(() -> sections.deleteSection(firstStation))
                .isInstanceOf(DeleteSectionException.class)
                .hasMessage("현재 구간이 하나 있기때문에, 구간을 제거 할수 없습니다.");
    }
}
