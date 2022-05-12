package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

@JdbcTest
public class JdbcSectionDaoTest {

    private JdbcSectionDao jdbcSectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcSectionDao = new JdbcSectionDao(jdbcTemplate);
    }

    @DisplayName("구간 정보를 등록한다.")
    @Test
    void save() {
        Section section = new Section(1L, 1L, 2L, 5);
        assertThat(jdbcSectionDao.save(section)).isNotNull();
    }

    @DisplayName("노선에 포함되는 지하철역들을 조회한다.")
    @Test
    void findStationsByLineId() {
        Section section1 = new Section(1L, 1L, 2L, 7);
        Section section2 = new Section(1L, 1L, 3L, 3);
        Section section3 = new Section(1L, 2L, 5L, 2);
        Section section4 = new Section(1L, 5L, 4L, 4);
        jdbcSectionDao.save(section1);
        jdbcSectionDao.save(section2);
        jdbcSectionDao.save(section3);
        jdbcSectionDao.save(section4);
        List<Section> sections = jdbcSectionDao.findSectionsByLineId(1L);

        assertThat(sections).containsExactly(section1, section2, section3, section4);
    }

    @DisplayName("구간 정보를 등록할 때 변경되는 구간 정보에 대해 업데이트한다.")
    @Test
    void update() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 7);
        Section section2 = new Section(2L, 1L, 1L, 3L, 3);
        Section section3 = new Section(3L,1L, 2L, 5L, 2);
        Section section4 = new Section(4L,1L, 5L, 4L, 4);
        jdbcSectionDao.save(section1);
        jdbcSectionDao.save(section2);
        jdbcSectionDao.save(section3);
        jdbcSectionDao.save(section4);
        boolean isUpdated = jdbcSectionDao.update(1L, section4);

        assertThat(isUpdated).isTrue();
    }

    @DisplayName("구간 정보에 지하철역 ID가 존재하면 구간을 삭제한다.")
    @Test
    void delete() {
        Section section1 = new Section(1L, 1L, 1L, 3L, 4);
        Section section2 = new Section(2L, 1L, 3L, 2L, 3);
        Section section3 = new Section(3L,1L, 2L, 5L, 2);
        jdbcSectionDao.save(section1);
        jdbcSectionDao.save(section2);
        jdbcSectionDao.save(section3);

        int deletedCount = jdbcSectionDao.delete(3L, 1L);

        assertThat(deletedCount).isEqualTo(2);
    }
}
