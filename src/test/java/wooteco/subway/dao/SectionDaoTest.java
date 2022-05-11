package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.section.JdbcSectionDao;
import wooteco.subway.domain.Section;

@JdbcTest
public class SectionDaoTest {

    public static final Section SECTION = new Section(1L, 1L, 2L, 1);

    private JdbcSectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(jdbcTemplate);
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void save() {
        sectionDao.save(SECTION);

        Integer count = jdbcTemplate.queryForObject("select count(*) from SECTION", Integer.class);

        assertThat(count).isEqualTo(1);
    }

    @DisplayName("특정 노선의 지하철 구간 목록을 조회한다.")
    @Test
    void findAllByLineId() {
        sectionDao.save(SECTION);
        sectionDao.save(new Section(1L, 2L, 3L, 2));

        List<Section> sections = sectionDao.findAllByLineId(1L);

        assertThat(sections).hasSize(2);
    }

    @DisplayName("지하철 구간의 전체 목록을 조회한다.")
    @Test
    void findAll() {
        sectionDao.save(SECTION);
        sectionDao.save(new Section(2L, 2L, 3L, 2));

        List<Section> sections = sectionDao.findAll();

        assertThat(sections).hasSize(2);
    }

    @DisplayName("지하철 구간을 수정한다.")
    @Test
    void update() {
        long saveSectionId = sectionDao.save(SECTION);
        Section updateSection = new Section(saveSectionId, 1L, 1L, 3L, 3);

        sectionDao.update(updateSection);

        Section section = sectionDao.findAll().get(0);
        assertAll(
                () -> {
                    assertThat(section.getDownStationId()).isEqualTo(3L);
                    assertThat(section.getDistance()).isEqualTo(3);
                }
        );
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void delete() {
        long sectionId = sectionDao.save(SECTION);

        sectionDao.delete(sectionId);

        assertThat(sectionDao.existSectionById(sectionId)).isFalse();
    }

    @DisplayName("해당 id의 지하철 구간이 있다면 true를 반환한다.")
    @Test
    void existSectionById() {
        long sectionId = sectionDao.save(SECTION);

        assertThat(sectionDao.existSectionById(sectionId)).isTrue();
    }
}
