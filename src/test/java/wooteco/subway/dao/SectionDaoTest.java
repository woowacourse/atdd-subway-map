package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
class SectionDaoTest {

    private final Station upStation = new Station(1L, "천호역");
    private final Station downStation = new Station(2L, "강남역");
    private final Station lastStation = new Station(3L, "강릉역");
    private SectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(dataSource);
        String schemaQuery = "create table if not exists SECTION ( id bigint auto_increment not null, " +
                "line_id bigint not null, up_station_id bigint not null, down_station_id bigint not null, " +
                "distance int, primary key(id) )";
        jdbcTemplate.execute(schemaQuery);
    }

    @DisplayName("구간을 등록 및 조회한다.")
    @Test
    void save() {
        Section firstSection = new Section(upStation, downStation, 10, 1L);
        Section lastSection = new Section(downStation, lastStation, 5, 1L);

        long firstSectionId = sectionDao.save(firstSection);
        long lastSectionId = sectionDao.save(lastSection);
        List<Section> sections = sectionDao.findAllByLineId(1L);

        assertThat(sections).containsExactly(new Section(firstSectionId, 10, 1L),
                new Section(lastSectionId, 5, 1L));
    }

    @DisplayName("지하철역 아이디로 구간을 조회한다.")
    @Test
    void findAllByStationId() {
        Section firstSection = new Section(upStation, downStation, 10, 1L);
        Section lastSection = new Section(downStation, lastStation, 5, 1L);

        long firstSectionId = sectionDao.save(firstSection);
        long lastSectionId = sectionDao.save(lastSection);
        List<Section> sections = sectionDao.findAllByStationId(1L);

        assertThat(sections).containsExactly(new Section(firstSectionId, 10, 1L));
    }


    @DisplayName("구간에 등록된 역들의 아이디를 반환한다.")
    @Test
    void findStationIdsById() {
        Section firstSection = new Section(upStation, downStation, 10, 1L);

        long id = sectionDao.save(firstSection);
        List<Long> stationIds = sectionDao.findStationIdsById(id);

        assertThat(stationIds).containsExactly(1L, 2L);
    }

    @DisplayName("기존에 등록된 구간의 정보를 변경한다.")
    @Test
    void update() {
        Section firstSection = new Section(upStation, downStation, 10, 1L);
        long firstSectionId = sectionDao.save(firstSection);

        Section requestSection = new Section(firstSectionId, upStation, lastStation, 15, 1L);
        sectionDao.update(requestSection);
        List<Long> stationIds = sectionDao.findStationIdsById(firstSectionId);

        assertThat(stationIds).containsExactly(1L, 3L);
    }

    @DisplayName("구간 정보 변경시 아이디로 조회 불가하면 예외가 발생한다.")
    @Test
    void cannotUpdate() {
        Section requestSection = new Section(6984L, upStation, lastStation, 15, 1L);

        assertThatCode(() -> sectionDao.update(requestSection))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ExceptionStatus.ID_NOT_FOUND.getMessage());
    }

    @DisplayName("구간 아이디로 구간 정보를 삭제한다.")
    @Test
    void deleteById() {
        Section firstSection = new Section(upStation, downStation, 10, 1L);
        long firstSectionId = sectionDao.save(firstSection);
        long beforeSectionCounts = sectionDao.findAllByLineId(1L).size();

        sectionDao.deleteById(firstSectionId);
        long afterSectionCounts = sectionDao.findAllByLineId(1L).size();

        assertThat(beforeSectionCounts - 1).isEqualTo(afterSectionCounts);
    }

    @DisplayName("존재하지 않는 구간 아이디로 구간 삭제가 불가능하다.")
    @Test
    void cannotDelete() {
        assertThatCode(() -> sectionDao.deleteById(8585L))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ExceptionStatus.ID_NOT_FOUND.getMessage());
    }

    @DisplayName("노선 아이디로 구간들을 삭제한다.")
    @Test
    void deleteByLineId() {
        Section firstSection = new Section(upStation, downStation, 10, 1L);
        Section secondSection = new Section(upStation, downStation, 10, 1L);
        sectionDao.save(firstSection);
        sectionDao.save(secondSection);

        int beforeCounts = sectionDao.findAllByLineId(1L).size();
        sectionDao.deleteAllById(1L);
        int afterCounts = sectionDao.findAllByLineId(1L).size();

        assertThat(beforeCounts - 2).isEqualTo(afterCounts);
    }
}
