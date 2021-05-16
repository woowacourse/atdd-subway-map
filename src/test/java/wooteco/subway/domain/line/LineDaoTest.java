package wooteco.subway.domain.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

@SpringBootTest
@Transactional
@Sql("classpath:test-schema.sql")
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
        Line line = addLine("2호선", "green");
        assertThat(lineDao.save(line)).isInstanceOf(Long.class);
    }

    @DisplayName("모든 노선을 조회한다")
    @Test
    void findAll() {
        Line line = addLine("2호선", "green");
        lineDao.save(line);

        Line line2 = addLine("3호선", "orange");
        lineDao.save(line2);

        assertThat(lineDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("노선 id를 통해 노선에 포함된 역의 id들을 조회한다")
    @Test
    void findStationsIdByLineId() {
        long stationId1 = stationDao.save(new Station("강남역"));
        long stationId2 = stationDao.save(new Station("잠실역"));
        long stationId3 = stationDao.save(new Station("신림역"));

        Line line = addLine("2호선", "green");
        long lineId = lineDao.save(line);

        Section section = new Section(lineId, stationId1, stationId2, 1);
        Section section2 = new Section(lineId, stationId2, stationId3, 2);

        sectionDao.save(section);
        sectionDao.save(section2);

        assertTrue(lineDao.findStationsIdByLineId(lineId)
            .containsAll(Arrays.asList(stationId1, stationId2, stationId3)));
    }

    @DisplayName("id로 노선을 조회한다")
    @Test
    void findById() {
        String name = "2호선";
        String color = "green";
        Line newLine = new Line(name, color);
        long lineId = lineDao.save(newLine);

        Line line = lineDao.findById(lineId).get();

        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @DisplayName("노선의 이름과 색상을 수정한다")
    @Test
    void update() {
        Line newLine = addLine("2호선", "green");
        long lineId = lineDao.save(newLine);

        String newName = "3호선";
        String newColor = "orange";
        Line updatedLine = addLine(newName, newColor);
        assertEquals(1, lineDao.update(lineId, updatedLine));

        Line line = lineDao.findById(lineId).get();
        assertThat(line.getName()).isEqualTo(newName);
        assertThat(line.getColor()).isEqualTo(newColor);
    }

    @DisplayName("존재하지 않는 노선을 수정한다")
    @Test
    void updateException() {
        Line updatedLine = addLine("3호선", "orange");

        assertEquals(0, lineDao.update(10, updatedLine));
    }

    @DisplayName("id로 노선을 삭제한다")
    @Test
    void delete() {
        Line newLine = addLine("2호선", "green");
        lineDao.save(newLine);

        Line newLine2 = addLine("3호선", "orange");
        long lineId = lineDao.save(newLine2);

        assertEquals(1, lineDao.delete(lineId));
        assertThat(lineDao.findAll().size()).isEqualTo(1);
    }

    @DisplayName("존재하지 않는 id로 노선을 삭제한다")
    @Test
    void deleteException() {
        Line newLine = addLine("2호선", "green");
        lineDao.save(newLine);

        assertEquals(0, lineDao.delete(10));
    }

    private Line addLine(String lineName, String color) {
        return new Line(lineName, color);
    }
}