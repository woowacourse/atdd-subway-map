package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

class SectionDaoTest extends DaoTest {

    private Long lineId;
    private Long upStationId;
    private Long downStationId;

    @BeforeEach
    void setUpData() {
        final Line line = new Line("2호선", "bg-green-600");
        lineId = lineDao.insert(line)
                .orElseThrow()
                .getId();

        final Station upStation = new Station("선릉역");
        upStationId = stationDao.insert(upStation)
                .orElseThrow()
                .getId();

        final Station downStation = new Station("삼성역");
        downStationId = stationDao.insert(downStation)
                .orElseThrow()
                .getId();
    }

    @Test
    @DisplayName("구간 객체를 저장하면 ID를 반환한다.")
    void Insert() {
        // given
        final Section section = new Section(lineId, upStationId, downStationId, 10);

        // when
        final Long actual = sectionDao.insert(section);

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("역이 구간에 등록되어 있으면 true 를 반환한다.")
    void IsStationExist_InsertedLineId_TrueReturned() {
        // given
        final Section section = new Section(lineId, upStationId, downStationId, 10);
        sectionDao.insert(section);

        // when
        final boolean actual = sectionDao.isStationExist(upStationId);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("역이 구간에 등록되어 있지 않으면 false 를 반환한다.")
    void IsStationExist_NotInsertedLineId_FalseReturned() {
        // given
        final Section section = new Section(lineId, upStationId, downStationId, 10);
        sectionDao.insert(section);

        // when
        final boolean actual = sectionDao.isStationExist(999L);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("노선이 일치하고 상행 역이 동일한 구간을 조회한다.")
    void FindBy_SameUpStationId_SectionFound() {
        // given
        final Section section = new Section(lineId, upStationId, downStationId, 10);
        final Long sectionId = sectionDao.insert(section);

        final Section expected = new Section(sectionId, lineId, upStationId, downStationId, 10);

        // when
        final Optional<Section> actual = sectionDao.findBy(lineId, upStationId, 999L);

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선이 일치하고 하행 역이 동일한 구간을 조회한다.")
    void FindBy_SameDownStationId_SectionFound() {
        // given
        final Section section = new Section(lineId, upStationId, downStationId, 10);
        final Long sectionId = sectionDao.insert(section);

        final Section expected = new Section(sectionId, lineId, upStationId, downStationId, 10);

        // when
        final Optional<Section> actual = sectionDao.findBy(lineId, 999L, downStationId);

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선이 일치하고 상행이나 하행 역이 동일한 구간이 존재하지 않으면 빈 Optional 을 반환한다.")
    void FindBy_NewDownStation_EmptyOptionalReturned() {
        // given
        final Section section = new Section(lineId, upStationId, downStationId, 10);
        sectionDao.insert(section);

        // when
        final Optional<Section> actual = sectionDao.findBy(lineId, downStationId, 999L);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("노선이 일치하고 상행이나 하행 역이 동일한 구간이 존재하지 않으면 빈 Optional 을 반환한다.")
    void FindBy_NewUpStation_EmptyOptionalReturned() {
        // given
        final Section section = new Section(lineId, upStationId, downStationId, 10);
        sectionDao.insert(section);

        // when
        final Optional<Section> actual = sectionDao.findBy(lineId, 999L, upStationId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("노선과 상행 종점이 일치하는 역을 조회한다.")
    void FindByLineIdAndUpStationId() {
        // given
        final Section section = new Section(lineId, upStationId, downStationId, 10);
        final Long id = sectionDao.insert(section);

        final Section expected = new Section(id, lineId, upStationId, downStationId, 10);

        // when
        final Optional<Section> actual = sectionDao.findByLineIdAndUpStationId(lineId, upStationId);

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선과 하행 종점이 일치하는 역을 조회한다.")
    void FindByLineIdAndDownStationId() {
        // given
        final Section section = new Section(lineId, upStationId, downStationId, 10);
        final Long id = sectionDao.insert(section);

        final Section expected = new Section(id, lineId, upStationId, downStationId, 10);

        // when
        final Optional<Section> actual = sectionDao.findByLineIdAndDownStationId(lineId, downStationId);

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선과 중간역이 일치하는 모든 구간을 조회한다.")
    void FindByLineIdAndStationId_MiddleStation_SizeTwo() {
        // given
        final Long middleStationId = stationDao.insert(new Station("가운데역"))
                .orElseThrow()
                .getId();
        final Section section1 = new Section(lineId, upStationId, middleStationId, 10);
        sectionDao.insert(section1);

        final Section section2 = new Section(lineId, middleStationId, downStationId, 7);
        sectionDao.insert(section2);

        // when
        final List<Section> actual = sectionDao.findByLineIdAndStationId(lineId, middleStationId);

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    @DisplayName("노선과 상행 역이 일치하는 모든 구간을 조회한다.")
    void FindByLineIdAndStationId_UpStation_SizeOne() {
        // given
        final Long middleStationId = stationDao.insert(new Station("가운데역"))
                .orElseThrow()
                .getId();
        final Section section1 = new Section(lineId, upStationId, middleStationId, 10);
        sectionDao.insert(section1);

        final Section section2 = new Section(lineId, middleStationId, downStationId, 7);
        sectionDao.insert(section2);

        // when
        final List<Section> actual = sectionDao.findByLineIdAndStationId(lineId, upStationId);

        // then
        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("노선과 하행 역이 일치하는 모든 구간을 조회한다.")
    void FindByLineIdAndStationId_DownStation_SizeOne() {
        // given
        final Long middleStationId = stationDao.insert(new Station("가운데역"))
                .orElseThrow()
                .getId();
        final Section section1 = new Section(lineId, upStationId, middleStationId, 10);
        sectionDao.insert(section1);

        final Section section2 = new Section(lineId, middleStationId, downStationId, 7);
        sectionDao.insert(section2);

        // when
        final List<Section> actual = sectionDao.findByLineIdAndStationId(lineId, downStationId);

        // then
        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("id에 해당하는 노선을 삭제한다.")
    void DeleteById() {
        // given
        final Section section = new Section(lineId, upStationId, downStationId, 10);
        final Long id = sectionDao.insert(section);

        // when
        final Integer actual = sectionDao.deleteById(id);

        // then
        assertThat(actual).isOne();
    }
}
