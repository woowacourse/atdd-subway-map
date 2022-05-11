package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.SectionRequest;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 관련 service 테스트")
@JdbcTest
class SectionServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionService sectionService;
    private SectionDao sectionDao;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);

        sectionService = new SectionService(sectionDao, stationDao);
    }

    @DisplayName("구간 생성 시 상행역에 해당하는 지하철역이 존재하지 경우 예외가 발생한다.")
    @Test
    void saveNotExistUpStation() {
        // when & then
        assertThatThrownBy(
                () -> sectionService.save(1L, new SectionRequest(1L, 2L, 10))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역이 존재하지 않습니다.");
    }

    @DisplayName("구간 생성 시 하행역에 해당하는 지하철역이 존재하지 경우 예외가 발생한다.")
    @Test
    void saveNotExistDownStation() {
        // given
        long stationId = stationDao.save(new Station(1L, "강남역"));

        // when & then
        assertThatThrownBy(
                () -> sectionService.save(1L, new SectionRequest(stationId, 2L, 10))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행역이 존재하지 않습니다.");
    }

    @DisplayName("구간 생성 시 상행역과 하행역이 이미 지하철 노선에 존재하면 예외가 발생한다.")
    @Test
    void saveAlreadyExistAllSection() {
        // given
        long station1Id = stationDao.save(new Station(1L, "강남역"));
        long station2Id = stationDao.save(new Station(2L, "역삼역"));

        sectionDao.save(1L, new Section(station1Id, station2Id, 10));

        // when & then
        assertThatThrownBy(
                () -> sectionService.save(1L, new SectionRequest(station1Id, station2Id, 10))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 지하철 노선에 존재합니다.");
    }

    @DisplayName("구간 생성 시 상행역 또는 하행역 중 하나만 지하철 노선에 포함되어 있으면 등록 가능하다.")
    @Test
    void saveExistOneStation() {
        // given
        long station1Id = stationDao.save(new Station(1L, "강남역"));
        long station2Id = stationDao.save(new Station(2L, "역삼역"));
        long station3Id = stationDao.save(new Station(3L, "삼성역"));

        sectionDao.save(1L, new Section(station1Id, station2Id, 10));

        // when & then
        assertThatCode(
                () -> sectionService.save(1L, new SectionRequest(station1Id, station3Id, 5)))
                .doesNotThrowAnyException();
    }

    @DisplayName("구간 생성 시 상행 종점을 등록한다.")
    @Test
    void saveNewUpStation() {
        // given
        long station1Id = stationDao.save(new Station(1L, "강남역"));
        long station2Id = stationDao.save(new Station(2L, "역삼역"));
        long station3Id = stationDao.save(new Station(3L, "삼성역"));

        sectionDao.save(1L, new Section(station2Id, station3Id, 10));

        // when & then
        assertThatCode(
                () -> sectionService.save(1L, new SectionRequest(station1Id, station2Id, 10))
        ).doesNotThrowAnyException();
    }

    @DisplayName("구간 생성 시 하행 종점을 등록한다.")
    @Test
    void saveNewDownStation() {
        // given
        long station1Id = stationDao.save(new Station(1L, "강남역"));
        long station2Id = stationDao.save(new Station(2L, "역삼역"));
        long station3Id = stationDao.save(new Station(3L, "삼성역"));

        sectionDao.save(1L, new Section(station1Id, station2Id, 10));

        // when & then
        assertThatCode(
                () -> sectionService.save(1L, new SectionRequest(station2Id, station3Id, 10))
        ).doesNotThrowAnyException();
    }

    @DisplayName("구간 생성 시 상행역을 등록한다.")
    @Test
    void saveUpStation() {
        // given
        long station1Id = stationDao.save(new Station(1L, "강남역"));
        long station2Id = stationDao.save(new Station(2L, "역삼역"));
        long station3Id = stationDao.save(new Station(3L, "삼성역"));

        sectionDao.save(1L, new Section(station1Id, station2Id, 10));

        // when & then
        assertThatCode(
                () -> sectionService.save(1L, new SectionRequest(station3Id, station1Id, 5))
        ).doesNotThrowAnyException();
    }

    @DisplayName("구간 생성 시 하행역을 등록한다.")
    @Test
    void saveDownStation() {
        // given
        long station1Id = stationDao.save(new Station(1L, "강남역"));
        long station2Id = stationDao.save(new Station(2L, "역삼역"));
        long station3Id = stationDao.save(new Station(3L, "삼성역"));

        sectionDao.save(1L, new Section(station1Id, station2Id, 10));

        // when & then
        assertThatCode(
                () -> sectionService.save(1L, new SectionRequest(station1Id, station3Id, 5))
        ).doesNotThrowAnyException();
    }

    @DisplayName("구간 생성 시 다른 구간과 연결되어 있지 않으면 예외가 발생한다.")
    @Test
    void saveNotExistAnyStation() {
        // given
        long station1Id = stationDao.save(new Station(1L, "강남역"));
        long station2Id = stationDao.save(new Station(2L, "역삼역"));

        // when & then
        assertThatThrownBy(
                () -> sectionService.save(1L, new SectionRequest(station1Id, station2Id, 10))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가하려는 구간이 노선에 포함되어 있지 않습니다.");
    }
}
