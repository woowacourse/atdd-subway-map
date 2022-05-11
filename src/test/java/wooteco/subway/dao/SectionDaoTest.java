package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SectionDaoTest {

    private final SectionDao sectionDao;

    public SectionDaoTest(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @AfterEach
    void reset() {
        sectionDao.deleteAll();
    }

    @Test
    @DisplayName("구간을 저장한다.")
    void save() {
        long expectedLineId = 1L;
        long expectedUpStationId = 2L;
        long expectedDownStationId = 3L;
        int expectedDistance = 5;

        SectionDto section = sectionDao.save(expectedLineId, expectedUpStationId, expectedDownStationId,
                expectedDistance);
        long actualLineId = section.getLineId();
        long actualUpStationId = section.getUpStationId();
        long actualDownStationId = section.getDownStationId();
        int actualDistance = section.getDistance();

        assertThat(actualLineId).isEqualTo(expectedLineId);
        assertThat(actualUpStationId).isEqualTo(expectedUpStationId);
        assertThat(actualDownStationId).isEqualTo(expectedDownStationId);
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("하행역을 수정한다.")
    void update_downStation() {
        SectionDto sectionDto = sectionDao.save(1L, 2L, 3L, 5);

        sectionDao.updateDownStation(sectionDto.getId(), 5L);

        assertThat(sectionDao.findById(1L).get(0).getDownStationId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("거리를 수정한다.")
    void update_distance() {
        SectionDto sectionDto = sectionDao.save(1L, 2L, 3L, 5);

        int expectedDistance = 8;
        sectionDao.updateDistance(sectionDto.getId(), expectedDistance);

        int actualDistance = sectionDao.findDistanceById(1L).orElse(0);
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }
}
