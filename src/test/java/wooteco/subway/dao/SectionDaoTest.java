package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private DataSource dataSource;
    private SectionDao sectionDao;

    @BeforeEach
    void before() {
        sectionDao = new JdbcSectionDao(dataSource);
    }

    @Test
    void save() {
        long lineId = 1L;
        long upStationId = 2L;
        long downStationId = 3L;
        int distance = 10;
        Section result = sectionDao.save(new Section(lineId, upStationId, downStationId, distance));
        assertAll(
                () -> assertThat(result.getLineId()).isEqualTo(lineId),
                () -> assertThat(result.getUpStationId()).isEqualTo(upStationId),
                () -> assertThat(result.getDownStationId()).isEqualTo(downStationId),
                () -> assertThat(result.getDistance()).isEqualTo(distance)
        );
    }

    @Test
    void findByLineId() {
        sectionDao.save(new Section(1L, 2L, 3L, 10));
        sectionDao.save(new Section(1L, 3L, 4L, 20));
        sectionDao.save(new Section(2L, 1L, 2L, 10));

        assertThat(sectionDao.findByLineId(1L).size()).isEqualTo(2);
    }

    @Test
    void updateSection() {
        Section savedSection = sectionDao.save(new Section(1L, 2L, 3L, 10));

        sectionDao.updateSection(new Section(savedSection.getId(), 1L, 3L, 4L, 20));
        Sections sections = sectionDao.findByLineId(1L);
        Section section = sections.getSections().get(0);
        assertAll(
                () -> assertThat(section.getUpStationId()).isEqualTo(3L),
                () -> assertThat(section.getDownStationId()).isEqualTo(4L),
                () -> assertThat(section.getDistance()).isEqualTo(20)
        );
    }

    @Test
    void deleteSections() {
        Section section1 = sectionDao.save(new Section(1L, 2L, 3L, 10));
        Section section2 = sectionDao.save(new Section(1L, 3L, 4L, 20));

        sectionDao.deleteSections(List.of(section1, section2));
        assertThat(sectionDao.findByLineId(1L).size()).isEqualTo(0);
    }
}
