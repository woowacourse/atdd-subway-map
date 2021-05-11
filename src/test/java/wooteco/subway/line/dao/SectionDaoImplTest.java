package wooteco.subway.line.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.line.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Sql(scripts = {"classpath:schema.sql", "classpath:dummy.sql"})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class SectionDaoImplTest {
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;

    public SectionDaoImplTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionDao = new SectionDaoImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void save() {
        Section savedSection = sectionDao.save(new Section(1L, 1L, 1L, 2L, 10));

        assertThat(savedSection.upStation().id()).isEqualTo(1L);
        assertThat(savedSection.downStation().id()).isEqualTo(2L);
        assertThat(savedSection.distance()).isEqualTo(10);
    }

    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }

    @Test
    void delete() {
    }
}