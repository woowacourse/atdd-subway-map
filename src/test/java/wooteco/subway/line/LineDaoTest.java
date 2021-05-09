package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.SectionDao;
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
        assertThat(lineDao.save(name, color)).isInstanceOf(Long.class);
    }

    @DisplayName("모든 노선을 조회한다")
    @Test
    void findAll() {
        String name = "2호선";
        String color = "green";
        lineDao.save(name, color);

        String name2 = "3호선";
        String color2 = "orange";
        lineDao.save(name2, color2);

        assertThat(lineDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("노선 id를 통해 노선에 포함된 역의 id들을 조회한다")
    @Test
    void findStationsIdByLineId() {
        String station1 = "강남역";
        String station2 = "잠실역";
        String station3 = "신림역";
        long stationId1 = stationDao.save(station1);
        long stationId2 = stationDao.save(station2);
        long stationId3 = stationDao.save(station3);

        String name = "2호선";
        String color = "green";
        long lineId = lineDao.save(name, color);
        sectionDao.save(lineId, stationId1, stationId2);
        sectionDao.save(lineId, stationId2, stationId3);

        assertTrue(lineDao.findStationsIdByLineId(lineId).containsAll(Arrays.asList(stationId1, stationId2, stationId3)));
    }

    @DisplayName("id로 노선을 조회한다")
    @Test
    void findById() {
        String name = "2호선";
        String color = "green";
        long lineId = lineDao.save(name, color);

        final Line line = lineDao.findById(lineId);

        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @DisplayName("노선의 이름과 색상을 수정한다")
    @Test
    void update() {
        String name = "2호선";
        String color = "green";
        long lineId = lineDao.save(name, color);

        String newName = "3호선";
        String newColor = "orange";
        lineDao.update(lineId, newName, newColor);

        final Line line = lineDao.findById(lineId);
        assertThat(line.getName()).isEqualTo(newName);
        assertThat(line.getColor()).isEqualTo(newColor);
    }

    @DisplayName("id로 노선을 삭제한다")
    @Test
    void delete() {
        String name = "2호선";
        String color = "green";
        lineDao.save(name, color);

        String name2 = "3호선";
        String color2 = "orange";
        long lineId = lineDao.save(name2, color2);

        lineDao.delete(lineId);
        assertThat(lineDao.findAll().size()).isEqualTo(1);
    }
}