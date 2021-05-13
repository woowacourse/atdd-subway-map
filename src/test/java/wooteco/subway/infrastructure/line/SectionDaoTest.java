package wooteco.subway.infrastructure.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.line.section.Section;
import wooteco.util.SectionFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Sql("classpath:/line/lineQueryInit.sql")
@JdbcTest
class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @Test
    void save_inListCase() {
        List<Section> sections = Arrays.asList(
                SectionFactory.create(1L, 1L, 2L, 10L),
                SectionFactory.create(1L, 1L, 2L, 10L),
                SectionFactory.create(1L, 1L, 2L, 10L)
        );

        sectionDao.save(sections);

        List<Section> allSections = sectionDao.findAll();

        assertThat(allSections).isEqualTo(sections);
    }

    @Test
    void save_inSingleCase() {
        Section section = SectionFactory.create(1L, 1L, 2L, 10L);
        sectionDao.save(section);

        Section sectionById = sectionDao.findById(1L);

        assertThat(sectionById).isEqualTo(section);
    }

    @Test
    void findAllByLineId() {
        List<Section> sections = Arrays.asList(
                SectionFactory.create(1L, 1L, 2L, 10L),
                SectionFactory.create(1L, 2L, 3L, 10L),
                SectionFactory.create(1L, 3L, 4L, 10L),
                SectionFactory.create(2L, 1L, 2L, 10L),
                SectionFactory.create(2L, 2L, 3L, 10L),
                SectionFactory.create(2L, 3L, 4L, 10L)
        );

        sectionDao.save(sections);

        List<Section> all = sectionDao.findAllByLineId(1L);

        assertThat(all).isEqualTo(Arrays.asList(
                SectionFactory.create(1L, 1L, 2L, 10L),
                SectionFactory.create(1L, 2L, 3L, 10L),
                SectionFactory.create(1L, 3L, 4L, 10L)
        ));
    }

    @Test
    void findAll() {
        List<Section> sections = Arrays.asList(
                SectionFactory.create(1L, 1L, 2L, 10L),
                SectionFactory.create(1L, 2L, 3L, 10L),
                SectionFactory.create(1L, 3L, 4L, 10L),
                SectionFactory.create(2L, 1L, 2L, 10L),
                SectionFactory.create(2L, 2L, 3L, 10L),
                SectionFactory.create(2L, 3L, 4L, 10L)
        );

        sectionDao.save(sections);

        assertThat(sectionDao.findAll()).isEqualTo(sections);
    }

    @Test
    void update() {
        Section section = SectionFactory.create(1L, 1L, 2L, 10L);

        Section save = sectionDao.save(section);
        Section newSection = SectionFactory.create(
                save.getId(),
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance()
        );

        sectionDao.update(Collections.singletonList(newSection));
        Section sectionById = sectionDao.findById(save.getId());

        assertThat(sectionById).isEqualTo(newSection);
    }

    @Test
    void delete() {
        List<Section> sections = Arrays.asList(
                SectionFactory.create(1L, 1L, 2L, 10L),
                SectionFactory.create(1L, 2L, 3L, 10L)
        );

        sectionDao.save(sections);
        sectionDao.delete(Collections.singletonList(
                SectionFactory.create(1L,1L, 1L, 2L, 10L)
        ));

        assertThat(sectionDao.findAll()).hasSize(1);
    }

}