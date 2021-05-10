package wooteco.subway.line.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.dto.SectionEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
class SectionDaoImplTest {
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;

    @Autowired
    public SectionDaoImplTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionDao = new SectionDaoImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void save() {
        Section savedSection = sectionDao.save(new Section(1L, 1L, 2L, 10));

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