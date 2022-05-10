package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 DAO 테스트")
@JdbcTest
class SectionDaoTest {

    private SectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void save() {
        sectionDao.save(1L, new Section(1L, 2L, 10));

        Integer count = jdbcTemplate.queryForObject("select count(*) from SECTION", Integer.class);

        assertThat(count).isEqualTo(1);
    }
}
