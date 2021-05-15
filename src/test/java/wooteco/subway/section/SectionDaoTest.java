package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@Sql("classpath:schema.sql")
class SectionDaoTest {

    @Autowired
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        Line line = new Line(1L);
        Station upStation = new Station(1L);
        Station downStation = new Station(2L);
        int distance = 1;

        Section section = new Section(line, upStation, downStation, distance);

        sectionDao.save(section);
    }

    @DisplayName("구간을 저장한다.")
    @Test
    void save() {
        Line line = new Line(1L);
        Station upStation = new Station(2L);
        Station downStation = new Station(3L);
        int distance = 1;

        Section section = new Section(line, upStation, downStation, distance);

        sectionDao.save(section);

        assertThat(sectionDao.count(line.getId())).isEqualTo(2);
    }

    @DisplayName("구간 정보를 Map 형태로 가져온다.")
    @Test
    void sectionMap() {
        Line line = new Line(1L);
        Station upStation = new Station(2L);
        Station downStation = new Station(3L);
        int distance = 1;

        Section section = new Section(line, upStation, downStation, distance);

        sectionDao.save(section);

        Map<Long, Long> sectionMap = sectionDao.sectionMap(line.getId());

        assertThat(sectionMap.size()).isEqualTo(2);
        assertThat(sectionMap.get(2L)).isEqualTo(3L);
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void delete() {
        Line line = new Line(1L);
        Station upStation = new Station(1L);
        Station downStation = new Station(2L);

        Section section = new Section(line, upStation, downStation);

        sectionDao.delete(section);

        assertThat(sectionDao.count(1L)).isEqualTo(0);
    }

    @DisplayName("구간의 거리를 가져온다")
    @Test
    void distance() {
        Line line = new Line(1L);
        Station upStation = new Station(1L);
        Station downStation = new Station(2L);

        Section section = new Section(line, upStation, downStation);

        assertThat(sectionDao.distance(section)).isEqualTo(1);
    }

    @DisplayName("노선에 존재하는 역인지 확인한다.")
    @Test
    void isExistStation() {
        long lineId = 1L;
        long existStationId = 1L;
        long notExistStationId = 100L;

        assertTrue(sectionDao.isExistStation(lineId, existStationId));
        assertFalse(sectionDao.isExistStation(lineId, notExistStationId));
    }

    @Test
    void findDownStationIdByUpStationId() {
        long lineId = 1L;
        long upStationId = 1L;

        assertThat(sectionDao.findDownStation(lineId, upStationId).get(0)).isEqualTo(2L);
    }

    @Test
    void findUpStationIdByDownStationId() {
        long lineId = 1L;
        long downStationId = 2L;

        assertThat(sectionDao.findUpStation(lineId, downStationId).get(0)).isEqualTo(1L);
    }

    @Test
    void count() {
        long lineId = 1L;

        assertThat(sectionDao.count(lineId)).isEqualTo(1);
    }
}