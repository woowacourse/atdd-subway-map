package wooteco.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.line.LineDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {

    @Test
    @DisplayName("Sections 내 Station 정렬 확인")
    public void sortedStations() {
        Station station1 = new Station(1L, "염창역");
        Station station2 = new Station(2L, "가양역");
        Station station3 = new Station(3L, "등촌역");
        Station station4 = new Station(4L, "증미역");
        Section section1 = new Section(1L, 1L, station2, station4, 10);
        Section section2 = new Section(2L, 1L, station4, station3, 10);
        Section section3 = new Section(3L, 1L, station3, station1, 10);

        List<Section> sectionList = Arrays.asList(section3, section1, section2);

        Sections sections = new Sections(sectionList);
        assertThat(sections.sortedStations())
                .containsExactly(
                        new Station(2L, "가양역"),
                        new Station(4L, "증미역"),
                        new Station(3L, "등촌역"),
                        new Station(1L, "염창역")
                );
    }
}
