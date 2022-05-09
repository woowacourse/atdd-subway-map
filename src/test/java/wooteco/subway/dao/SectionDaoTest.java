package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

@JdbcTest
public class SectionDaoTest {

    public static final Section SECTION = new Section(1L, 1L, 1L, 1);

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
}
