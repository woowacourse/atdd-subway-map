package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
@Sql({"/schema.sql", "/test-data.sql"})
public class SectionDaoTest {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    @Autowired
    public SectionDaoTest(JdbcTemplate jdbcTemplate) {
        lineDao = new LineDao(jdbcTemplate);
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @Test
    void save() {
        //given
        final Line savedLine = lineDao.findById(1L);
        final Section section = Section.createWithoutId(new Station(2L, "잠실역"), new Station(3L, "석촌역"), 3);
        savedLine.addSection(section);
        //when
        sectionDao.deleteByLineId(1L);
        sectionDao.save(savedLine.getSections(), 1L);
        //then
        final List<Section> foundSections = lineDao.findById(1L).getSections();
        assertThat(foundSections.size()).isEqualTo(2);
    }

    @Test
    void deleteByLineId() {
        //when
        sectionDao.deleteByLineId(1L);
        //then
        final List<Section> foundSections = lineDao.findById(1L).getSections();
        assertThat(foundSections.size()).isZero();
    }
}
