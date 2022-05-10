package wooteco.subway.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.mock.MemorySectionDao;
import wooteco.subway.mock.MemoryStationDao;

public class SectionServiceTest {
    private static final long LINE_ID = 1L;

    private MemoryStationDao stationDao = new MemoryStationDao();
    private MemorySectionDao sectionDao = new MemorySectionDao();

    private SectionService sectionService = new SectionService(sectionDao, stationDao);

    @BeforeEach
    void before() {
        stationDao.clear();
        sectionDao.clear();
    }

    @Test
    @DisplayName("상행역과 하행역이 동일한 경우 예외처리")
    void sameStation() {
        Station first = stationDao.save(new Station("강남역"));
        Station second = stationDao.save(new Station("역삼역"));
        Station third = stationDao.save(new Station("선릉역"));
        sectionDao.save(new Section(LINE_ID, first.getId(), third.getId(), 10));

        Assertions.assertThatThrownBy(
                        () -> sectionService.create(new Section(LINE_ID, first.getId(), first.getId(), 10))
                ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 일치합니다.");

    }

    @Test
    @DisplayName("등록된 구간을 중복으로 등록하면 예외처리")
    void duplicateSection() {
        Station first = stationDao.save(new Station("강남역"));
        Station second = stationDao.save(new Station("역삼역"));
        Station third = stationDao.save(new Station("선릉역"));
        sectionDao.save(new Section(LINE_ID, first.getId(), third.getId(), 10));

        Assertions.assertThatThrownBy(
                        () -> sectionService.create(new Section(LINE_ID, first.getId(), third.getId(), 10))
                ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 등록된 구간입니다.");

    }

    @Test
    @DisplayName("입력한 역이 존재하지 않는다면 예외처리")
    void noExistStation() {
        Station first = stationDao.save(new Station("강남역"));
        Station second = stationDao.save(new Station("역삼역"));
        Station third = stationDao.save(new Station("선릉역"));
        sectionDao.save(new Section(LINE_ID, first.getId(), third.getId(), 10));

        Assertions.assertThatThrownBy(
                        () -> sectionService.create(new Section(LINE_ID, 4L, 5L, 10))
                ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("적어도 한 개의 역이 노선 구간 내에 존재해야 합니다.");
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void add() {
        Station first = stationDao.save(new Station("강남역"));
        Station second = stationDao.save(new Station("역삼역"));
        Station third = stationDao.save(new Station("선릉역"));
        sectionDao.save(new Section(LINE_ID, first.getId(), third.getId(), 10));

        sectionService.create(new Section(LINE_ID, second.getId(), third.getId(), 10));
    }

    @Test
    @DisplayName("입력한 구간이 기존 구간보다 길면 예외처리")
    void longSection() {
        Station first = stationDao.save(new Station("강남역"));
        Station second = stationDao.save(new Station("역삼역"));
        Station third = stationDao.save(new Station("선릉역"));
        sectionDao.save(new Section(LINE_ID, first.getId(), third.getId(), 10));

        Assertions.assertThatThrownBy(
                        () -> sectionService.create(new Section(LINE_ID, second.getId(), third.getId(), 11))
                ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간보다 긴 역을 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("종점을 연장할 수 있다.")
    void addExtension() {
        Station first = stationDao.save(new Station("강남역"));
        Station second = stationDao.save(new Station("역삼역"));
        Station third = stationDao.save(new Station("선릉역"));
        sectionDao.save(new Section(LINE_ID, first.getId(), third.getId(), 10));

        sectionService.create(new Section(LINE_ID, third.getId(), second.getId(), 100));
    }
}
