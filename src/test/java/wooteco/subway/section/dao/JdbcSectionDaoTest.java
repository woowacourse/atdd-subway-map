package wooteco.subway.section.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.dao.JdbcStationDao;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class JdbcSectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private JdbcSectionDao jdbcSectionDao;
    private StationDao stationDao;
    private Station ganamStation;
    private Station yeoksamStation;
    private Station jamsilStation;
    private Section section1;
    private Section section2;

    @BeforeEach
    void setUp() {

        jdbcSectionDao = new JdbcSectionDao(jdbcTemplate);
        stationDao = new JdbcStationDao(jdbcTemplate);
        ganamStation = new Station(1L, "강남역");
        yeoksamStation = new Station(2L, "역삼역");
        jamsilStation = new Station(3L, "잠실역");

        stationDao.save(ganamStation);
        stationDao.save(yeoksamStation);
        stationDao.save(jamsilStation);

        section1 = new Section(1L, 1L, ganamStation, yeoksamStation, 3);
        section2 = new Section(2L, 1L, yeoksamStation, jamsilStation, 3);
    }

    @Test
    @DisplayName("구간 저장 테스트")
    void save() {
        Section savedSection = jdbcSectionDao.save(section1);

        assertThat(savedSection.getUpStation()).isEqualTo(section1.getUpStation());
        assertThat(savedSection.getDownStation()).isEqualTo(section1.getDownStation());
        assertThat(savedSection.getDistance()).isEqualTo(section1.getDistance());
    }

    @Test
    @DisplayName("노선 번호로 구간 찾기 테스트")
    void findByLineId() {

        Section savedSection = jdbcSectionDao.save(section1);
        jdbcSectionDao.save(section2);

        Sections findByLineIdSections = jdbcSectionDao.findByLineId(savedSection.getLineId());

        Section findByLineIdSection = findByLineIdSections.getSections().get(0);

        assertThat(findByLineIdSection.getUpStation()).isEqualTo(section1.getUpStation());
        assertThat(findByLineIdSection.getDownStation()).isEqualTo(section1.getDownStation());
        assertThat(findByLineIdSection.getDistance()).isEqualTo(section1.getDistance());
    }

    @Test
    @DisplayName("구간 업데이트 테스트")
    void update() {
        Section savedSection = jdbcSectionDao.save(section1);
        Section updateSection = new Section(savedSection.getId(), savedSection.getLineId(), yeoksamStation, jamsilStation, 5);

        jdbcSectionDao.update(updateSection);

        Sections findByLineIdSections = jdbcSectionDao.findByLineId(savedSection.getLineId());

        assertThat(findByLineIdSections.getSections().get(0)).isEqualTo(updateSection);
    }

    @Test
    @DisplayName("구간 삭제 테스트")
    void delete() {

        Section savedSection1 = jdbcSectionDao.save(section1);
        Section savedSection2 = jdbcSectionDao.save(section2);
        jdbcSectionDao.delete(savedSection2.getLineId(), savedSection2.getDownStation());

        Sections findByLineIdSections = jdbcSectionDao.findByLineId(savedSection1.getLineId());

        assertThat(findByLineIdSections.getSections().get(0)).isEqualTo(section1);
        assertThat(findByLineIdSections.getSections().size()).isEqualTo(1);
    }
}
