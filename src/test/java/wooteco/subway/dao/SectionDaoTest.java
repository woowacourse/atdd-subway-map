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

        Section section = sectionDao.save(expectedLineId, expectedUpStationId, expectedDownStationId, expectedDistance);
        long actualLineId = section.getLineId();
        long actualUpStationId = section.getUpStationId();
        long actualDownStationId = section.getDownStationId();
        int actualDistance = section.getDistance();

        assertThat(actualLineId).isEqualTo(expectedLineId);
        assertThat(actualUpStationId).isEqualTo(expectedUpStationId);
        assertThat(actualDownStationId).isEqualTo(expectedDownStationId);
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }
}
