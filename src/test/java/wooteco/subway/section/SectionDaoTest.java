package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.Stations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SectionDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;
    private Station gangnam = new Station(1L, "강남역");
    private Station jamsil = new Station(2L, "잠실역");
    private Station sadang = new Station(3L, "사당역");
    private Stations stations;


    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDao(jdbcTemplate);
        stations = new Stations(Arrays.asList(gangnam, jamsil, sadang));
    }

    @DisplayName("구간 추가 및 조회 테스트")
    @Test
    void saveSection() {
        sectionDao.insert(1L, new Section(gangnam, jamsil, 5));
        List<Section> sections = sectionDao.selectAll(1L, stations);
        assertThat(sections).containsExactly(new Section(gangnam, jamsil, 5));
    }

    @DisplayName("구간 삭제 테스트")
    @Test
    void deleteSection() {
        sectionDao.insert(1L, new Section(gangnam, jamsil, 5));
        sectionDao.delete(1L, 2L);
        List<Section> sections = sectionDao.selectAll(1L, stations);
        assertThat(sections).isEmpty();
    }

    @DisplayName("구간 추가 시 새로운 역이 하행역으로 등록됐을 때 구간 변경 테스트")
    @Test
    void updateWhenNewStationDownwardTest() {
        sectionDao.insert(1L, new Section(gangnam, sadang, 30));
        sectionDao.updateWhenNewStationDownward(1L, new Section(gangnam, jamsil, 20));
        List<Section> sections = sectionDao.selectAll(1L, stations);
        Section section = sections.get(0);
        assertThat(section.getUpStationId()).isEqualTo(2L);
        assertThat(section.getDistance()).isEqualTo(10);
    }

    @DisplayName("구간 추가 시 새로운 역이 상행역으로 등록됐을 때 구간 변경 테스트")
    @Test
    void updateWhenNewStationUpwardTest() {
        sectionDao.insert(1L, new Section(gangnam, sadang, 30));
        sectionDao.updateWhenNewStationUpward(1L, new Section(jamsil, sadang, 20));
        List<Section> sections = sectionDao.selectAll(1L, stations);
        Section section = sections.get(0);
        assertThat(section.getUpStationId()).isEqualTo(1L);
        assertThat(section.getDistance()).isEqualTo(10);
    }
}
