package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.section.Section;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SectionDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("구간 추가 성공 테스트")
    @Test
    void saveSection() {
        assertDoesNotThrow(() ->
                sectionDao.insert(1, new Section(1, 2, 5))
        );
    }
}
