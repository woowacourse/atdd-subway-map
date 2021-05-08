package wooteco.subway.dao.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.entity.SectionEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
class DBSectionDaoTest {
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;

    @Autowired
    public DBSectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionDao = new DBSectionDao(jdbcTemplate);
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void save() {
        SectionEntity savedSection = sectionDao.save(new SectionEntity(1L, 1L, 2L, 10));

        assertThat(savedSection.getUpStationId()).isEqualTo(1L);
        assertThat(savedSection.getDownStationId()).isEqualTo(2L);
        assertThat(savedSection.getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("구간을 추가시 존재하지 않는 역이면 예외가 발생한다.")
    void saveException1() {
        assertThatThrownBy(() ->
                sectionDao.save(new SectionEntity(1L, 3L, 4L, 10)))
                .isInstanceOf(IllegalStateException.class);


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