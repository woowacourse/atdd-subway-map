package wooteco.subway.section;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.line.Line;
import wooteco.subway.line.LineDao;
import wooteco.subway.station.StationDao;

@SpringBootTest
@Transactional
@Sql("classpath:schema.sql")
class SectionDaoTest {

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    @DisplayName("새로운 구간을 생성한다")
    @Test
    void save() {
        String station1 = "강남역";
        String station2 = "잠실역";
        String station3 = "신림역";
        long stationId1 = stationDao.save(station1);
        long stationId2 = stationDao.save(station2);
        long stationId3 = stationDao.save(station3);

        String name = "2호선";
        String color = "green";
        Line line = new Line(name, color);
        long lineId = lineDao.save(line);

        Section section = new Section(lineId, stationId1, stationId2);
        Section section2 = new Section(lineId, stationId2, stationId3);
        assertDoesNotThrow(() -> sectionDao.save(section));
        assertDoesNotThrow(() -> sectionDao.save(section2));
    }
}