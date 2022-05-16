package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

class SectionDaoTest extends DaoTest {

    private Line line;
    private Station redUpStation;
    private Station redDownStation;
    private Section redSection;
    private Distance redDistance;
    private Station blueUpStation;
    private Station blueDownStation;
    private Section blueSection;
    private Distance blueDistance;

    @BeforeEach
    void setUpData() {
        line = lineDao.insert(new Line("2호선", "green"))
                .orElseThrow();

        redUpStation = stationDao.insert(new Station("red-up"))
                .orElseThrow();
        redDownStation = stationDao.insert(new Station("red-down"))
                .orElseThrow();
        redDistance = new Distance(10);
        redSection = new Section(line, redUpStation, redDownStation, redDistance);

        blueUpStation = stationDao.insert(new Station("blue-up"))
                .orElseThrow();
        blueDownStation = stationDao.insert(new Station("blue-down"))
                .orElseThrow();
        blueDistance = new Distance(7);
        blueSection = new Section(line, blueUpStation, blueDownStation, blueDistance);

    }

    private Section toSectionWithId(final Long id, final Section section) {
        return new Section(
                id,
                section.getLine(),
                section.getUpStation(),
                section.getDownStation(),
                new Distance(section.getDistance())
        );
    }

    @Test
    @DisplayName("객체를 DB에 저장한다.")
    void Insert_DomainObject_Success() {
        // when
        final Long id = sectionDao.insert(redSection);

        // then
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("상행 역을 포함하는 구간이 존재하면 true 를 반환한다.")
    void ExistStation_UpStationId_ReturnedTrue() {
        // given
        sectionDao.insert(redSection);

        // when
        final boolean actual = sectionDao.existStation(redUpStation.getId());

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("하행 역을 포함하는 구간이 존재하면 true 를 반환한다.")
    void ExistStation_DownStationId_ReturnedTrue() {
        // given
        sectionDao.insert(redSection);

        // when
        final boolean actual = sectionDao.existStation(redDownStation.getId());

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("역 아이디에 해당하는 구간이 존재하면 false 를 반환한다.")
    void ExistStation_InvalidStationId_ReturnedFalse() {
        // when
        final boolean actual = sectionDao.existStation(999L);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("역 아이디에 해당하는 모든 구간을 조회한다.")
    void FindAllByLineId() {
        // given
        final Long redSectionId = sectionDao.insert(redSection);
        final Long blueSectionId = sectionDao.insert(blueSection);

        final Sections expected = new Sections(
                List.of(toSectionWithId(redSectionId, redSection), toSectionWithId(blueSectionId, blueSection))
        );

        // when
        final Sections actual = sectionDao.findAllByLineId(line.getId());

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("노선 아이디가 일치하고 상행 역 아이디가 일치하는 구간을 조회한다.")
    void FindBy_MatchLineIdAndUpStationId_Success() {
        // given
        final Long id = sectionDao.insert(redSection);
        final Section expected = toSectionWithId(id, redSection);

        // when
        final Optional<Section> actual = sectionDao.findBy(line.getId(), 999L,
                redDownStation.getId());

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선 아이디가 일치하고 하행 역 아이디가 일치하는 구간을 조회한다.")
    void FindBy_MatchLineIdAndDownStationId_Success() {
        // given
        final Long id = sectionDao.insert(redSection);
        final Section expected = toSectionWithId(id, redSection);

        // when
        final Optional<Section> actual = sectionDao.findBy(line.getId(), 999L,
                redDownStation.getId());

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선 아이디가 일치하고 하행 역이나 상행 역 아이디가 일치하는 구간이 존재하지 않으면 빈 Optional 을 반환한다.")
    void FindBy_NotMatchStationId_EmptyOptionalReturned() {
        // given
        sectionDao.insert(redSection);

        // when
        final Optional<Section> actual = sectionDao.findBy(line.getId(), 999L,
                888L);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("노선과 상행 역이 일치하는 구간을 조회한다.")
    void FindByLIneIdAndUpStationId() {
        // given
        final Long id = sectionDao.insert(redSection);
        final Section expected = toSectionWithId(id, redSection);

        // when
        final Optional<Section> actual = sectionDao.findByLineIdAndUpStationId(line.getId(),
                redUpStation.getId());

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선과 상행 역이 일치하는 구간이 존재하지 않으면 빈 Optional 을 반환한다.")
    void FindByLIneIdAndUpStationId_NotMatchId_EmptyOptionalReturned() {
        // given
        sectionDao.insert(redSection);

        // when
        final Optional<Section> actual = sectionDao.findByLineIdAndUpStationId(line.getId(),
                999L);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("노선과 하행 역이 일치하는 구간을 조회한다.")
    void FindByLIneIdAndDownStationId() {
        // given
        final Long id = sectionDao.insert(redSection);
        final Section expected = toSectionWithId(id, redSection);

        // when
        final Optional<Section> actual = sectionDao.findByLineIdAndDownStationId(line.getId(),
                redDownStation.getId());

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선과 하행 역이 일치하는 구간이 존재하지 않으면 빈 Optional 을 반환한다.")
    void FindByLIneIdAndDownStationId_NotMatchId_EmptyOptionalReturned() {
        // given
        sectionDao.insert(redSection);

        // when
        final Optional<Section> actual = sectionDao.findByLineIdAndDownStationId(line.getId(),
                999L);

        // then
        assertThat(actual).isEmpty();
    }
}