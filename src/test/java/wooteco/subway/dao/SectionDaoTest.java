package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
            assertThat(savedSection.getId()).isOne();
            assertThat(savedSection).isEqualTo(section);
        });
    }
}
