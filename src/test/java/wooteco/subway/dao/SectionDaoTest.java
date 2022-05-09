package wooteco.subway.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SectionDaoTest {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;


    public SectionDaoTest(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @BeforeEach
    void set() {
        stationDao.save("강남역");
        stationDao.save("선릉역");
    }

    @AfterEach
    void reset() {
        //sectionDao.deleteAll();
    }

    @Test
    @DisplayName("구간을 저장한다.")
    void save() {
        LineRequest lineRequest = new LineRequest("강남역", "green", 1L, 2L, 10);
        lineDao.save(lineRequest);

        Section section = sectionDao.save(lineRequest, 1L);
        long actualUpStationId = section.getUpStationId();
        long actualDownStationId = section.getDownStationId();
        int actualDistance = section.getDistance();

        assertThat(actualUpStationId).isEqualTo(1L);
        assertThat(actualDownStationId).isEqualTo(2L);
        assertThat(actualDistance).isEqualTo(10);
    }
}
