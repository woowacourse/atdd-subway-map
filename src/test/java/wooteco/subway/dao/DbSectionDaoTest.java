package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Section;

import javax.sql.DataSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Sql("/init.sql")
class DbSectionDaoTest {

    @Autowired
    private DataSource dataSource;

    private DbSectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new DbSectionDao(dataSource);
    }

    @DisplayName("저장을 하고 리스트 및 단일 조회를 할 수 있다")
    @Test
    void can_save_find() {
        Section section = new Section(1L, 2L, 7, 1L);
        long savedSectionId = sectionDao.save(section);
        Section foundSection = sectionDao.findById(savedSectionId).get();
        assertThat(foundSection).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(section);
    }

    @DisplayName("리스트 조회를 할 수 있다")
    @Test
    void can_save_findAll() {
        long id1 = sectionDao.save(new Section(1L, 2L, 7, 1L));
        long id2 = sectionDao.save(new Section(4L, 3L, 3, 1L));

        List<Section> sections = sectionDao.findByLineId(1L);
        assertThat(sections.size()).isEqualTo(2);
        assertThat(sections).containsExactlyInAnyOrder(
                new Section(id1, 1L, 2L, 7, 1L),
                new Section(id2, 4L, 3L, 3, 1L));
    }

    @DisplayName("수정을 할 수 있다")
    @Test
    void can_update() {
        Section section = new Section(1L, 2L, 7, 1L);
        long savedId = sectionDao.save(section);

        Section updateSection = new Section(savedId, 3L, 4L, 11, 3L);

        sectionDao.update(updateSection);

        Section foundSection = sectionDao.findById(savedId).get();

        assertThat(foundSection).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(updateSection);
    }

    @DisplayName("삭제를 할 수 있다")
    @Test
    void can_delete() {
        Section section = new Section(1L, 2L, 7, 1L);
        long savedId = sectionDao.save(section);
        section.setId(savedId);
        sectionDao.deleteSection(section);
        List<Section> sections = sectionDao.findByLineId(1L);
        assertThat(sections).isEmpty();
    }
}