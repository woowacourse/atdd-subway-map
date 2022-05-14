package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
class SectionDaoTest {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    @Autowired
    public SectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.lineDao = new LineDao(jdbcTemplate);
        this.stationDao = new StationDao(jdbcTemplate);
        this.sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("구간을 저장한다.")
    @Test
    void 구간_저장() {
        Line line = generateLine("2호선", "bg-green-600");
        Station upStation = generateStation("선릉역");
        Station downStation = generateStation("잠실역");
        Integer distance = 10;

        Section createdSection = sectionDao.save(new Section(line, upStation, downStation, distance));

        assertAll(
                () -> assertThat(createdSection.getLine()).isEqualTo(line),
                () -> assertThat(createdSection.getUpStation()).isEqualTo(upStation),
                () -> assertThat(createdSection.getDownStation()).isEqualTo(downStation),
                () -> assertThat(createdSection.getDistance()).isEqualTo(distance)
        );
    }

    @DisplayName("노선 조작 기능")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromLine() {
        Line line = generateLine("2호선", "bg-green-600");
        Station upStation1 = generateStation("선릉역");
        Station downStation1 = generateStation("잠실역");
        Integer distance1 = 10;
        Station upStation2 = generateStation("신도림역");
        Station downStation2 = generateStation("신대방역");
        Integer distance2 = 7;
        sectionDao.save(new Section(line, upStation1, downStation1, distance1));
        sectionDao.save(new Section(line, upStation2, downStation2, distance2));

        return Stream.of(
                dynamicTest("노선 별 구간을 조회한다.", () -> {
                    List<Section> sections = sectionDao.findByLineId(line.getId());

                    assertThat(sections.size()).isEqualTo(2);
                }),

                dynamicTest("노선 별 구간을 삭제한다.", () -> {
                    sectionDao.deleteByLineId(line.getId());

                    List<Section> sections = sectionDao.findByLineId(line.getId());
                    assertThat(sections.size()).isEqualTo(0);
                })
        );
    }
    
    @DisplayName("구간을 전체 저장한다.")
    @Test
    void 구간_전체_저장() {
        Line line = generateLine("2호선", "bg-green-600");
        Station upStation1 = generateStation("선릉역");
        Station downStation1 = generateStation("잠실역");
        Integer distance1 = 10;
        Station upStation2 = generateStation("신도림역");
        Station downStation2 = generateStation("신대방역");
        Integer distance2 = 7;
        Section section1 = new Section(line, upStation1, downStation1, distance1);
        Section section2 = new Section(line, upStation2, downStation2, distance2);

        sectionDao.saveAll(List.of(section1, section2));

        List<Section> sections = sectionDao.findByLineId(line.getId());
        assertThat(sections.size()).isEqualTo(2);
    }

    private Line generateLine(String name, String color) {
        return lineDao.save(new Line(name, color));
    }

    private Station generateStation(String name) {
        return stationDao.save(new Station(name));
    }
}
