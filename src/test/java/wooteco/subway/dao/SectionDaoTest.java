package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

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

    @Test
    @DisplayName("구간을 저장한다.")
    void save() {
        long expectedLineId = 1L;
        long expectedUpStationId = 2L;
        long expectedDownStationId = 3L;
        int expectedDistance = 5;

        SectionDto section = sectionDao.save(expectedLineId, expectedUpStationId, expectedDownStationId, expectedDistance);
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
    @DisplayName("거리를 검색한다.")
    void findDistance() {
        int expectedDistance = 5;
        SectionDto section = sectionDao.save(1L, 1L, 2L, expectedDistance);

        int actualDistance = sectionDao.findDistance(1L, 1L, 2L).orElse(0);

        assertThat(actualDistance).isEqualTo(actualDistance);
    }

    @Test
    @DisplayName("상행역과 하행역이 일치하는 역을 검색한다.")
    void findByUpStationAndDownStation() {
        SectionDto sectionDto = sectionDao.save(1L, 2L, 3L, 5);

        long actualId = sectionDao.findByUpStationAndDownStation(sectionDto.getUpStationId(), sectionDto.getDownStationId()).orElse(0L);

        assertThat(actualId).isEqualTo(1L);
    }
}
