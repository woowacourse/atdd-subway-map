package wooteco.subway.line.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.line.domain.section.Distance;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.infra.section.JdbcSectionDao;
import wooteco.subway.line.infra.section.SectionDao;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JdbcSectionDaoTest {

    private final SectionDao sectionDao;

    public JdbcSectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.sectionDao = new JdbcSectionDao(jdbcTemplate);
    }

    @DisplayName("구간 저장된다.")
    @Test
    void save() {
        //given
        Section section = new Section(1L, 1L, 2L, new Distance(10));

        //when
        Section savedSection = sectionDao.save(section);

        //then
        assertThat(savedSection.getId()).isNotNull();
        assertThat(savedSection.getLineId()).isEqualTo(section.getLineId());
        assertThat(savedSection.getUpStationId()).isEqualTo(section.getUpStationId());
        assertThat(savedSection.getDownStationId()).isEqualTo(section.getDownStationId());
        assertThat(savedSection.getDistance()).isEqualTo(section.getDistance());
    }

    @DisplayName("구간 찾을 수 있다.")
    @Test
    void findById() {
        //given
        Section section = new Section(1L, 1L, 2L, new Distance(10));
        Section savedSection = sectionDao.save(section);

        //when
        Section findSection = sectionDao.findById(savedSection.getId()).get();

        //then
        assertThat(findSection.getId()).isEqualTo(savedSection.getId());
        assertThat(findSection.getLineId()).isEqualTo(savedSection.getLineId());
        assertThat(findSection.getUpStationId()).isEqualTo(savedSection.getUpStationId());
        assertThat(findSection.getDownStationId()).isEqualTo(savedSection.getDownStationId());
        assertThat(findSection.getDistance()).isEqualTo(savedSection.getDistance());
    }
}