package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.SectionDomain;
import wooteco.subway.domain.SectionsDomain;
import wooteco.subway.domain.Station;

class JdbcSectionDomainDaoTest extends DaoTest {

    private Station upStation;
    private Station downStation;
    private Line line;
    private SectionDomain section;
    private Distance distance;

    @BeforeEach
    void setUpData() {
        upStation = stationDao.insert(new Station("선릉역"))
                .orElseThrow();
        downStation = stationDao.insert(new Station("역삼역"))
                .orElseThrow();
        line = lineDao.insert(new Line("2호선", "green"))
                .orElseThrow();
        distance = new Distance(10);
        section = new SectionDomain(line, upStation, downStation, distance);
    }

    private SectionDomain toSectionWithId(final Long id, final SectionDomain sectionDomain) {
        return new SectionDomain(
                id,
                sectionDomain.getLine(),
                sectionDomain.getUpStation(),
                sectionDomain.getDownStation(),
                new Distance(sectionDomain.getDistance())
        );
    }

    @Test
    @DisplayName("객체를 DB에 저장한다.")
    void Insert_DomainObject_Success() {
        // when
        final Long id = sectionDomainDao.insert(section);

        // then
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("상행 역을 포함하는 구간이 존재하면 true 를 반환한다.")
    void ExistStation_UpStationId_ReturnedTrue() {
        // given
        sectionDomainDao.insert(section);

        // when
        final boolean actual = sectionDomainDao.existStation(upStation.getId());

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("하행 역을 포함하는 구간이 존재하면 true 를 반환한다.")
    void ExistStation_DownStationId_ReturnedTrue() {
        // given
        sectionDomainDao.insert(section);

        // when
        final boolean actual = sectionDomainDao.existStation(downStation.getId());

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("역 아이디에 해당하는 구간이 존재하면 false 를 반환한다.")
    void ExistStation_InvalidStationId_ReturnedFalse() {
        // when
        final boolean actual = sectionDomainDao.existStation(999L);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("역 아이디에 해당하는 모든 구간을 조회한다.")
    void FindAllByLineId() {
        // given
        final Long sectionId = sectionDomainDao.insert(section);
        final Station endDownStation = stationDao.insert(new Station("홍대입구역"))
                .orElseThrow();
        final SectionDomain newSection = new SectionDomain(
                line,
                downStation,
                endDownStation,
                distance
        );
        final Long newSectionId = sectionDomainDao.insert(newSection);

        final SectionsDomain expected = new SectionsDomain(
                List.of(toSectionWithId(sectionId, section), toSectionWithId(newSectionId, newSection))
        );

        // when
        final SectionsDomain actual = sectionDomainDao.findAllByLineId(line.getId());

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("노선 아이디가 일치하고 상행 역 아이디가 일치하는 구간을 조회한다.")
    void FindBy_MatchLineIdAndUpStationId_Success() {
        // given
        final Long id = sectionDomainDao.insert(section);
        final SectionDomain expected = toSectionWithId(id, section);

        // when
        final Optional<SectionDomain> actual = sectionDomainDao.findBy(line.getId(), 999L,
                downStation.getId());

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선 아이디가 일치하고 하행 역 아이디가 일치하는 구간을 조회한다.")
    void FindBy_MatchLineIdAndDownStationId_Success() {
        // given
        final Long id = sectionDomainDao.insert(section);
        final SectionDomain expected = toSectionWithId(id, section);

        // when
        final Optional<SectionDomain> actual = sectionDomainDao.findBy(line.getId(), 999L,
                downStation.getId());

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선 아이디가 일치하고 하행 역이나 상행 역 아이디가 일치하는 구간이 존재하지 않으면 빈 Optional 을 반환한다.")
    void FindBy_NotMatchStationId_EmptyOptionalReturned() {
        // given
        sectionDomainDao.insert(section);

        // when
        final Optional<SectionDomain> actual = sectionDomainDao.findBy(line.getId(), 999L,
                888L);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("노선과 상행 역이 일치하는 구간을 조회한다.")
    void FindByLIneIdAndUpStationId() {
        // given
        final Long id = sectionDomainDao.insert(section);
        final SectionDomain expected = toSectionWithId(id, section);

        // when
        final Optional<SectionDomain> actual = sectionDomainDao.findByLineIdAndUpStationId(line.getId(),
                upStation.getId());

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선과 상행 역이 일치하는 구간이 존재하지 않으면 빈 Optional 을 반환한다.")
    void FindByLIneIdAndUpStationId_NotMatchId_EmptyOptionalReturned() {
        // given
        sectionDomainDao.insert(section);

        // when
        final Optional<SectionDomain> actual = sectionDomainDao.findByLineIdAndUpStationId(line.getId(),
                999L);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("노선과 하행 역이 일치하는 구간을 조회한다.")
    void FindByLIneIdAndDownStationId() {
        // given
        final Long id = sectionDomainDao.insert(section);
        final SectionDomain expected = toSectionWithId(id, section);

        // when
        final Optional<SectionDomain> actual = sectionDomainDao.findByLineIdAndDownStationId(line.getId(),
                downStation.getId());

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("노선과 하행 역이 일치하는 구간이 존재하지 않으면 빈 Optional 을 반환한다.")
    void FindByLIneIdAndDownStationId_NotMatchId_EmptyOptionalReturned() {
        // given
        sectionDomainDao.insert(section);

        // when
        final Optional<SectionDomain> actual = sectionDomainDao.findByLineIdAndDownStationId(line.getId(),
                999L);

        // then
        assertThat(actual).isEmpty();
    }
}