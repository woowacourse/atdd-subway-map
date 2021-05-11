package wooteco.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.line.LineDao;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SectionsTest {
    @Autowired
    private StationDao stationDao;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private LineDao lineDao;

    @Test
    @DisplayName("Sections 내 Station 정렬 확인")
    public void sortedStations() {
        lineDao.save("9호선", "황토색");
        stationDao.save("염창역");   //1L
        stationDao.save("가양역");   //2L
        stationDao.save("등촌역");   //3L
        stationDao.save("증미역");   //4L
        Section section1 = sectionDao.save(1L, 2L, 4L, 10);
        Section section2 = sectionDao.save(1L, 4L, 3L, 10);
        Section section3 = sectionDao.save(1L, 3L, 1L, 10);

        List<Section> sectionList = Arrays.asList(section3, section1, section2);

        Sections sections = new Sections(sectionList);
        assertThat(sections.sortedStations())
                .containsExactly(
                        new StationResponse(2L, "가양역"),
                        new StationResponse(4L, "증미역"),
                        new StationResponse(3L, "등촌역"),
                        new StationResponse(1L, "염창역")
                );
    }
}
