package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.Fixtures;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.SectionV2;
import wooteco.subway.domain.Station;

@JdbcTest
public class SectionDaoV2Test {

    private SectionDaoV2 sectionDao;
    private StationDao stationDao;
    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDaoV2(dataSource);
        this.stationDao = new StationDao(jdbcTemplate);
        this.lineDao = new LineDao(dataSource);
    }

    @Test
    @DisplayName("구간을 저장할 수 있다.")
    void save() {
        // given
        Station 강남역 = stationDao.findById(stationDao.save(Fixtures.강남역));
        Station 역삼역 = stationDao.findById(stationDao.save(Fixtures.역삼역));
        Line 이호선 = lineDao.findOnlyLineById(lineDao.save(Fixtures.이호선));

        SectionV2 section = new SectionV2(이호선.getId(), 강남역, 역삼역, 10);

        // when
        Long saveSectionId = sectionDao.save(section);

        // then
        SectionV2 saveSection = sectionDao.findById(saveSectionId);
        assertThat(saveSection)
                .extracting(SectionV2::getLineId, SectionV2::getUpStation, SectionV2::getDownStation, SectionV2::getDistance)
                .contains(이호선.getId(), 강남역, 역삼역, 10);
    }

    @Test
    @DisplayName("구간을 조회할 수 있다.")
    void findById() {
        // given
        Station 강남역 = stationDao.findById(stationDao.save(Fixtures.강남역));
        Station 역삼역 = stationDao.findById(stationDao.save(Fixtures.역삼역));
        Line 이호선 = lineDao.findOnlyLineById(lineDao.save(Fixtures.이호선));

        SectionV2 section = new SectionV2(이호선.getId(), 강남역, 역삼역, 10);
        Long 강남_역삼_id = sectionDao.save(section);

        // when
        SectionV2 findSection = sectionDao.findById(강남_역삼_id);

        // then
        assertThat(findSection)
                .extracting(SectionV2::getLineId, SectionV2::getUpStation, SectionV2::getDownStation, SectionV2::getDistance)
                .contains(강남_역삼_id, 강남역, 역삼역, 10);
    }

    @Test
    @DisplayName("노선 id를 통해 구간들을 조회할 수 있다.")
    void findByLineId() {
        // given
        Station 강남역 = stationDao.findById(stationDao.save(Fixtures.강남역));
        Station 역삼역 = stationDao.findById(stationDao.save(Fixtures.역삼역));
        Line 이호선 = lineDao.findOnlyLineById(lineDao.save(Fixtures.이호선));

        SectionV2 section = new SectionV2(이호선.getId(), 강남역, 역삼역, 10);
        Long 강남_역삼_id = sectionDao.save(section);

        // when
        List<SectionV2> findSections = sectionDao.findByLineId(강남_역삼_id);

        // then
        assertThat(findSections).hasSize(1)
                .extracting(SectionV2::getLineId, SectionV2::getUpStation, SectionV2::getDownStation, SectionV2::getDistance)
                .contains(
                        tuple(강남_역삼_id, 강남역, 역삼역, 10));
    }

    @Test
    @DisplayName("구간에 대한 정보를 변경할 수 있다.")
    void update() {
        // given
        Station 강남역 = stationDao.findById(stationDao.save(Fixtures.강남역));
        Station 역삼역 = stationDao.findById(stationDao.save(Fixtures.역삼역));
        Station 선릉역 = stationDao.findById(stationDao.save(Fixtures.선릉역));
        Line 이호선 = lineDao.findOnlyLineById(lineDao.save(Fixtures.이호선));

        SectionV2 oldSection = new SectionV2(이호선.getId(), 강남역, 역삼역, 10);
        Long 강남_역삼_id = sectionDao.save(oldSection);

        SectionV2 newSection = new SectionV2(강남_역삼_id, 이호선.getId(), 강남역, 선릉역, 5);

        // when
        sectionDao.update(newSection);

        // then
        SectionV2 findSection = sectionDao.findById(강남_역삼_id);
        assertThat(findSection)
                .extracting(SectionV2::getLineId, SectionV2::getUpStation, SectionV2::getDownStation, SectionV2::getDistance)
                .contains(강남_역삼_id, 강남역, 선릉역, 5);
    }

    @Test
    @DisplayName("구간에 대한 정보를 제거할 수 있다.")
    void deleteSectionById() {
        // given
        Station 강남역 = stationDao.findById(stationDao.save(Fixtures.강남역));
        Station 역삼역 = stationDao.findById(stationDao.save(Fixtures.역삼역));
        Station 선릉역 = stationDao.findById(stationDao.save(Fixtures.선릉역));
        Line 이호선 = lineDao.findOnlyLineById(lineDao.save(Fixtures.이호선));

        SectionV2 강남_역삼_구간 = new SectionV2(이호선.getId(), 강남역, 역삼역, 10);
        SectionV2 역삼_선릉_구간 = new SectionV2(이호선.getId(), 역삼역, 선릉역, 10);

        sectionDao.save(강남_역삼_구간);
        sectionDao.save(역삼_선릉_구간);

        Long 이호선_id = 이호선.getId();
        List<SectionV2> 이호선_구간_정보 = sectionDao.findByLineId(이호선_id);

        // when
        sectionDao.deleteSections(이호선_구간_정보);

        // then
        List<SectionV2> findSections = sectionDao.findByLineId(이호선_id);
        assertThat(findSections).hasSize(0);
    }
}
