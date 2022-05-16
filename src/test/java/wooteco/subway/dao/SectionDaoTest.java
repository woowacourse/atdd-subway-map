package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Transactional
class SectionDaoTest {
    private SectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        sectionDao = new SectionDao(jdbcTemplate);
        jdbcTemplate.execute("DROP TABLE Section IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE Section(" +
                "id bigint auto_increment not null,\n" +
                "line_id bigint not null,\n" +
                "up_station_id bigint not null,\n" +
                "down_station_id bigint not null,\n" +
                "distance int,\n" +
                "primary key(id))");
    }

    @DisplayName("Section 객체의 정보가 제대로 저장되는 것을 확인한다.")
    @Test
    void save() {
        final Section section = new Section(1L, 2L, 10);
        sectionDao.save(1L, section);
        final int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Section", Integer.class);

        assertThat(actual).isEqualTo(1);
    }

    @DisplayName("노선에 해당하는 Section 객체들을 가져오는 것을 확인한다.")
    @Test
    void find_sections_by_line_id() {
        final Section section = new Section(1L, 2L, 10);
        sectionDao.save(1L, section);
        final List<Section> sections = sectionDao.findSectionsByLineId(1L);
        final int actual = sections.size();

        assertThat(actual).isEqualTo(1);
    }

    @DisplayName("노선에 해당하는 Section 객체들이 없을 때 빈 리스트를 가져오는 것을 확인한다.")
    @Test
    void find_sections_by_line_id_when_empty() {
        final List<Section> sections = sectionDao.findSectionsByLineId(1L);
        final int actual = sections.size();

        assertThat(actual).isEqualTo(0);
    }

    @DisplayName("노선에 해당하는 구간의 개수를 확인한다.")
    @Test
    void counts_by_line() {
        final Section section = new Section(1L, 2L, 10);
        sectionDao.save(1L, section);
        final int actual = sectionDao.countsByLine(1L);
        assertThat(actual).isEqualTo(1);
    }

    @DisplayName("노선에 해당하는 구간을 모두 삭제하는 것을 확인한다.")
    @Test
    void delete_all_by_line() {
        final Section section = new Section(1L, 2L, 10);
        sectionDao.save(1L, section);
        sectionDao.deleteAllByLine(1L);
        final int actual = jdbcTemplate.queryForObject("select count(*) from Section where line_id = ?", Integer.class, 1L);

        assertThat(actual).isEqualTo(0);
    }

    @DisplayName("상행역 id로 구간을 찾는 것을 확인한다.")
    @Test
    void find_section_by_up_station_id() {
        final Section section = new Section(1L, 2L, 10);
        sectionDao.save(1L, section);
        final Section findingStation = sectionDao.findSectionByUpStationId(1L, section);

        assertThat(findingStation.getUpStationId()).isEqualTo(1L);
    }

    @DisplayName("하행역 id로 구간을 찾는 것을 확인한다.")
    @Test
    void find_section_by_down_station_id() {
        final Section section = new Section(1L, 2L, 10);
        sectionDao.save(1L, section);
        final Section findingStation = sectionDao.findSectionByDownStationId(1L, section);

        assertThat(findingStation.getDownStationId()).isEqualTo(2L);
    }

    @DisplayName("상행역 id로 구간을 찾아 수정하는 것을 확인한다.")
    @Test
    void edit_by_up_station_id() {
        final Section section = new Section(1L, 2L, 10);
        sectionDao.save(1L, section);
        final Section editedSection = new Section(1L, 2L, 5);
        sectionDao.editByUpStationId(1L, editedSection);
        final Section actualSection = sectionDao.findSectionByUpStationId(1L, editedSection);

        assertThat(actualSection.getDistance()).isEqualTo(5);
    }

    @DisplayName("같은 라인에서 입력한 역을 가지고 있는 구간을 모두 반환하는 것을 확인한다.")
    @Test
    void find_sections_by_station_id() {
        final Section section1 = new Section(1L, 2L, 10);
        final Section section2 = new Section(2L, 3L, 10);
        sectionDao.save(1L, section1);
        sectionDao.save(1L, section2);
        final List<Section> sections = sectionDao.findSectionsByStationId(1L, 2L);

        assertThat(sections.size()).isEqualTo(2);
    }

    @DisplayName("같은 라인에서 입력한 역을 가지고 있는 구간을 모두 삭제하는 것을 확인한다.")
    @Test
    void delete_section_by_station_id() {
        final Section section1 = new Section(1L, 2L, 10);
        final Section section2 = new Section(2L, 3L, 10);
        sectionDao.save(1L, section1);
        sectionDao.save(1L, section2);
        sectionDao.deleteSectionByStationId(1L, 2L);
        final int actual = sectionDao.countsByLine(1L);

        assertThat(actual).isEqualTo(0);
    }
}
