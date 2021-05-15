package wooteco.subway.section.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.line.model.Line;
import wooteco.subway.section.api.dto.SectionDto;
import wooteco.subway.section.model.Section;
import wooteco.subway.station.model.Station;

@JdbcTest
class SectionDaoTest {

    private SectionDao sectionDao;

    @BeforeEach
    void setUp(@Autowired JdbcTemplate jdbcTemplate) {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("특정 노선에 포함되는 section들을 조회하는 기능")
    @Test
    void findSectionsByLineId() {
        //given
        Line line = new Line(1L, "2호선", "GREEN");
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "잠실역");
        Station station3 = new Station(3L, "건대입구역");
//        LineRequest request = new LineRequest("2호선", "green", 1L, 2L, 10);
//        LineRequest request2 = new LineRequest("2호선", "green", 2L, 3L, 10);
        Section section1 = Section.builder()
            .line(line)
            .upStation(station1)
            .downStation(station2)
            .distance(10)
            .build();
        Section section2 = Section.builder()
            .line(line)
            .upStation(station2)
            .downStation(station3)
            .distance(15)
            .build();

        sectionDao.save(section1);
        sectionDao.save(section2);
        //when
        List<SectionDto> sections = sectionDao.findSectionsByLineId(line.getId());
        //then
        assertThat(sections).hasSize(2);
    }
}
