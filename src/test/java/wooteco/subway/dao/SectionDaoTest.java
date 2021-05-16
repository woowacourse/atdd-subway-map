package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Id;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.InvalidSectionOnLineException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("classpath:initializeTable.sql")
public class SectionDaoTest {

    private Line 일호선 = new Line(1L, "1호선", "bg-blue-100");
    private Station 신설역 = new Station(1L, "신설역");
    private Station 동묘역 = new Station(2L, "동묘역");
    private Station 동대문역 = new Station(3L, "동대문역");
    private Distance 거리 = new Distance(10);

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        lineDao.create(일호선);
        stationDao.save(신설역);
        stationDao.save(동묘역);
        stationDao.save(동대문역);

        Section 신설역_동묘역 = new Section(일호선, 신설역, 동묘역, 거리);
        Section 동묘역_동대문역 = new Section(일호선, 동묘역, 동대문역, 거리);

        sectionDao.save(신설역_동묘역);
        sectionDao.save(동묘역_동대문역);
    }

    @Test
    @DisplayName("Section 저장 테스트")
    public void save() {
        // given
        Section targetSection = new Section(일호선, 신설역, 동대문역, 거리);

        // when
        Section savedSection = sectionDao.save(targetSection);

        // then
        assertThat(savedSection.getLine()).isEqualTo(일호선);
        assertThat(savedSection.getUpStation()).isEqualTo(신설역);
        assertThat(savedSection.getDownStation()).isEqualTo(동대문역);
        assertThat(savedSection.getDistance()).isEqualTo(거리);
    }

    @Test
    @DisplayName("노선에 있는 Section들 반환 테스트")
    public void findAllByLine() {
        // given

        // when
        List<Section> sections = sectionDao.findAllByLine(일호선);
        Section 저장되어있는_신설_동묘_구간 = sections.get(0);
        Section 저장되어있는_동묘_동대문_구간 = sections.get(1);

        // then
        assertThat(저장되어있는_신설_동묘_구간.getLine()).isEqualTo(일호선);
        assertThat(저장되어있는_신설_동묘_구간.getUpStation()).isEqualTo(신설역);
        assertThat(저장되어있는_신설_동묘_구간.getDownStation()).isEqualTo(동묘역);
        assertThat(저장되어있는_신설_동묘_구간.getDistance()).isEqualTo(거리);
        assertThat(저장되어있는_동묘_동대문_구간.getLine()).isEqualTo(일호선);
        assertThat(저장되어있는_동묘_동대문_구간.getUpStation()).isEqualTo(동묘역);
        assertThat(저장되어있는_동묘_동대문_구간.getDownStation()).isEqualTo(동대문역);
        assertThat(저장되어있는_동묘_동대문_구간.getDistance()).isEqualTo(거리);
    }

    @Test
    @DisplayName("노선에 있는 상행역 조회 테스트")
    public void findByLineIdAndUpStation() {
        // given

        // when
        Section section = sectionDao.findByLineAndUpStation(일호선, 신설역)
            .orElseThrow(InvalidSectionOnLineException::new);

        // then
        assertThat(section.getLine()).isEqualTo(일호선);
        assertThat(section.getUpStation()).isEqualTo(신설역);
        assertThat(section.getDownStation()).isEqualTo(동묘역);
        assertThat(section.getDistance()).isEqualTo(거리);
    }

    @Test
    @DisplayName("노선에 있는 하행역 조회 테스트")
    public void findByLineIdAndDownStation() {
        // given

        // when
        Section section = sectionDao.findByLineAndDownStation(일호선, 동묘역)
            .orElseThrow(InvalidSectionOnLineException::new);

        // then
        assertThat(section.getLine()).isEqualTo(일호선);
        assertThat(section.getUpStation()).isEqualTo(신설역);
        assertThat(section.getDownStation()).isEqualTo(동묘역);
        assertThat(section.getDistance()).isEqualTo(거리);
    }

    @Test
    @DisplayName("노선에 있는 상/하행역 조회 예외처리")
    public void findByLineIdAndStation() {
        // given
        Station 없는역 = new Station(9L, "없는역");
        Line 없는노선 = new Line(2L, "없는노선", "bg-white-999");

        // when

        // then
        assertThat(sectionDao.findByLineAndUpStation(일호선, 없는역)).isEmpty();
        assertThat(sectionDao.findByLineAndUpStation(없는노선, 동묘역)).isEmpty();
        assertThat(sectionDao.findByLineAndDownStation(없는노선, 동묘역)).isEmpty();
        assertThat(sectionDao.findByLineAndUpStation(일호선, 동대문역)).isEmpty();
    }

    @Test
    @DisplayName("구간 중 상행역 제거")
    public void deleteByLineIdAndUpStation() {
        // given

        // when
        List<Section> 제거전_전체_역 = sectionDao.findAllByLine(일호선);
        int 제거된_역_개수 = sectionDao.deleteByLineAndUpStation(일호선, 신설역);
        List<Section> 제거후_전체_역 = sectionDao.findAllByLine(일호선);

        //then
        assertThat(제거된_역_개수).isEqualTo(1);
        assertThat(제거전_전체_역).hasSize(2);
        assertThat(제거후_전체_역).hasSize(1);
    }

    @Test
    @DisplayName("구간 중 하행역 제거")
    public void deleteByLineIdAndDownStation() {
        // given

        // when
        List<Section> 제거전_전체_역 = sectionDao.findAllByLine(일호선);
        int 제거된_역_개수 = sectionDao.deleteByLineAndDownStation(일호선, 동대문역);
        List<Section> 제거후_전체_역 = sectionDao.findAllByLine(일호선);

        //then
        assertThat(제거된_역_개수).isEqualTo(1);
        assertThat(제거전_전체_역).hasSize(2);
        assertThat(제거후_전체_역).hasSize(1);
    }

    @Test
    @DisplayName("구간 제거")
    public void delete() {
        // given
        Section section = new Section(new Id(2L), 일호선, 동묘역, 동대문역, 거리);

        // when
        List<Section> 제거전_전체_역 = sectionDao.findAllByLine(일호선);
        int 제거된_역_개수 = sectionDao.delete(section);
        List<Section> 제거후_전체_역 = sectionDao.findAllByLine(일호선);

        // then
        assertThat(제거된_역_개수).isEqualTo(1);
        assertThat(제거전_전체_역).hasSize(2);
        assertThat(제거후_전체_역).hasSize(1);
    }
}
