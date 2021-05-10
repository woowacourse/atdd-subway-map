package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("이름으로 역 생성")
    void createStationWithName() {
        // given
        String name = "2호선";
        String color = "초록색";

        // when
        Line line = new Line(name, color);

        // then
        assertThat(line).isInstanceOf(Line.class);
        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("아이디와 이름으로 역 생성")
    void createStationWithIdAndName() {
        // given
        long id = 1;
        String name = "2호선";
        String color = "초록색";

        // when
        Line line = new Line(id, name, color);

        // then
        assertThat(line).isInstanceOf(Line.class);
        assertThat(line.getId()).isEqualTo(id);
        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("아이디, 이름 가져오기")
    void getName() {
        // given
        Line line1 = new Line();

        String givenName2 = "2호선";
        String givenColor2 = "초록색";
        Line line2 = new Line(givenName2, givenColor2);

        long givenId3 = 2;
        String givenName3 = "3호선";
        String givenColor3 = "분홍색";
        Line line3 = new Line(givenId3, givenName3, givenColor3);

        // when
        Long id1 = line1.getId();
        String name1 = line1.getName();
        String color1 = line1.getColor();

        Long id2 = line2.getId();
        String name2 = line2.getName();
        String color2 = line2.getColor();

        Long id3 = line3.getId();
        String name3 = line3.getName();
        String color3 = line3.getColor();

        // then
        assertThat(id1).isNull();
        assertThat(name1).isNull();
        assertThat(color1).isNull();

        assertThat(id2).isNull();
        assertThat(name2).isEqualTo(givenName2);
        assertThat(color2).isEqualTo(givenColor2);

        assertThat(id3).isEqualTo(givenId3);
        assertThat(name3).isEqualTo(givenName3);
        assertThat(color3).isEqualTo(givenColor3);
    }

}