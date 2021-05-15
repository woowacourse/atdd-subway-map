package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@Sql("classpath:schema.sql")
class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    @DisplayName("노선 이름, 색을 입력하면 노선을 저장하고 id를 반환한다")
    @Test
    void save() {
        String name = "2호선";
        String color = "red";

        Line line = new Line(name, color);
        assertThat(lineDao.save(line)).isInstanceOf(Long.class);
    }

    @DisplayName("모든 노선을 조회한다")
    @Test
    void findAll() {
        String name = "2호선";
        String color = "green";

        Line line2 = new Line(name, color);
        lineDao.save(line2);

        String name2 = "3호선";
        String color2 = "orange";

        Line line3 = new Line(name2, color2);
        lineDao.save(line3);

        assertThat(lineDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("노선 id를 통해 노선에 포함된 역의 id들을 조회한다")
    @Test
    void findStationsIdByLineId() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("잠실역");
        Station station3 = new Station("신림역");
        long stationId1 = stationDao.save(station1);
        long stationId2 = stationDao.save(station2);
        long stationId3 = stationDao.save(station3);


        Line line = new Line(1L);

        Section section1 = new Section(line, new Station(stationId1), new Station(stationId2));
        Section section2 = new Section(line, new Station(stationId2), new Station(stationId3));
        sectionDao.save(section1);
        sectionDao.save(section2);

        assertTrue(lineDao.findStationsIdByLineId(line.getId())
                .containsAll(Arrays.asList(stationId1, stationId2, stationId3)));
    }

    @DisplayName("id로 노선을 조회한다")
    @Test
    void findById() {
        String name = "2호선";
        String color = "green";
        Line line = new Line(name, color);
        long lineId = lineDao.save(line);

        Line findLine = lineDao.findById(lineId);

        assertThat(findLine.getName()).isEqualTo(name);
        assertThat(findLine.getColor()).isEqualTo(color);
    }

    @DisplayName("노선의 이름과 색상을 수정한다")
    @Test
    void update() {
        String name = "2호선";
        String color = "green";
        long lineId = lineDao.save(new Line(name, color));
        Line line = lineDao.findById(lineId);
        
        String newName = "3호선";
        String newColor = "orange";

        Line updateLine = line.update(newName, newColor);

        assertThat(updateLine.getName()).isEqualTo(newName);
        assertThat(updateLine.getColor()).isEqualTo(newColor);
    }

    @DisplayName("id로 노선을 삭제한다")
    @Test
    void delete() {
        String name = "2호선";
        String color = "green";
        Line line = new Line(name, color);

        lineDao.save(line);

        String name2 = "3호선";
        String color2 = "orange";
        Line line2 = new Line(name2, color2);
        long lineId = lineDao.save(line2);

       Line findLine = lineDao.findById(lineId);

        lineDao.delete(findLine);
        assertThat(lineDao.findAll().size()).isEqualTo(1);
    }
}