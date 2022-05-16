package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.domain.fixture.SectionFixture.*;
import static wooteco.subway.domain.fixture.StationFixture.*;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.fixture.LineFixture;
import wooteco.subway.domain.property.Distance;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionSeries;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.StationEntity;

@SpringBootTest
@Sql(value = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SectionRepositoryTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;
    private Long lineId;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(dataSource, jdbcTemplate);
        StationDao stationDao = new JdbcStationDao(dataSource, jdbcTemplate);
        LineDao lineDao = new JdbcLineDao(dataSource, jdbcTemplate);

        stationDao.save(StationEntity.from(getStationA()));
        stationDao.save(StationEntity.from(getStationB()));
        stationDao.save(StationEntity.from(getStationC()));

        lineId = lineDao.save(LineEntity.from(LineFixture.getNewLine()));
    }

    @Test
    @DisplayName("persist를 통해 저장한다.")
    public void saveByPersist() {
        // given
        SectionSeries sectionSeries = new SectionSeries(List.of());

        // when
        sectionSeries.add(new Section(getStationB(), getStationC(), new Distance(10)));
        sectionRepository.persist(lineId, sectionSeries);
        // then
        assertThat(sectionRepository.findAllSections(lineId)).hasSize(1);
    }

    @Test
    @DisplayName("persist를 통해 삭제한다.")
    public void updateByPersist() {
        // given
        sectionDao.save(SectionEntity.from(getSectionAb(), lineId));
        sectionDao.save(SectionEntity.from(getSectionBc(), lineId));

        // when
        SectionSeries removed = new SectionSeries(List.of(getSectionAb()));
        sectionRepository.persist(lineId, removed);

        // then
        assertThat(sectionRepository.findAllSections(lineId)).hasSize(1);
    }
}