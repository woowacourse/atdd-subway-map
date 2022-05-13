package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Section;

@JdbcTest
@Sql("classpath:sectionDao.sql")
class SectionDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDaoImpl(jdbcTemplate);
    }

    @DisplayName("노선에 해당하는 구간들을 반환한다.")
    @Test
    void findByLineId() {
        Long lineId = 1L;
        Section firstSection = new Section(lineId, 1L, 2L, 10);
        Section secondSection = new Section(lineId, 2L, 3L, 8);
        sectionDao.save(firstSection);
        sectionDao.save(secondSection);

        Section savedFirstSection = new Section(1L, lineId, 1L, 2L, 10);
        Section savedSecondSection = new Section(2L, lineId, 2L, 3L, 8);

        List<Section> sections = sectionDao.findByLineId(lineId);

        assertThat(sections).isEqualTo(Arrays.asList(savedFirstSection, savedSecondSection));
    }

    @DisplayName("id에 해당하는 구간의 상행선, 하행선, 거리를 수정한다.")
    @Test
    void update() {
        Section firstSection = new Section(1L, 1L, 2L, 10);
        sectionDao.save(firstSection);
        final Section updatingSection = new Section(1L, 1L, 4L, 5L, 8);
        sectionDao.update(updatingSection);

        assertThat(sectionDao.findByLineId(1L)).contains(updatingSection);
    }
}
