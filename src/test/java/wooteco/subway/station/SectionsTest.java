package wooteco.subway.station;

import org.junit.jupiter.api.Test;
import wooteco.subway.line.Line;

class SectionsTest {

    @Test
    void addUpSectionTest() {
        Line line = new Line("7호선", "Green", new Station("남성역"), new Station("내방역"), 10);
        line.getSections().add(new Station("남성역"), new Station("이수역"), 3);

        line.getSections().stream()
                .forEach(System.out::println);
    }

    @Test
    void addDownSectionTest() {
        Line line = new Line("7호선", "Green", new Station("남성역"), new Station("내방역"), 10);
        line.getSections().add(new Station("이수역"), new Station("내방역"), 7);

        line.getSections().stream()
                .forEach(System.out::println);
    }
}