package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.service.dto.request.SectionRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("delete from section");
        jdbcTemplate.update("delete from line");
        jdbcTemplate.update("delete from station");
    }

    @Test
    @DisplayName("구간 사이에 역을 추가한다. 강남-광교 -> 강남-양재-광교")
    void addSection() {
        Station 강남 = stationRepository.save(new Station("강남"));
        Station 양재 = stationRepository.save(new Station("양재"));
        Station 광교 = stationRepository.save(new Station("광교"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        sectionRepository.save(new Section(신분당선, 강남, 광교, 10));

        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 양재.getId(), 4);
        lineService.createSection(신분당선.getId(), sectionRequest);

        List<Section> sections = sectionRepository.findSectionByLine(신분당선);
        Section 구간1 = findSection(강남, sections);
        assertThat(구간1.getDownStation()).isEqualTo(양재);
        assertThat(구간1.getDistance()).isEqualTo(4);

        Section 구간2 = findSection(양재, sections);
        assertThat(구간2.getDownStation()).isEqualTo(광교);
        assertThat(구간2.getDistance()).isEqualTo(6);
    }

    @Test
    @DisplayName("구간 사이에 앞역을 기준으로 추가한다. 강남-양재-광교 -> 강남-양재-판교-광교")
    void addSectionFrontStation() {
        Station 강남 = stationRepository.save(new Station("강남"));
        Station 양재 = stationRepository.save(new Station("양재"));
        Station 광교 = stationRepository.save(new Station("광교"));
        Station 판교 = stationRepository.save(new Station("판교"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        sectionRepository.save(new Section(신분당선, 강남, 광교, 10));
        SectionRequest sectionRequest1 = new SectionRequest(강남.getId(), 양재.getId(), 4);
        lineService.createSection(신분당선.getId(), sectionRequest1);

        SectionRequest sectionRequest2 = new SectionRequest(양재.getId(), 판교.getId(), 4);
        lineService.createSection(신분당선.getId(), sectionRequest2);

        List<Section> sections = sectionRepository.findSectionByLine(신분당선);
        Section 구간2 = findSection(양재, sections);
        Section 구간3 = findSection(판교, sections);
        assertThat(구간2.getDownStation()).isEqualTo(판교);
        assertThat(구간2.getDistance()).isEqualTo(4);
        assertThat(구간3.getDownStation()).isEqualTo(광교);
        assertThat(구간3.getDistance()).isEqualTo(2);
    }

    @Test
    @DisplayName("구간 사이에 뒷역을 기준으로 추가한다. 강남-양재-광교 -> 강남-양재-판교-광교")
    void addSectionBackStation() {
        Station 강남 = stationRepository.save(new Station("강남"));
        Station 양재 = stationRepository.save(new Station("양재"));
        Station 광교 = stationRepository.save(new Station("광교"));
        Station 판교 = stationRepository.save(new Station("판교"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        sectionRepository.save(new Section(신분당선, 강남, 광교, 10));
        SectionRequest sectionRequest1 = new SectionRequest(강남.getId(), 양재.getId(), 4);
        lineService.createSection(신분당선.getId(), sectionRequest1);

        SectionRequest sectionRequest2 = new SectionRequest(판교.getId(), 광교.getId(), 4);
        lineService.createSection(신분당선.getId(), sectionRequest2);

        List<Section> sections = sectionRepository.findSectionByLine(신분당선);
        Section 구간2 = findSection(양재, sections);
        Section 구간3 = findSection(판교, sections);
        assertThat(구간2.getDownStation()).isEqualTo(판교);
        assertThat(구간2.getDistance()).isEqualTo(2);
        assertThat(구간3.getDownStation()).isEqualTo(광교);
        assertThat(구간3.getDistance()).isEqualTo(4);
    }

    @Test
    @DisplayName("상행 종점을 변경한다. 양재-광교 -> 강남-양재-광교")
    void addUpStation() {
        Station 양재 = stationRepository.save(new Station("양재"));
        Station 광교 = stationRepository.save(new Station("광교"));
        Station 강남 = stationRepository.save(new Station("강남"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        sectionRepository.save(new Section(신분당선, 양재, 광교, 10));

        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 양재.getId(), 5);

        lineService.createSection(신분당선.getId(), sectionRequest);

        List<Section> sections = sectionRepository.findSectionByLine(신분당선);
        Section 구간1 = findSection(강남, sections);
        Section 구간2 = findSection(양재, sections);
        assertThat(구간1.getDownStation()).isEqualTo(양재);
        assertThat(구간1.getDistance()).isEqualTo(5);
        assertThat(구간2.getDownStation()).isEqualTo(광교);
        assertThat(구간2.getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("하행 종점을 변경한다. 강남-양재 -> 강남-양재-광교")
    void addDownStation() {
        Station 강남 = stationRepository.save(new Station("강남"));
        Station 양재 = stationRepository.save(new Station("양재"));
        Station 광교 = stationRepository.save(new Station("광교"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        sectionRepository.save(new Section(신분당선, 강남, 양재, 10));

        SectionRequest sectionRequest = new SectionRequest(양재.getId(), 광교.getId(), 5);
        lineService.createSection(신분당선.getId(), sectionRequest);

        List<Section> sections = sectionRepository.findSectionByLine(신분당선);
        Section 구간1 = findSection(강남, sections);
        Section 구간2 = findSection(양재, sections);
        assertThat(구간1.getDownStation()).isEqualTo(양재);
        assertThat(구간1.getDistance()).isEqualTo(10);
        assertThat(구간2.getDownStation()).isEqualTo(광교);
        assertThat(구간2.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("중간 역을 삭제한다. 강남-양재-광교 -> 강남-광교")
    void deleteSection() {
        Station 강남 = stationRepository.save(new Station("강남"));
        Station 양재 = stationRepository.save(new Station("양재"));
        Station 광교 = stationRepository.save(new Station("광교"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        sectionRepository.save(new Section(신분당선, 강남, 광교, 10));
        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 양재.getId(), 4);
        lineService.createSection(신분당선.getId(), sectionRequest);

        lineService.deleteSection(신분당선.getId(), 양재.getId());

        List<Section> sections = sectionRepository.findSectionByLine(신분당선);
        assertThat(sections).hasSize(1);
        assertThat(sections.get(0).getDistance()).isEqualTo(10);
        assertThat(sections.get(0).getUpStation()).isEqualTo(강남);
        assertThat(sections.get(0).getDownStation()).isEqualTo(광교);
    }

    @Test
    @DisplayName("상행 종점을 삭제한다. 강남-양재-광교 -> 양재-광교")
    void deleteUpSection() {
        Station 강남 = stationRepository.save(new Station("강남"));
        Station 양재 = stationRepository.save(new Station("양재"));
        Station 광교 = stationRepository.save(new Station("광교"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        sectionRepository.save(new Section(신분당선, 강남, 광교, 10));
        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 양재.getId(), 4);
        lineService.createSection(신분당선.getId(), sectionRequest);

        lineService.deleteSection(신분당선.getId(), 강남.getId());

        List<Section> sections = sectionRepository.findSectionByLine(신분당선);
        assertThat(sections).hasSize(1);
        assertThat(sections.get(0).getDistance()).isEqualTo(6);
    }

    @Test
    @DisplayName("하행 종점을 삭제한다. 강남-양재-광교 -> 강남-양재")
    void deleteDownSection() {
        Station 강남 = stationRepository.save(new Station("강남"));
        Station 양재 = stationRepository.save(new Station("양재"));
        Station 광교 = stationRepository.save(new Station("광교"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        sectionRepository.save(new Section(신분당선, 강남, 광교, 10));
        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 양재.getId(), 4);
        lineService.createSection(신분당선.getId(), sectionRequest);

        lineService.deleteSection(신분당선.getId(), 광교.getId());

        List<Section> sections = sectionRepository.findSectionByLine(신분당선);
        assertThat(sections).hasSize(1);
        assertThat(sections.get(0).getDistance()).isEqualTo(4);
    }

    @Test
    @DisplayName("라인에 없는 역을 구간으로 추가할 경우 예외를 발생한다.")
    void addSectionNotFindStation() {
        Station 강남 = stationRepository.save(new Station("강남"));
        Station 양재 = stationRepository.save(new Station("양재"));
        Station 광교 = stationRepository.save(new Station("광교"));
        Station 창동 = stationRepository.save(new Station("창동"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        sectionRepository.save(new Section(신분당선, 강남, 광교, 10));

        SectionRequest sectionRequest = new SectionRequest(양재.getId(), 창동.getId(), 4);

        assertThatThrownBy(() -> lineService.createSection(신분당선.getId(), sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("생성할 수 없는 구간입니다.");
    }

    @Test
    @DisplayName("라인에 둘다 존재하는 역을 구간으로 추가할 경우 예외를 발생한다.")
    void addSectionDuplicateStation() {
        Station 강남 = stationRepository.save(new Station("강남"));
        Station 광교 = stationRepository.save(new Station("광교"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        sectionRepository.save(new Section(신분당선, 강남, 광교, 10));

        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 광교.getId(), 4);

        assertThatThrownBy(() -> lineService.createSection(신분당선.getId(), sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("기존에 존재하는 구간입니다.");
    }

    @Test
    @DisplayName("기존 구간보다 같거나 긴 거리의 구간으로 추가시 예외를 발생한다.")
    void addSectionOverDistance() {
        Station 강남 = stationRepository.save(new Station("강남"));
        Station 양재 = stationRepository.save(new Station("양재"));
        Station 광교 = stationRepository.save(new Station("광교"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        sectionRepository.save(new Section(신분당선, 강남, 광교, 10));

        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 양재.getId(), 10);

        assertThatThrownBy(() -> lineService.createSection(신분당선.getId(), sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("추가하려는 거리가 큽니다.");
    }

    private Section findSection(Station station, List<Section> sections) {
        return sections.stream()
            .filter(entity -> entity.isEqualToUpStation(station))
            .findFirst()
            .get();
    }
}
