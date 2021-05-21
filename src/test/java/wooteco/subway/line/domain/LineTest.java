package wooteco.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.section.Distance;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.domain.section.Sections;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @DisplayName("이름이 같으면 true를 반환한다.")
    @Test
    void isSameName() {
        //given
        String name = "1호선";
        Line line = new Line(name, "빨간색");

        //when
        boolean result = line.isSameName(name);

        assertThat(result).isTrue();
    }

    @DisplayName("이름이 다르면 false를 반환한다.")
    @Test
    void isDifferentName() {
        //given
        String name = "1호선";
        Line line = new Line(name, "빨간색");
        String differentName = "2호선";

        //when
        boolean result = line.isSameName(differentName);

        assertThat(result).isFalse();
    }

    @DisplayName("ID가 같으면 true를 반환한다.")
    @Test
    void isSameId() {
        //given
        Long id = 1L;
        Line line = new Line(id, "1호선", "빨간색");

        //when
        boolean result = line.isSameId(id);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("ID가 같으면 false를 반환한다.")
    @Test
    void isDifferentId() {
        //given
        Line line = new Line(1L, "1호선", "빨간색");
        Long differentId = 2L;

        //when
        boolean result = line.isSameId(differentId);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("구간 추가할 수 있다.")
    @Test
    void addSection() {
        //given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 2L, new Distance(10)));
        Line line = new Line(1L, "1호선", "빨간색", new Sections(sectionList));
        Section section = new Section(1L, 2L, 3L, new Distance(10));

        //when
        line.addSection(section);

        //then
        assertThat(line.getSections().toList()).contains(section);
    }

    @Test
    void update() {
        //given
        Line line = new Line("2호선", "빨간색");
        String updateName = "3호선";
        String updateColor = "파란색";

        //when
        Line updatedLine = line.update(updateName, updateColor);

        //then
        assertThat(updatedLine.getName()).isEqualTo(updateName);
        assertThat(updatedLine.getColor()).isEqualTo(updateColor);
    }

    @Test
    void sortingSectionIds() {
        //given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 2L, new Distance(10)));
        sectionList.add(new Section(1L, 3L, 1L, new Distance(10)));
        Line line = new Line(1L, "1호선", "빨간색", new Sections(sectionList));

        //when
        List<Long> ids = line.sortingSectionIds();

        //then
        assertThat(ids).containsExactly(3L, 1L, 2L);
    }
}