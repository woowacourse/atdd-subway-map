package wooteco.subway.section.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class JDBCSectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private JDBCSectionDao jdbcSectionDao;

    @BeforeEach
    void setUp() {
        jdbcSectionDao = new JDBCSectionDao(jdbcTemplate);
    }

    @Test
    @DisplayName("구간 저장 테스트")
    void save() {
        Section section = new Section(1L, 1L, 1L, 2L, 3);
        Section savedSection = jdbcSectionDao.save(section);

        assertThat(savedSection.getUpStationId()).isEqualTo(section.getUpStationId());
        assertThat(savedSection.getDownStationId()).isEqualTo(section.getDownStationId());
        assertThat(savedSection.getDistance()).isEqualTo(section.getDistance());
    }

    @Test
    @DisplayName("노선 번호로 구간 찾기 테스트")
    void findByLineId() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 3);
        Section section2 = new Section(2L, 1L, 2L, 3L, 3);

        List<Section> sectionList = new ArrayList<>();
        sectionList.add(section1);
        sectionList.add(section2);

        Sections sections = new Sections(sectionList);
        jdbcSectionDao.save(section1);
        jdbcSectionDao.save(section2);

        Sections findByLineIdSections = jdbcSectionDao.findByLineId(1L);

        Section findByLineIdSection = findByLineIdSections.getSections().get(0);

        assertThat(findByLineIdSection.getUpStationId()).isEqualTo(section1.getUpStationId());
        assertThat(findByLineIdSection.getDownStationId()).isEqualTo(section1.getDownStationId());
        assertThat(findByLineIdSection.getDistance()).isEqualTo(section1.getDistance());
    }

    @Test
    @DisplayName("구간 업데이트 테스트")
    void update() {
        Section section = new Section(1L, 1L, 1L, 2L, 3);

        Section savedSection = jdbcSectionDao.save(section);
        Section updateSection = new Section(savedSection.getId(), 1L, 2L, 3L, 5);

        jdbcSectionDao.update(updateSection);

        Sections findByLineIdSections = jdbcSectionDao.findByLineId(1L);

        assertThat(findByLineIdSections.getSections().get(0)).isEqualTo(updateSection);
    }

    @Test
    @DisplayName("구간 삭제 테스트")
    void delete() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 3);
        Section section2 = new Section(2L, 1L, 2L, 3L, 3);

        jdbcSectionDao.save(section1);
        jdbcSectionDao.save(section2);
        jdbcSectionDao.delete(1L, 3L);

        Sections findByLineIdSections = jdbcSectionDao.findByLineId(1L);

        assertThat(findByLineIdSections.getSections().get(0)).isEqualTo(section1);
        assertThat(findByLineIdSections.getSections().size()).isEqualTo(1);
    }
}
