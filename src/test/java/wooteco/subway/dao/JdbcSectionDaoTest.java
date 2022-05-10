package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionEntity;

@JdbcTest
public class JdbcSectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(jdbcTemplate);
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void save() {
        Long lineId = 1L;
        Station station1 = new Station(1L, "선릉역");
        Station station2 = new Station(2L, "강남역");
        Section section = new Section(station1, station2, 10);

        sectionDao.save(lineId, section);

        assertThat(sectionDao.findByLine(1L).size()).isEqualTo(1);
    }

    @DisplayName("지하철 구간을 수정한다.")
    @Test
    void update() {
        Long lineId = 1L;
        Station station1 = new Station(1L, "선릉역");
        Station station2 = new Station(2L, "강남역");
        Section section = new Section(station1, station2, 10);

        sectionDao.save(lineId, section);
        SectionEntity sectionEntity = sectionDao.findByLine(lineId).get(0);
        Section newSection = new Section(sectionEntity.getId(), new Station(3L, "역삼역"), station2, 11);
        sectionDao.update(lineId, newSection);
    }
}
