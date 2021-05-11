package wooteco.subway.section;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@SpringBootTest
@Sql("classpath:test-schema.sql")
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

    @DisplayName("노선의 시작역 id를 반환한다.")
    @Test
    void findStartStation() {
        save();
        assertEquals(1L, sectionDao.findStartStationIdByLineId(1L));
    }

    @DisplayName("노선의 끝역 id를 반환한다.")
    @Test
    void findEndStation() {
        save();
        assertEquals(3L, sectionDao.findEndStationIdByLineId(1L));
    }

    @DisplayName("같은 노선의 모든 구간을 조회해 map으로 반환한다.")
    @Test
    void findSections() {
        save();
        Map<Station, Station> expectedSections = new HashMap<>();
        expectedSections.put(stationDao.findById(1L), stationDao.findById(2L));
        expectedSections.put(stationDao.findById(2L), stationDao.findById(3L));

        Map<Long, Long> expect = new HashMap<>();
        expect.put(1L, 2L);
        expect.put(2L, 3L);
        Map<Long, Long> sectionsByLineId = sectionDao.findSectionsByLineId(1L);

        assertEquals(expect, sectionsByLineId);
    }

    @DisplayName("UpStation이 같은 구간을 조회한다.")
    @Test
    void findSectionBySameUpStation() {
        long stationId1 = stationDao.save("강남역");
        long stationId2 = stationDao.save("잠실역");

        Line line = new Line("2호선", "green");
        long lineId = lineDao.save(line);

        int distance = 100;
        Section section = new Section(lineId, stationId1, stationId2, distance);
        sectionDao.save(section);

        assertEquals(section, sectionDao.findSectionBySameUpStation(lineId, stationDao.findById(1L)).orElseThrow(
            IllegalInputException::new));
    }

    @DisplayName("DownStation이 같은 구간을 조회한다.")
    @Test
    void findSectionBySameDownStation() {
        long stationId1 = stationDao.save("강남역");
        long stationId2 = stationDao.save("잠실역");
        long stationId3 = stationDao.save("신림역");

        Line line = new Line("2호선", "green");
        long lineId = lineDao.save(line);

        int distance = 100;
        int distance2 = 200;
        Section section = new Section(lineId, stationId1, stationId2, distance);
        Section section2 = new Section(lineId, stationId2, stationId3, distance2);
        sectionDao.save(section);
        sectionDao.save(section2);

       assertEquals(section2, sectionDao.findSectionBySameDownStation(lineId, stationDao.findById(3L)).get());
    }

    @DisplayName("upStation, downStation을 수정한다.")
    @Test
    void updateUpStation() {
        long stationId1 = stationDao.save("강남역");
        long stationId2 = stationDao.save("잠실역");
        long stationId3 = stationDao.save("신림역");

        Line line = new Line("2호선", "green");
        long lineId = lineDao.save(line);

        int distance = 100;
        int distance2 = 200;
        Section section = new Section(lineId, stationId1, stationId2, distance);
        Section section2 = new Section(lineId, stationId2, stationId3, distance2);
        sectionDao.save(section);
        sectionDao.save(section2);

        assertEquals(1, sectionDao.updateUpStation(section, stationDao.findById(3L)));
        assertEquals(1, sectionDao.updateDownStation(section, stationDao.findById(1L)));
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void deleteSection() {
        long stationId1 = stationDao.save("강남역");
        long stationId2 = stationDao.save("잠실역");
        long stationId3 = stationDao.save("신림역");

        Line line = new Line("2호선", "green");
        long lineId = lineDao.save(line);

        int distance = 100;
        int distance2 = 200;
        Section section = new Section(lineId, stationId1, stationId2, distance);
        Section section2 = new Section(lineId, stationId2, stationId3, distance2);
        sectionDao.save(section);
        sectionDao.save(section2);

        assertEquals(1,sectionDao.deleteSection(section2));
        assertEquals(1, sectionDao.findSectionsByLineId(lineId).size());
    }
}