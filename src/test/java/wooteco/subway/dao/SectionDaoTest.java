package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDao(jdbcTemplate);
    }

    @Test
    @DisplayName("정상적으로 저장되는 경우를 테스트한다")
    public void saveTest() {
        assertDoesNotThrow(() -> {
            sectionDao.save(1L, 1L, 2L, 0);
        });
    }

    @Test
    @DisplayName("정상적으로 제거되는 경우를 테스트한다")
    public void deleteTest() {
        sectionDao.save(1L, 1L, 2L, 0);
        assertDoesNotThrow(() -> {
            sectionDao.deleteById(1L, 1L);
        });
    }
}
