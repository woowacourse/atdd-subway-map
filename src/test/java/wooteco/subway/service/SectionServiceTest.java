package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    Line savedLine;

    Station upStation;

    Station downStation;

    Section section;

    @BeforeEach
    void setup() {
        savedLine = lineDao.save(new Line("5호선", "bg-purple-600"));
        upStation = stationDao.save(new Station("아차산역"));
        downStation = stationDao.save(new Station("군자역"));
        section = sectionDao.save(new Section(upStation, downStation, 10, savedLine.getId()));
    }

    @DisplayName("(갈래길이 아닌 경우) 특정 노선에 구간을 추가한다.")
    @Test
    void addNotBranchedSection() {
        final Station newStation = stationDao.save(new Station("마장역"));
        final Section section = new Section(downStation, newStation, 10, savedLine.getId());
        final Section savedSection = sectionService.addSection(savedLine.getId(), section);

        assertThat(savedSection).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(section);
    }

    @DisplayName("(갈래길인 경우) 특정 노선에 구간을 추가한다.")
    @Test
    void addBranchedSection() {
        final Station newStation = stationDao.save(new Station("마장역"));
        final Section newSection = new Section(newStation, downStation, 9, savedLine.getId());
        final Section savedSection = sectionService.addSection(savedLine.getId(), newSection);

        final Optional<Section> foundSection = sectionDao.findAllByLineId(savedLine.getId())
                .stream()
                .filter(it -> it.getUpStation().equals(section.getUpStation()))
                .findAny();
        assert (foundSection.isPresent());

        assertAll(
                () -> assertThat(savedSection).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(newSection),
                () -> assertThat(foundSection.get()).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(new Section(upStation, newStation, 1, savedLine.getId()))
        );
    }

    @DisplayName("특정 노선에 구간을 삭제한다.")
    @Test
    void delete() {
        final Station newStation = stationDao.save(new Station("마장역"));
        sectionDao.save(new Section(downStation, newStation, 10, savedLine.getId()));

        sectionService.delete(savedLine.getId(), downStation.getId());

        final List<Section> list = sectionDao.findAllByLineId(savedLine.getId());
        // 아차산 - 마장
        Optional<Section> foundSection = list.stream()
                .filter(section -> section.getDownStation().equals(newStation))
                .findAny();

        assert (foundSection.isPresent());

        assertThat(foundSection.get()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new Section(upStation, newStation, 20, savedLine.getId()));
    }
}
