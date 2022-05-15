package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    private SectionDao sectionDao;
    private StationDao stationDao;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate, dataSource);
        stationDao = new StationDao(jdbcTemplate, dataSource);

        station1 = stationDao.save(new Station("짱구역"));
        station2 = stationDao.save(new Station("짱아역"));
        station3 = stationDao.save(new Station("선아역"));
        station4 = stationDao.save(new Station("흰둥역"));
    }

    @Test
    @DisplayName("구간을 등록한다.")
    void saveTest() {
        Section section = new Section(1L, station1, station2, 10);
        Section savedSection = sectionDao.save(section);

        assertAll(
            () -> assertThat(savedSection.getLineId()).isEqualTo(1L),
            () -> assertThat(savedSection.getUpStation()).isEqualTo(section.getUpStation()),
            () -> assertThat(savedSection.getDownStation()).isEqualTo(section.getDownStation()),
            () -> assertThat(savedSection.getDistance()).isEqualTo(10)
        );
    }

    @Test
    @DisplayName("노선에 등록된 구간들을 모두 조회한다.")
    void findAllByLineId() {
        Section section1 = new Section(1L, station1, station2, 10);
        Section section2 = new Section(1L, station1, station3, 7);
        Section section3 = new Section(1L, station2, station4, 12);

        Section savedSection = sectionDao.save(section1);
        sectionDao.save(section2);
        sectionDao.save(section3);

        List<Section> sectionsByLineId = sectionDao.findByLineId(savedSection.getLineId());
        List<Long> upStationIds = sectionsByLineId.stream()
            .map(section -> section.getUpStation().getId())
            .collect(Collectors.toList());
        List<Long> downStationIds = sectionsByLineId.stream()
            .map(section -> section.getDownStation().getId())
            .collect(Collectors.toList());

        assertAll(
            () -> assertThat(sectionsByLineId).hasSize(3),
            () -> assertThat(upStationIds).containsExactly(station1.getId(), station1.getId(), station2.getId()),
            () -> assertThat(downStationIds).containsExactly(station2.getId(), station3.getId(), station4.getId())
        );
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteTest() {
        Section section1 = new Section(1L, station1, station2, 10);
        Section section2 = new Section(1L, station1, station3, 7);
        Section section3 = new Section(1L, station2, station4, 12);

        Section savedSection = sectionDao.save(section1);
        sectionDao.save(section2);
        sectionDao.save(section3);

        sectionDao.deleteById(savedSection.getId());

        List<Section> sectionsByLineId = sectionDao.findByLineId(savedSection.getLineId());
        List<Long> sectionIds = sectionsByLineId.stream()
            .map(Section::getId)
            .collect(Collectors.toList());

        assertAll(
            () -> assertThat(sectionIds).hasSize(2),
            () -> assertThat(sectionIds).doesNotContain(savedSection.getId())
        );
    }
}
