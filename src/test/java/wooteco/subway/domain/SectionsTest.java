package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    private Station upStation = new Station("강남역");
    private Station downStation = new Station("청계산입구역");
    private Line line;
    
    @BeforeEach
    void init(){
        line = Line.initialCreateWithoutId("신분당선", "빨간색", upStation, downStation, 7);
    }

    @Test
    @DisplayName("노선이 추가되면 자동으로 구간이 추가된다.")
    void createLine() {
        //then
        List<Station> stations = line.getStations();
        assertThat(stations.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("구간을 추가하면 노선에 포함된 역이 한 개 늘어난다.")
    void addSection() {
        //given
        Station upStation2 = new Station("청계산입구역");
        Station downStation2 = new Station("정자역");
        Section section = Section.createWithoutId(upStation2, downStation2, 7);
        //when
        line.addSection(section);
        //then
        final List<Station> stations = line.getStations();
        assertThat(stations.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("구간을 추가할 때 상행 갈래길을 방지한다.")
    void addSectionWithoutUpBranch() {
        //given
        Station upStation2 = new Station("강남역");
        Station downStation2 = new Station("양재역");
        Section section = Section.createWithoutId(upStation2, downStation2, 5);
        //when
        line.addSection(section);
        //then
        List<Section> sections = line.getSections();
        final Section section1 = sections.stream()
                .filter(it -> it.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow();
        final Section section2 = sections.stream()
                .filter(it -> it.getUpStation().equals(downStation2))
                .findFirst()
                .orElseThrow();
        assertAll(
                () -> assertThat(section1.getDownStation()).isEqualTo(downStation2),
                () -> assertThat(section2.getDownStation()).isEqualTo(downStation)
        );
    }

    @Test
    @DisplayName("구간을 추가할 때 하행 갈래길을 방지한다.")
    void addSectionWithoutDownBranch() {
        //given
        Station upStation2 = new Station("양재역");
        Station downStation2 = new Station("청계산입구역");
        Section section = Section.createWithoutId(upStation2, downStation2, 5);
        //when
        line.addSection(section);
        //then
        final List<Section> sections = line.getSections();
        final Section section1 = sections.stream()
                .filter(it -> it.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow();
        final Section section2 = sections.stream()
                .filter(it -> it.getUpStation().equals(upStation2))
                .findFirst()
                .orElseThrow();

        assertAll(
                () -> assertThat(section1.getDownStation()).isEqualTo(upStation2),
                () -> assertThat(section2.getDownStation()).isEqualTo(downStation2)
        );
    }

    @Test
    @DisplayName("상행 종점 구간 등록")
    void addSectionUpTerminal() {
        //given
        final Station newUpStation = new Station("신강남역");
        final Section section = Section.createWithoutId(newUpStation, upStation, 3);
        //when
        line.addSection(section);
        //then
        final List<Section> sections = line.getSections();
        final Section section1 = sections.stream()
                .filter(it -> it.getDownStation().equals(upStation))
                .findFirst()
                .orElseThrow();
        final Section section2 = sections.stream()
                .filter(it -> it.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow();

        assertAll(
                () -> assertThat(section1.getUpStation()).isEqualTo(newUpStation),
                () -> assertThat(section2.getDownStation()).isEqualTo(downStation)
        );
    }

    @Test
    @DisplayName("하행 종점 구간 등록")
    void addSectionDownTerminal() {
        //given
        final Station newDownStation = new Station("정자역");
        final Section section = Section.createWithoutId(downStation, newDownStation, 10);
        //when
        line.addSection(section);
        //then
        final List<Section> sections = line.getSections();

        final Section section1 = sections.stream()
                .filter(it -> it.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow();
        final Section section2 = sections.stream()
                .filter(it -> it.getUpStation().equals(downStation))
                .findFirst()
                .orElseThrow();

        assertAll(
                () -> assertThat(section1.getDownStation()).isEqualTo(downStation),
                () -> assertThat(section2.getDownStation()).isEqualTo(newDownStation)
        );
    }

    @Test
    @DisplayName("역 사이에 구간을 등록할 때 원래 구간보다 길이가 크거나 같을 경우 예외 발생 - 상행 갈래길")
    void distanceExceptionWithUpBranch() {
        //given
        final Station newUpStation = new Station("양재역");
        final Section section = Section.createWithoutId(newUpStation, downStation, 7);

        //then
        assertThatThrownBy(() -> line.addSection(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("역 사이에 구간을 등록할 때 원래 구간보다 길이가 크거나 같을 경우 예외 발생 - 하행 갈래길")
    void distanceExceptionWithDownBranch() {
        //given
        final Station newDownStation = new Station("양재역");
        final Section section = Section.createWithoutId(upStation, newDownStation, 7);

        //then
        assertThatThrownBy(() -> line.addSection(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있으면 예외 발생")
    void alreadyRegisteredException() {
        //given
        final Section section = Section.createWithoutId(upStation, downStation, 4);
        //then
        assertThatThrownBy(() -> line.addSection(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역이 둘 다 노선에 등록되어 있지 않으면 예외 발생")
    void noExistentStationException() {
        //given
        final Station newUpStation = new Station("석촌고분역");
        final Station newDownStation = new Station("석촌역");
        final Section section = Section.createWithoutId(newUpStation, newDownStation, 5);
        //then
        assertThatThrownBy(() -> line.addSection(section))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    @DisplayName("상행 종점이 제거될 경우 다음역이 종점이 됨")
    void deleteSectionWithUpTerminal() {
        //given
        final Station newDownStation = new Station("정자역");
        final Section section = Section.createWithoutId(downStation, newDownStation, 10);
        line.addSection(section);
        //when
        line.deleteSection(upStation);
        //then
        final List<Station> stations = line.getStations();
        final List<Section> sections = line.getSections();
        assertAll(
                () -> assertThat(stations.contains(upStation)).isFalse(),
                () -> assertThat(sections.stream().anyMatch(it -> it.getDownStation().equals(downStation))).isFalse()
        );
    }

    @Test
    @DisplayName("하행 종점이 제거될 경우 이전 역이 종점이 됨")
    void deleteSectionWithDownTerminal() {
        //given
        final Station newDownStation = new Station("정자역");
        final Section section = Section.createWithoutId(downStation, newDownStation, 10);
        line.addSection(section);
        //when
        line.deleteSection(newDownStation);
        //then
        final List<Station> stations = line.getStations();
        final List<Section> sections = line.getSections();
        assertAll(
                () -> assertThat(stations.contains(newDownStation)).isFalse(),
                () -> assertThat(sections.stream().anyMatch(it -> it.getUpStation().equals(downStation))).isFalse()
        );
    }
    
    @Test
    @DisplayName("강남-청계산입구-양재역 에서 청계산입구가 제거되면 강남-양재가 된다.")
    void deleteSectionOnMiddle() {
        //given
        final Station newDownStation = new Station("정자역");
        final Section section = Section.createWithoutId(downStation, newDownStation, 10);
        line.addSection(section);
        //when
        line.deleteSection(downStation);
        //then
        final List<Station> stations = line.getStations();
        final List<Section> sections = line.getSections();
        final Section section1 = sections.stream()
                .filter(it -> it.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow();
        assertAll(
                () -> assertThat(stations.contains(downStation)).isFalse(),
                () -> assertThat(section1.getDownStation()).isEqualTo(newDownStation),
                () -> assertThat(section1.getDistance()).isEqualTo(17)
        );
    }
    
    @Test
    @DisplayName("구간이 하나면 역을 제거할 수 없다.")
    void deleteExceptionByFinalSection() {
        //then
        assertThatThrownBy(() -> line.deleteSection(upStation))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

