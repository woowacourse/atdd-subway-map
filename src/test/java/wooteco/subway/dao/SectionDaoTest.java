package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    private Long savedLineId;
    private Long savedStationId1;
    private Long savedStationId2;
    private Long savedStationId3;
    private Long savedStationId4;
    private Long savedStationId5;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);

        LineDao lineDao = new LineDao(jdbcTemplate);
        StationDao stationDao = new StationDao(jdbcTemplate);

        Line newLine = new Line("2호선", "bg-red-600");
        savedLineId = lineDao.save(newLine).getId();

        Station newStation1 = new Station("선릉역");
        savedStationId1 = stationDao.save(newStation1).getId();

        Station newStation2 = new Station("삼성역");
        savedStationId2 = stationDao.save(newStation2).getId();

        Station newStation3 = new Station("종합운동장역");
        savedStationId3 = stationDao.save(newStation3).getId();

        Station newStation4 = new Station("잠실새내역");
        savedStationId4 = stationDao.save(newStation4).getId();

        Station newStation5 = new Station("잠실역");
        savedStationId5 = stationDao.save(newStation5).getId();
    }

    @DisplayName("호선 ID를 통해 구간 목록을 가져온다.")
    @Test
    void findSectionsByLineId() {
        // given
        Long lineId = savedLineId;
        Section savedSection1 = new Section(savedStationId1, savedStationId2, new Distance(10));
        Section savedSection2 = new Section(savedStationId2, savedStationId3, new Distance(10));

        Sections sections = new Sections(savedSection1);
        sections.addSection(savedSection2);

        sectionDao.saveSections(lineId, sections);

        // when
        Sections foundSections = sectionDao.findSectionsByLineId(lineId);
        List<Section> actual = foundSections.getValue();
        List<Long> actualUpStationIds = actual.stream().map(Section::getUpStationId).collect(Collectors.toList());
        List<Long> actualDownStationIds = actual.stream().map(Section::getDownStationId).collect(Collectors.toList());
        List<Distance> actualDistances = actual.stream().map(Section::getDistance).collect(Collectors.toList());

        List<Section> expected = sections.getValue();
        List<Long> expectedUpStationIds = expected.stream().map(Section::getUpStationId).collect(Collectors.toList());
        List<Long> expectedDownStationIds = expected.stream().map(Section::getDownStationId).collect(Collectors.toList());
        List<Distance> expectedDistances = expected.stream().map(Section::getDistance).collect(Collectors.toList());

        // then
        assertAll(
                () -> assertThat(actualUpStationIds).containsAll(expectedUpStationIds),
                () -> assertThat(actualDownStationIds).containsAll(expectedDownStationIds),
                () -> assertThat(actualDistances).containsAll(expectedDistances)
        );
    }

    @DisplayName("호선 ID와 구간 목록을 전달받아 구간 목록을 저장한다.")
    @Test
    void saveSections() {
        // given
        Long lineId = savedLineId;
        Sections sections = new Sections(savedStationId1, savedStationId2, new Distance(10));
        sections.addSection(new Section(savedStationId2, savedStationId3, new Distance(10)));
        sections.addSection(new Section(savedStationId3, savedStationId4, new Distance(10)));
        sections.addSection(new Section(savedStationId4, savedStationId5, new Distance(10)));

        // when
        sectionDao.saveSections(lineId, sections);
        List<Section> actual = sectionDao.findSectionsByLineId(lineId).getValue();
        List<Section> expected = sections.getValue();

        // then
        assertThat(actual).hasSameSizeAs(expected);
    }
}
