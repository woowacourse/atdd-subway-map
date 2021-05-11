package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

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
        long lineId = 1L;
        long upStationId = 1L;
        long downStationId = 2L;
        int distance = 1;

        sectionDao.save(lineId, upStationId, downStationId, distance);
    }

    @DisplayName("구간을 저장한다.")
    @Test
    void save() {
        long lineId = 1L;
        long upStationId = 2L;
        long downStationId = 3L;
        int distance = 1;

        sectionDao.save(lineId, upStationId, downStationId, distance);

        assertThat(sectionDao.count(lineId)).isEqualTo(2);
    }

    @DisplayName("구간 정보를 Map 형태로 가져온다.")
    @Test
    void sectionMap() {
        long lineId = 1L;
        long upStationId = 2L;
        long downStationId = 3L;
        int distance = 1;

        sectionDao.save(lineId, upStationId, downStationId, distance);

        Map<Long, Long> sectionMap = sectionDao.sectionMap(lineId);

        assertThat(sectionMap.size()).isEqualTo(2);
        assertThat(sectionMap.get(2L)).isEqualTo(3L);
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void delete() {
        long lineId = 1L;
        long upStationId = 1L;
        long downStationId = 2L;
        sectionDao.delete(lineId, upStationId, downStationId);

        assertThat(sectionDao.count(1L)).isEqualTo(0);
    }

    @DisplayName("구간의 거리를 가져온다")
    @Test
    void distance() {
        long lineId = 1L;
        long upStationId = 1L;
        long downStationId = 2L;

        assertThat(sectionDao.distance(lineId, upStationId, downStationId)).isEqualTo(1);
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

        assertThat(sectionDao.findDownStationIdByUpStationId(lineId, upStationId).get(0)).isEqualTo(2L);
    }

    @Test
    void findUpStationIdByDownStationId() {
        long lineId = 1L;
        long downStationId = 2L;

        assertThat(sectionDao.findUpStationIdByDownStationId(lineId, downStationId).get(0)).isEqualTo(1L);
    }

    @Test
    void count() {
        long lineId = 1L;

        assertThat(sectionDao.count(lineId)).isEqualTo(1);
    }
}