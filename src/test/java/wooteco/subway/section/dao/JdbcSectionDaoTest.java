package wooteco.subway.section.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.line.dao.JDBCLineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.dao.JdbcStationDao;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class JdbcSectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private JdbcSectionDao jdbcSectionDao;
    private JdbcStationDao stationDao;
    private JDBCLineDao lineDao;

    private Line 이호선;
    private Line 등록된이호선;

    private Station 강남역;
    private Station 역삼역;
    private Station 잠실역;
    private Section 강남_역삼_구간;
    private Section 역삼_잠실_구간;

    @BeforeEach
    void setUp() {

        jdbcSectionDao = new JdbcSectionDao(jdbcTemplate);
        stationDao = new JdbcStationDao(jdbcTemplate);
        lineDao = new JDBCLineDao(jdbcTemplate);

        이호선 = new Line("2호선", "RED");

        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        잠실역 = new Station(3L, "잠실역");

        stationDao.save(강남역);
        stationDao.save(역삼역);
        stationDao.save(잠실역);

        등록된이호선 = lineDao.save(이호선);

        강남_역삼_구간 = new Section(1L, 등록된이호선.getId(), 강남역, 역삼역, 3);
        역삼_잠실_구간 = new Section(2L, 등록된이호선.getId(), 역삼역, 잠실역, 3);
    }

    @Test
    @DisplayName("구간 저장 테스트")
    void save() {
        Section savedSection = jdbcSectionDao.save(강남_역삼_구간);

        assertThat(savedSection.getUpStation()).isEqualTo(강남_역삼_구간.getUpStation());
        assertThat(savedSection.getDownStation()).isEqualTo(강남_역삼_구간.getDownStation());
        assertThat(savedSection.getDistance()).isEqualTo(강남_역삼_구간.getDistance());
    }

    @Test
    @DisplayName("노선 번호로 구간 찾기 테스트")
    void findByLineId() {

        Section savedSection = jdbcSectionDao.save(강남_역삼_구간);
        jdbcSectionDao.save(역삼_잠실_구간);

        Sections findByLineIdSections = jdbcSectionDao.findByLineId(savedSection.getLineId());

        Section findByLineIdSection = findByLineIdSections.getSections().get(0);

        assertThat(findByLineIdSection.getUpStation()).isEqualTo(강남_역삼_구간.getUpStation());
        assertThat(findByLineIdSection.getDownStation()).isEqualTo(강남_역삼_구간.getDownStation());
        assertThat(findByLineIdSection.getDistance()).isEqualTo(강남_역삼_구간.getDistance());
    }

    @Test
    @DisplayName("구간 업데이트 테스트")
    void update() {
        Section savedSection = jdbcSectionDao.save(강남_역삼_구간);
        Section updateSection = new Section(savedSection.getId(), savedSection.getLineId(), 역삼역, 잠실역, 5);

        jdbcSectionDao.update(updateSection);

        Sections findByLineIdSections = jdbcSectionDao.findByLineId(savedSection.getLineId());

        assertThat(findByLineIdSections.getSections().get(0)).isEqualTo(updateSection);
    }

    @Test
    @DisplayName("구간 삭제 테스트")
    void delete() {
        Section savedSection1 = jdbcSectionDao.save(강남_역삼_구간);
        Section savedSection2 = jdbcSectionDao.save(역삼_잠실_구간);

        jdbcSectionDao.delete(savedSection2.getLineId(), savedSection2.getDownStation());

        Sections findByLineIdSections = jdbcSectionDao.findByLineId(savedSection1.getLineId());

        assertThat(findByLineIdSections.getSections()).hasSize(1)
                .containsExactly(강남_역삼_구간);
    }
}
