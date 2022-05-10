package wooteco.subway.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@SpringBootTest
public class SectionServiceTest {
    @Autowired
    private SectionService sectionService;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void before() {
        Station first = stationDao.save(new Station("강남역"));
        Station second = stationDao.save(new Station("역삼역"));
        sectionDao.save(new Section(1L, first.getId(), second.getId(), 10));
    }

    @Test
    @DisplayName("상행역과 하행역이 동일한 경우 예외처리")
    void sameStation() {
        Assertions.assertThatThrownBy(
                        () -> sectionService.create(new Section(1L, 1L, 1L, 10))
                ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역은 동일할 수 없습니다.");

    }

    @Test
    @DisplayName("등록된 구간을 중복으로 등록하면 예외처리")
    void duplicateSection() {
        Assertions.assertThatThrownBy(
                        () -> sectionService.create(new Section(1L, 1L, 2L, 10))
                ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 등록된 구간입니다.");

    }

    @Test
    @DisplayName("입력한 역이 존재하지 않는다면 예외처리")
    void noExistStation() {
        Assertions.assertThatThrownBy(
                        () -> sectionService.create(new Section(1L, 3L, 4L, 10))
                ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("적어도 한 개의 역이 노선 구간 내에 존재해야 합니다.");
    }
}
