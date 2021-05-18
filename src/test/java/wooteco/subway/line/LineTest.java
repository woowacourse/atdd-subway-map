package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.domain.Line;

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

        long givenId = 2;
        String givenName = "3호선";
        String givenColor = "분홍색";
        Line line = new Line(givenId, givenName, givenColor);

        // when

        Long id = line.getId();
        String name = line.getName();
        String color = line.getColor();

        // then

        assertThat(id).isEqualTo(givenId);
        assertThat(name).isEqualTo(givenName);
        assertThat(color).isEqualTo(givenColor);
    }

}