package wooteco.subway.station;

import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    void addUpSectionTest() {
        Sections sections = new Sections(new Station("남성역"), new Station("내방역"), 10);
        sections.add(new Station("남성역"), new Station("이수역"), 3);

        sections.stream()
                .forEach(System.out::println);
    }

    @Test
    void addDownSectionTest() {
        Sections sections = new Sections(new Station("남성역"), new Station("내방역"), 10);
        sections.add(new Station("이수역"), new Station("내방역"), 7);

        sections.stream()
                .forEach(System.out::println);
    }

    @Test
    void deleteLeftSideStationTest() {
        Sections sections = new Sections(new Station("남성역"), new Station("내방역"), 10);
        sections.add(new Station("이수역"), new Station("내방역"), 7);

        sections.delete(new Station("남성역"));
        sections.stream()
                .forEach(System.out::println);
    }

    @Test
    void deleteRightSideStationTest() {
        Sections sections = new Sections(new Station("남성역"), new Station("내방역"), 10);
        sections.add(new Station("이수역"), new Station("내방역"), 7);

        sections.delete(new Station("내방역"));
        sections.stream()
                .forEach(System.out::println);
    }

    @Test
    void deleteMiddleOfStationTest() {
        Sections sections = new Sections(new Station("남성역"), new Station("내방역"), 10);
        sections.add(new Station("이수역"), new Station("내방역"), 7);

        sections.delete(new Station("이수역"));
        sections.stream()
                .forEach(System.out::println);
    }
}