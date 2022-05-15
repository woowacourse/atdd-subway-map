package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@JdbcTest
@Sql("classpath:section.sql")
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

    @Test
    @DisplayName("LineId로 데이터를 가져오는 경우를 테스트한다")
    public void getSectionsByLineIdTest() {
        sectionDao.save(1L, 1L, 2L, 5);
        sectionDao.save(1L, 1L, 3L, 3);

        assertThat(sectionDao.findAllByLineId(1L).size()).isEqualTo(2);
    }

    @Test
    @DisplayName("LineId에 해당하는 데이터가 없는 경우를 테스트한다")
    public void getSectionsByLineIdNotTest() {
        sectionDao.save(1L, 1L, 2L, 5);
        sectionDao.save(1L, 1L, 3L, 3);

        assertThat(sectionDao.findAllByLineId(2L).size()).isEqualTo(0);
    }
}
