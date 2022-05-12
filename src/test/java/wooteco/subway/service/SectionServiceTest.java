package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.repository.*;
import wooteco.subway.service.dto.SectionRequest;
import wooteco.subway.utils.exception.DuplicatedException;
import wooteco.subway.utils.exception.NotDeleteException;
import wooteco.subway.utils.exception.NotFoundException;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class SectionServiceTest {

    private static final Long LINE_ID = 1L;
    private static final Long UP_STATION_ID = 1L;
    private static final Long MIDDLE_STATION_ID = 2L;
    private static final Long DOWN_STATION_ID = 3L;

    @Autowired
    private DataSource dataSource;

    private SectionService sectionService;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;
    private LineRepository lineRepository;


    @BeforeEach
    void setUp() {
        sectionRepository = new SectionRepositoryImpl(dataSource);
        lineRepository = new LineRepositoryImpl(dataSource);
        stationRepository = new StationRepositoryImpl(dataSource);
        sectionService = new SectionService(sectionRepository, stationRepository);
    }


    @DisplayName("노선을 생성할때 구간을 생성한다.")
    @Test
    void init() {
        Station newStation = stationRepository.save(new Station("신림역"));
        SectionRequest sectionRequest = new SectionRequest(newStation.getId(), MIDDLE_STATION_ID, 2);
        Section section = sectionService.init(LINE_ID, sectionRequest);

        assertThat(section.getId()).isNotNull();
    }

    @DisplayName("구간이 2개있을 때 구간을 추가한다.")
    @Test
    void add() {
        //given
        sectionRepository.deleteById(UP_STATION_ID);
        Station station = stationRepository.save(new Station("신도림역"));
        SectionRequest sectionRequest = new SectionRequest(MIDDLE_STATION_ID, station.getId(), 2);
        //when
        sectionService.add(LINE_ID, sectionRequest);
        List<Section> sections = sectionRepository.findAllByLineId(LINE_ID);
        //then
        assertThat(sections).hasSize(2);
    }

    @DisplayName("하행 종점에 구간을 추가한다.")
    @Test
    void addDownTerminal() {
        //given
        Station downStation = stationRepository.save(new Station("신도림역"));
        SectionRequest sectionRequest = new SectionRequest(DOWN_STATION_ID, downStation.getId(), 5);
        //when
        sectionService.add(LINE_ID, sectionRequest);
        List<Section> sections = sectionRepository.findAllByLineId(LINE_ID);
        //then
        assertThat(sections).hasSize(3);
    }

    @DisplayName("상행 종점에 구간을 추가한다.")
    @Test
    void addUpTerminal() {
        //given
        Station station = stationRepository.save(new Station("신도림역"));
        SectionRequest sectionRequest = new SectionRequest(station.getId(), UP_STATION_ID, 5);
        //when
        sectionService.add(LINE_ID, sectionRequest);
        List<Section> sections = sectionRepository.findAllByLineId(LINE_ID);
        //then
        assertThat(sections).hasSize(3);
    }

    @DisplayName("역 중간에 넣을 구간중 하행역이 일치할때 구간을 추가한다.")
    @Test
    void addDown() {
        //given
        Station station = stationRepository.save(new Station("신도림역"));
        SectionRequest sectionRequest = new SectionRequest(station.getId(), MIDDLE_STATION_ID, 3);
        //when
        sectionService.add(LINE_ID, sectionRequest);
        List<Section> sections = sectionRepository.findAllByLineId(LINE_ID);
        //then
        for (Section section : sections) {
            System.out.println("상행 > " + section.getUpStation().getName() + " , 하행 > " + section.getDownStation().getName());
        }
        assertThat(sections).hasSize(3);
    }

    @DisplayName("역 중간에 넣을 구간중 상행역이 일치할때 구간을 추가한다.")
    @Test
    void addUp() {
        //given
        Station station = stationRepository.save(new Station("신도림역"));
        SectionRequest sectionRequest = new SectionRequest(MIDDLE_STATION_ID, station.getId(), 3);
        //when
        sectionService.add(LINE_ID, sectionRequest);
        List<Section> sections = sectionRepository.findAllByLineId(LINE_ID);
        //then
        assertThat(sections).hasSize(3);
    }

    @DisplayName("구간 사이에 구간이 추가될때 길이가 기존 구간보다 같거나 길면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {5, 6})
    void createFailure(int distance) {
        Station downStation = stationRepository.save(new Station("신도림역"));
        SectionRequest sectionRequest = new SectionRequest(MIDDLE_STATION_ID, downStation.getId(), distance);

        assertThatThrownBy(
                () -> sectionService.add(LINE_ID, sectionRequest)
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선에 존재하지 않는 역으로 구간을 저장하려고 하면 예외가 발생한다..")
    @Test
    void createFailureWhenNonStations() {
        Station upStation = stationRepository.save(new Station("신림역"));
        Station downStation = stationRepository.save(new Station("신도림역"));

        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), 5);

        Line line = lineRepository.findById(LINE_ID).get();
        assertThatThrownBy(
                () -> sectionService.add(line.getId(), sectionRequest)
        ).isExactlyInstanceOf(NotFoundException.class);

    }

    @DisplayName("이미 존재하는 구간을 추가할 때 예외가 발생한다.")
    @Test
    void createFailureWhenDuplicate() {
        Line line = lineRepository.findById(LINE_ID).get();
        SectionRequest sectionRequest = new SectionRequest(MIDDLE_STATION_ID, DOWN_STATION_ID, 5);

        assertThatThrownBy(
                () -> sectionService.add(line.getId(), sectionRequest)
        ).isExactlyInstanceOf(DuplicatedException.class);
    }

    @DisplayName("중간의 역을 구간에서 제거하면 기존의 구간들이 연결된다. 거리는 기존의 구간들의 합이다.")
    @Test
    void delete() {
        sectionService.delete(LINE_ID, MIDDLE_STATION_ID);
        List<Section> sections = sectionRepository.findAllByLineId(LINE_ID);

        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(sections.get(0).getUpStation().getId()).isEqualTo(UP_STATION_ID),
                () -> assertThat(sections.get(0).getDownStation().getId()).isEqualTo(DOWN_STATION_ID),
                () -> assertThat(sections.get(0).getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("상행 종점의 역을 구간에서 제거한다.")
    @Test
    void deleteWhenTerminalUpStation() {
        sectionService.delete(LINE_ID, UP_STATION_ID);
        List<Section> sections = sectionRepository.findAllByLineId(LINE_ID);

        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(sections.get(0).getUpStation().getId()).isEqualTo(MIDDLE_STATION_ID),
                () -> assertThat(sections.get(0).getDownStation().getId()).isEqualTo(DOWN_STATION_ID)
        );
    }

    @DisplayName("하행 종점의 역을 구간에서 제거한다.")
    @Test
    void deleteWhenTerminalDownStation() {
        sectionService.delete(LINE_ID, DOWN_STATION_ID);
        List<Section> sections = sectionRepository.findAllByLineId(LINE_ID);

        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(sections.get(0).getUpStation().getId()).isEqualTo(UP_STATION_ID),
                () -> assertThat(sections.get(0).getDownStation().getId()).isEqualTo(MIDDLE_STATION_ID)
        );
    }

    @DisplayName("구간이 하나일때는 역을 구간에서 제거할수없다.")
    @Test
    void deleteFailure() {
        sectionService.delete(LINE_ID, DOWN_STATION_ID);
        assertThatThrownBy(
                () -> sectionService.delete(LINE_ID, MIDDLE_STATION_ID)
        ).isExactlyInstanceOf(NotDeleteException.class);
    }
}
