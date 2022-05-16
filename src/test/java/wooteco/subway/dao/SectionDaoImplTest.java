package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

@JdbcTest
public class SectionDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDaoImpl(jdbcTemplate);
    }

    @Test
    void save() {
        Section section = new Section(1L, 2L, 3L, 10);
        assertDoesNotThrow(() -> sectionDao.save(section));
    }

    @Test
    void delete() {
        // given
        Long savedId = sectionDao.save(new Section(1L, 2L, 3L, 3));

        // when
        sectionDao.deleteById(savedId);

        // then
        List<Section> sections = sectionDao.findByLineId(1L);
        assertThat(sections).hasSize(0);
    }
}
