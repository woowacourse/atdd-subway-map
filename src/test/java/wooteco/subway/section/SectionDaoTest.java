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
        long stationId1 = stationDao.save("강남역");
        long stationId2 = stationDao.save("잠실역");
        long stationId3 = stationDao.save("신림역");

        Line line = new Line("2호선", "green");
        long lineId = lineDao.save(line);

        int distance = 100;
        int distance2 = 200;
        Section section = new Section(lineId, stationId1, stationId2, distance);
        Section section2 = new Section(lineId, stationId2, stationId3, distance2);

        long sectionId = sectionDao.save(section);
        assertEquals(sectionId + 1, sectionDao.save(section2));
    }
}