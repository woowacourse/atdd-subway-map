package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Section;

@JdbcTest
public class SectionDaoTest {

    private SectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("지하철 구간을 저장하고 찾는다.")
    @Test
    void save() {
        Section section = new Section(1L, 1L, 2L, 10);
        sectionDao.save(section);

        assertThat(sectionDao.findAllByLineId(1L)).hasSize(1);
    }

    @DisplayName("지하철 구간을 수정한다. - 상행 기준")
    @Test
    void updateByUpStation() {
        Section section = new Section(1L, 1L, 2L, 10);
        Section section1 = new Section(1L, 1L, 3L, 7);
        sectionDao.save(section);

        sectionDao.updateByUpStationId(section1);

        assertThat(sectionDao.findAllByLineId(1L).get(0).getDownStationId()).isEqualTo(3L);
    }

    @DisplayName("지하철 구간을 수정한다. - 하행 기준")
    @Test
    void updateByDownStation() {
        Section section = new Section(1L, 1L, 2L, 10);
        Section section1 = new Section(1L, 3L, 2L, 7);
        sectionDao.save(section);

        sectionDao.updateByDownStationId(section1);

        assertThat(sectionDao.findAllByLineId(1L).get(0).getUpStationId()).isEqualTo(3L);
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void delete() {
        Section section = new Section(1L, 1L, 2L, 10);
        sectionDao.save(section);

        List<Section> sections = sectionDao.findAllByLineId(1L);
        sectionDao.delete(sections.get(0).getUpStationId());

        assertThat(sectionDao.findAllByLineId(1L)).hasSize(0);
    }
}
