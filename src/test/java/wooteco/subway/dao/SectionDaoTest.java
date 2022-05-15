package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Import({
        LineDao.class,
        StationDao.class,
        SectionDao.class
})
@JdbcTest
public class SectionDaoTest {

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineDao lineDao;

    @Test
    @DisplayName("구간을 저장할 수 있다.")
    void save() {
        // given
        Station 강남역 = stationDao.findById(stationDao.save(new Station("강남역")));
        Station 역삼역 = stationDao.findById(stationDao.save(new Station("역삼역")));
        Line 이호선 = lineDao.findById(lineDao.save(new Line("2호선", "초록색")));

        Section section = new Section(이호선.getId(), 강남역, 역삼역, 10);

        // when
        Long saveSectionId = sectionDao.save(section);

        // then
        Section saveSection = sectionDao.findById(saveSectionId);
        assertThat(saveSection)
                .extracting(Section::getLineId, Section::getUpStation, Section::getDownStation, Section::getDistance)
                .contains(이호선.getId(), 강남역, 역삼역, 10);
    }

    @Test
    @DisplayName("구간을 조회할 수 있다.")
    void findById() {
        // given
        Station 강남역 = stationDao.findById(stationDao.save(new Station("강남역")));
        Station 역삼역 = stationDao.findById(stationDao.save(new Station("역삼역")));
        Line 이호선 = lineDao.findById(lineDao.save(new Line("2호선", "초록색")));

        Section section = new Section(이호선.getId(), 강남역, 역삼역, 10);
        Long 강남_역삼_id = sectionDao.save(section);

        // when
        Section findSection = sectionDao.findById(강남_역삼_id);

        // then
        assertThat(findSection)
                .extracting(Section::getLineId, Section::getUpStation, Section::getDownStation, Section::getDistance)
                .contains(이호선.getId(), 강남역, 역삼역, 10);
    }

    @Test
    @DisplayName("노선 id를 통해 구간들을 조회할 수 있다.")
    void findByLineId() {
        // given
        Station 강남역 = stationDao.findById(stationDao.save(new Station("강남역")));
        Station 역삼역 = stationDao.findById(stationDao.save(new Station("역삼역")));
        Line 이호선 = lineDao.findById(lineDao.save(new Line("2호선", "초록색")));
        Section section = new Section(이호선.getId(), 강남역, 역삼역, 10);
        Long 강남_역삼_id = sectionDao.save(section);

        // when
        List<Section> findSections = sectionDao.findByLineId(이호선.getId());

        // then
        assertThat(findSections).hasSize(1)
                .extracting(Section::getLineId, Section::getUpStation, Section::getDownStation, Section::getDistance)
                .contains(
                        tuple(이호선.getId(), 강남역, 역삼역, 10));
    }

    @Test
    @DisplayName("구간에 대한 정보를 변경할 수 있다.")
    void update() {
        // given
        Station 강남역 = stationDao.findById(stationDao.save(new Station("강남역")));
        Station 역삼역 = stationDao.findById(stationDao.save(new Station("역삼역")));
        Station 선릉역 = stationDao.findById(stationDao.save(new Station("선릉역")));
        Line 이호선 = lineDao.findById(lineDao.save(new Line("2호선", "초록색")));

        Section oldSection = new Section(이호선.getId(), 강남역, 역삼역, 10);
        Long 강남_역삼_id = sectionDao.save(oldSection);

        Section newSection = new Section(강남_역삼_id, 이호선.getId(), 강남역, 선릉역, 5);

        // when
        sectionDao.update(newSection);

        // then
        Section findSection = sectionDao.findById(강남_역삼_id);
        assertThat(findSection)
                .extracting(Section::getLineId, Section::getUpStation, Section::getDownStation, Section::getDistance)
                .contains(이호선.getId(), 강남역, 선릉역, 5);
    }

    @Test
    @DisplayName("구간에 대한 정보를 제거할 수 있다.")
    void deleteSectionById() {
        // given
        Station 강남역 = stationDao.findById(stationDao.save(new Station("강남역")));
        Station 역삼역 = stationDao.findById(stationDao.save(new Station("역삼역")));
        Station 선릉역 = stationDao.findById(stationDao.save(new Station("선릉역")));
        Line 이호선 = lineDao.findById(lineDao.save(new Line("2호선", "초록색")));

        Section 강남_역삼_구간 = new Section(이호선.getId(), 강남역, 역삼역, 10);
        Section 역삼_선릉_구간 = new Section(이호선.getId(), 역삼역, 선릉역, 10);

        sectionDao.save(강남_역삼_구간);
        sectionDao.save(역삼_선릉_구간);

        Long 이호선_id = 이호선.getId();
        List<Section> 이호선_구간_정보 = sectionDao.findByLineId(이호선_id);

        // when
        sectionDao.deleteSections(이호선_구간_정보);

        // then
        List<Section> findSections = sectionDao.findByLineId(이호선_id);
        assertThat(findSections).hasSize(0);
    }
}
