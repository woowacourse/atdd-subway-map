package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import wooteco.subway.dao.dto.SectionDto;

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
    @DisplayName("id로 구간을 검색한다.")
    void findById() {
        SectionDto expected = sectionDao.save(1L, 2L, 3L, 5);

        SectionDto actual = this.sectionDao.findById(1L).get(0);

        assertThat(actual.getLineId()).isEqualTo(expected.getLineId());
        assertThat(actual.getUpStationId()).isEqualTo(expected.getUpStationId());
        assertThat(actual.getDownStationId()).isEqualTo(expected.getDownStationId());
        assertThat(actual.getDistance()).isEqualTo(expected.getDistance());
    }

    @Test
    @DisplayName("모든 구간을 검색한다.")
    void findAll() {
        sectionDao.save(1L, 2L, 3L, 4);
        sectionDao.save(2L, 3L, 4L, 5);
        sectionDao.save(3L, 4L, 5L, 6);

        List<SectionDto> sections = sectionDao.findAll();

        assertThat(sections.size()).isEqualTo(3);
    }
}
