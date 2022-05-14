package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import wooteco.subway.dao.dto.SectionDto;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

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
        Section expectedSection = new Section(1L, new Station(1L, "강남역"), new Station(2L, "선릉역"), 5);
        Section actualSection = sectionDao.save(expectedSection);

        assertThat(actualSection).isEqualTo(expectedSection);
    }

    @Test
    @DisplayName("id로 구간을 검색한다.")
    void findById() {
        Section section = new Section(1L, new Station(1L, "강남역"), new Station(2L, "선릉역"), 5);
        sectionDao.save(section);

        SectionDto actual = this.sectionDao.findById(1L).get(0);

        assertThat(actual.getLineId()).isEqualTo(1L);
        assertThat(actual.getUpStationId()).isEqualTo(1L);
        assertThat(actual.getDownStationId()).isEqualTo(2L);
        assertThat(actual.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("모든 구간을 검색한다.")
    void findAll() {
        Section section1 = new Section(1L, new Station(1L, "강남역"), new Station(2L, "선릉역"), 5);
        sectionDao.save(section1);
        Section section2 = new Section(1L, new Station(3L, "역삼역"), new Station(2L, "선릉역"), 5);
        sectionDao.save(section2);
        Section section3 = new Section(1L, new Station(2L, "선릉역"), new Station(4L, "잠실역"), 5);
        sectionDao.save(section3);

        List<SectionDto> sections = sectionDao.findAll();

        assertThat(sections.size()).isEqualTo(3);
    }
}
