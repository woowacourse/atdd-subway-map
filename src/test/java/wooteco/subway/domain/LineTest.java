package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class LineTest {

    @Test
    public void isSameName() {
        final String stationName = "1호선";
        final Line line1 = Line.create(stationName, "red");
        final Line line2 = Line.create(stationName, "blue");

        assertThat(line1.isSameName(line2.getName())).isTrue();
    }

    @Test
    public void isSameId() {
        final Line line1 = Line.create(1L, "1호선", "red");
        final Line line2 = Line.create(1L, "1호선", "red");

        assertThat(line1.isSameId(line2.getId())).isTrue();
    }

    @Test
    public void changeInfo() {
        String expectedName = "2호선";
        String expectedColor = "blue";

        final Line line = Line.create("1호선", "red");
        line.changeInfo(expectedName, expectedColor);

        assertThat(line.getColor()).isEqualTo(expectedColor);
        assertThat(line.getName()).isEqualTo(expectedName);
    }

    @Test
    public void isNotSameId() {
        final Line line1 = Line.create(1L, "1호선", "red");
        final Line line2 = Line.create(2L, "1호선", "red");

        assertThat(line1.isNotSameId(line2.getId())).isTrue();
    }

    @Test
    public void isSameColor() {
        final String stationColor = "red";
        final Line line1 = Line.create("1호선", stationColor);
        final Line line2 = Line.create("2호선", stationColor);

        assertThat(line1.isSameColor(line2.getColor())).isTrue();
    }
}
