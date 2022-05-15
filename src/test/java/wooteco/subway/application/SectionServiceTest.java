package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.application.exception.DuplicateSectionException;
import wooteco.subway.application.exception.NotFoundLineException;
import wooteco.subway.application.exception.NotFoundStationException;
import wooteco.subway.application.exception.UnaddableSectionException;
import wooteco.subway.application.exception.UndeletableSectionException;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionEdge;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.exception.UnsplittableException;
import wooteco.subway.dto.AddSectionRequest;
import wooteco.subway.dto.DeleteSectionRequest;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@SpringBootTest
@Transactional
public class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @DisplayName("존재하지 않는 노선에 구간 추가 시 예외 발생")
    @Test
    void addSectionToNotFoundLine() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));

        assertThatThrownBy(() -> sectionService
            .addSection(1L, new AddSectionRequest(upStation.getId(), downStation.getId(), 10))
        ).isInstanceOf(NotFoundLineException.class);
    }

    @DisplayName("존재하지 않는 상행, 하행역으로 구간 추가 시 예외 발생")
    @Test
    void addSectionWithNotFoundUpAndDownStation() {
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));

        assertThatThrownBy(() -> sectionService
            .addSection(line.getId(), new AddSectionRequest(1L, 2L, 10))
        ).isInstanceOf(NotFoundStationException.class);
    }

    @DisplayName("상행 종점에 새로운 구간 추가")
    @Test
    void addSectionToLastUpStation() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));
        Station newStation = stationRepository.save(new Station("선릉역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        Section section1 = sectionRepository.save(new Section(line.getId(),
            new SectionEdge(upStation.getId(), downStation.getId(), 10)));

        Section section2 = sectionService.addSection(line.getId(),
            new AddSectionRequest(newStation.getId(), upStation.getId(), 8));

        List<Section> sections = sectionRepository.findAllByLineId(line.getId());
        assertThat(sections).containsExactlyInAnyOrder(section1, section2);
    }

    @DisplayName("하행 종점에 새로운 구간 추가")
    @Test
    void addSectionToLastDownStation() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));
        Station newStation = stationRepository.save(new Station("선릉역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        Section section = sectionRepository.save(new Section(line.getId(),
            new SectionEdge(upStation.getId(), downStation.getId(), 10)));

        Section newSection = sectionService.addSection(line.getId(),
            new AddSectionRequest(downStation.getId(), newStation.getId(), 8));

        List<Section> sections = sectionRepository.findAllByLineId(line.getId());
        assertThat(sections).containsExactlyInAnyOrder(section, newSection);
    }

    @DisplayName("중복된 구간 추가 시 예외 발생")
    @Test
    void addSectionWithDuplicateStations() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        Section section = sectionRepository.save(new Section(line.getId(),
            new SectionEdge(upStation.getId(), downStation.getId(), 10)));

        assertThatThrownBy(() -> sectionService.addSection(line.getId(),
            new AddSectionRequest(upStation.getId(), downStation.getId(), 8))
        ).isInstanceOf(DuplicateSectionException.class);

        List<Section> sections = sectionRepository.findAllByLineId(line.getId());
        assertThat(sections).containsExactly(section);
    }

    @DisplayName("기존 구간과 동일한 상행역을 가진 구간 추가")
    @Test
    void addSectionWithSameUpStation() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));
        Station newStation = stationRepository.save(new Station("선릉역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(upStation.getId(), downStation.getId(), 10)));

        sectionService.addSection(line.getId(),
            new AddSectionRequest(upStation.getId(), newStation.getId(), 8));

        List<SectionEdge> sectionEdges = sectionRepository.findAllByLineId(line.getId()).stream()
            .map(Section::getEdge)
            .collect(Collectors.toList());
        assertThat(sectionEdges).containsExactlyInAnyOrder(
            new SectionEdge(upStation.getId(), newStation.getId(), 8),
            new SectionEdge(newStation.getId(), downStation.getId(), 2));
    }

    @DisplayName("기존 구간과 동일한 하행역을 가진 구간 추가")
    @Test
    void addSectionWithSameDownStation() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));
        Station newStation = stationRepository.save(new Station("선릉역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(upStation.getId(), downStation.getId(), 10)));

        sectionService.addSection(line.getId(),
            new AddSectionRequest(newStation.getId(), downStation.getId(), 8));

        List<SectionEdge> sectionEdges = sectionRepository.findAllByLineId(line.getId()).stream()
            .map(Section::getEdge)
            .collect(Collectors.toList());
        assertThat(sectionEdges).containsExactlyInAnyOrder(
            new SectionEdge(upStation.getId(), newStation.getId(), 2),
            new SectionEdge(newStation.getId(), downStation.getId(), 8));
    }

    @DisplayName("기존 구간과 동일한 거리를 가진 구간 추가 시 예외 발생")
    @Test
    void addSectionWithSameDistance() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));
        Station newStation = stationRepository.save(new Station("선릉역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(upStation.getId(), downStation.getId(), 10)));

        assertThatThrownBy(() -> sectionService.addSection(line.getId(),
            new AddSectionRequest(newStation.getId(), downStation.getId(), 10)))
            .isInstanceOf(UnsplittableException.class);

        List<SectionEdge> sectionEdges = sectionRepository.findAllByLineId(line.getId()).stream()
            .map(Section::getEdge)
            .collect(Collectors.toList());
        assertThat(sectionEdges)
            .containsExactlyInAnyOrder(new SectionEdge(upStation.getId(), downStation.getId(), 10));
    }

    @DisplayName("구간에 추가되지 않은 역을 상행, 하행역으로 추가 시 예외 발생")
    @Test
    void addSectionWithNotFoundUpStation() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));
        Station notFoundStation1 = stationRepository.save(new Station("선릉역"));
        Station notFoundStation2 = stationRepository.save(new Station("잠실역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(upStation.getId(), downStation.getId(), 10)));

        assertThatThrownBy(() -> sectionService.addSection(line.getId(),
            new AddSectionRequest(notFoundStation1.getId(), notFoundStation2.getId(), 3)))
            .isInstanceOf(UnaddableSectionException.class);

        List<SectionEdge> sectionEdges = sectionRepository.findAllByLineId(line.getId()).stream()
            .map(Section::getEdge)
            .collect(Collectors.toList());
        assertThat(sectionEdges)
            .containsExactlyInAnyOrder(new SectionEdge(upStation.getId(), downStation.getId(), 10));
    }

    @DisplayName("존재하지 않는 노선에 구간 삭제 시 예외 발생")
    @Test
    void deleteSectionToNotFoundLine() {
        Station upStation = stationRepository.save(new Station("강남역"));

        assertThatThrownBy(
            () -> sectionService.deleteSection(1L, new DeleteSectionRequest(upStation.getId()))
        ).isInstanceOf(NotFoundLineException.class);
    }

    @DisplayName("존재하지 않는 역으로 구간 삭제 시 예외 발생")
    @Test
    void deleteSectionWithNotFoundUpAndDownStation() {
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));

        assertThatThrownBy(
            () -> sectionService.deleteSection(line.getId(), new DeleteSectionRequest(1L))
        ).isInstanceOf(NotFoundStationException.class);
    }

    @DisplayName("구간에 추가되지 않은 역을 삭제 시 예외 발생")
    @Test
    void deleteSectionWithNotFoundUpStation() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));
        Station notFoundStation = stationRepository.save(new Station("선릉역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(upStation.getId(), downStation.getId(), 10)));

        assertThatThrownBy(() -> sectionService
            .deleteSection(line.getId(), new DeleteSectionRequest(notFoundStation.getId())))
            .isInstanceOf(UndeletableSectionException.class);

        List<SectionEdge> sectionEdges = sectionRepository.findAllByLineId(line.getId()).stream()
            .map(Section::getEdge)
            .collect(Collectors.toList());
        assertThat(sectionEdges)
            .containsExactlyInAnyOrder(new SectionEdge(upStation.getId(), downStation.getId(), 10));
    }

    @DisplayName("구간이 하나인 노선에서 구간 삭제 시 예외 발생")
    @Test
    void deleteOnlyOneSection() {
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("역삼역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(upStation.getId(), downStation.getId(), 10)));

        assertThatThrownBy(() -> sectionService
            .deleteSection(line.getId(), new DeleteSectionRequest(upStation.getId())))
            .isInstanceOf(UndeletableSectionException.class);

        List<SectionEdge> sectionEdges = sectionRepository.findAllByLineId(line.getId()).stream()
            .map(Section::getEdge)
            .collect(Collectors.toList());
        assertThat(sectionEdges)
            .containsExactlyInAnyOrder(new SectionEdge(upStation.getId(), downStation.getId(), 10));
    }

    @DisplayName("상행 종점 구간 제거")
    @Test
    void deleteLastUpStationSection() {
        Station station1 = stationRepository.save(new Station("강남역"));
        Station station2 = stationRepository.save(new Station("역삼역"));
        Station station3 = stationRepository.save(new Station("잠실역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(station1.getId(), station2.getId(), 10)));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(station2.getId(), station3.getId(), 5)));

        sectionService.deleteSection(line.getId(), new DeleteSectionRequest(station1.getId()));

        List<SectionEdge> sectionEdges = sectionRepository.findAllByLineId(line.getId()).stream()
            .map(Section::getEdge)
            .collect(Collectors.toList());
        assertThat(sectionEdges)
            .containsExactlyInAnyOrder(new SectionEdge(station2.getId(), station3.getId(), 5));
    }

    @DisplayName("하행 종점 구간 제거")
    @Test
    void deleteLastDownStationSection() {
        Station station1 = stationRepository.save(new Station("강남역"));
        Station station2 = stationRepository.save(new Station("역삼역"));
        Station station3 = stationRepository.save(new Station("잠실역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(station1.getId(), station2.getId(), 10)));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(station2.getId(), station3.getId(), 5)));

        sectionService.deleteSection(line.getId(), new DeleteSectionRequest(station3.getId()));

        List<SectionEdge> sectionEdges = sectionRepository.findAllByLineId(line.getId()).stream()
            .map(Section::getEdge)
            .collect(Collectors.toList());
        assertThat(sectionEdges)
            .containsExactlyInAnyOrder(new SectionEdge(station1.getId(), station2.getId(), 10));
    }

    @DisplayName("중간 구간 제거")
    @Test
    void deleteBetweenSection() {
        Station station1 = stationRepository.save(new Station("강남역"));
        Station station2 = stationRepository.save(new Station("역삼역"));
        Station station3 = stationRepository.save(new Station("잠실역"));
        Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(station1.getId(), station2.getId(), 10)));
        sectionRepository.save(new Section(line.getId(),
            new SectionEdge(station2.getId(), station3.getId(), 5)));

        sectionService.deleteSection(line.getId(), new DeleteSectionRequest(station2.getId()));

        List<SectionEdge> sectionEdges = sectionRepository.findAllByLineId(line.getId()).stream()
            .map(Section::getEdge)
            .collect(Collectors.toList());
        assertThat(sectionEdges)
            .containsExactlyInAnyOrder(new SectionEdge(station1.getId(), station3.getId(), 15));
    }
}
