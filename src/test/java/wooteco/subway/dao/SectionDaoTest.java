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
import wooteco.subway.domain.Section;

@JdbcTest
public class SectionDaoTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private JdbcSectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(jdbcTemplate);
    }
    
    @Test
    @DisplayName("Section 객체를 저장하기")
    void save() {
        // given
        Section section = new Section(null, 1L, 1L, 2L, 1);

        // when
        Section savedSection = sectionDao.save(section);

        // then
        assertAll(() -> {
            assertThat(savedSection.getId()).isNotNull();
            assertThat(savedSection).isEqualTo(section);
        });
    }
    
    @Test
    @DisplayName("특정 노선의 구간 반환하기")
    void findByLineId() {
        // given
        Section savedSection1 = sectionDao.save(new Section(null, 1L, 1L, 2L, 1));
        Section savedSection2 = sectionDao.save(new Section(null, 1L, 2L, 3L, 2));

        // when
        List<Section> sections = sectionDao.findByLineId(1L);
        
        // then
        assertThat(sections).containsOnly(savedSection1, savedSection2);
    }

    @Test
    @DisplayName("특정 노선 삭제하기")
    void delete() {
        // given
        Section savedSection = sectionDao.save(new Section(null, 1L, 1L, 2L, 1));

        // when
        int deletedSections = sectionDao.deleteById(savedSection.getId());

        // then
        assertThat(deletedSections).isOne();
    }

    @Test
    @DisplayName("구간 정보를 수정하기")
    void update() {
        // given
        Section section = sectionDao.save(new Section(null, 1L, 1L, 2L, 1));
        Section sectionForUpdate = new Section(section.getId(), 1L, 1L, 3L, 3);

        // when
        int updated = sectionDao.update(sectionForUpdate);

        // then
        assertThat(updated).isOne();
    }
}
