package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.common.exception.AlreadyExistsException;
import wooteco.subway.common.exception.InvalidInputException;
import wooteco.subway.common.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.line.LineFactory.인천1호선;
import static wooteco.subway.line.LineFactory.인천2호선;
import static wooteco.subway.line.SectionFactory.*;
import static wooteco.subway.station.StationFactory.*;

@DisplayName("Section 일급컬렉션 기능 테스트")
class SectionsTest {
    private Sections singleSection;
    private Sections doubleSections;

    @BeforeEach
    void init() {
        singleSection = new Sections(인천1호선_구간.sortedSections());
        doubleSections = new Sections(인천2호선_구간.sortedSections());
    }

    @DisplayName("상행좀점역에 역을 추가한다.")
    @Test
    void headAddSection() {
        //given
        Section section = new Section(낙성대역, 흑기역, 5);
        //when
        assertThatCode(() -> singleSection.addSection(section))
                .doesNotThrowAnyException();
        List<Section> sections = singleSection.sortedSections();
        //then
        assertThat(sections).containsExactly(section, 인천1호선_흑기백기구간);
    }

    @DisplayName("하행좀점역에 역을 추가한다.")
    @Test
    void tailAddSection() {
        //given
        Section section = new Section(백기역, 낙성대역, 5);

        //when
        assertThatCode(() -> singleSection.addSection(section))
                .doesNotThrowAnyException();
        List<Section> sections = singleSection.sortedSections();

        //then
        assertThat(sections).containsExactly(인천1호선_흑기백기구간, section);
    }

    @DisplayName("상행역을 기준으로 역을 추가한다.")
    @Test
    void headBetweenAdSection() {
        //given
        int distance = 3;
        Section section = new Section(흑기역, 낙성대역, distance);

        //when
        assertThatCode(() -> singleSection.addSection(section))
                .doesNotThrowAnyException();
        List<Section> sections = singleSection.sortedSections();

        //then
        assertThat(sections).contains(section, new Section(인천1호선, 낙성대역, 백기역, 인천1호선_흑기백기구간.distance() - distance));
    }

    @DisplayName("하행역을 기준으로 역을 추가한다.")
    @Test
    void tailBetweenAdSection() {
        //given
        int distance = 3;
        Section section = new Section(낙성대역, 백기역, distance);

        //when
        assertThatCode(() -> singleSection.addSection(section))
                .doesNotThrowAnyException();
        List<Section> sections = singleSection.sortedSections();

        //then
        assertThat(sections).contains(new Section(인천1호선, 흑기역, 낙성대역, 인천1호선_흑기백기구간.distance() - distance), section);
    }

    @DisplayName("추가하려는 구간이 기존 구간의 길이보다 길거나 같으면 예외가 발생한다.")
    @Test
    void headBetweenAdSectionException() {
        //given
        int distance = 5;
        Section section = new Section(낙성대역, 백기역, distance);

        //when
        //then
        assertThatThrownBy(() -> singleSection.addSection(section))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("기존의 구간보다 길이가 길 수 없음!!");
    }

    @DisplayName("추가하려는 구간이 이미 등록되어있으면 예외가 발생한다")
    @Test
    void AddSectionAlreadyExistsException() {
        //given
        //when
        //then
        assertThatThrownBy(() -> singleSection.addSection(인천1호선_흑기백기구간))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("이미 등록되어 있는 구간임!");
    }

    @DisplayName("추가하려는 구간의 상행, 하행역이 노선에 등록되어있으면 예외가 발생한다")
    @Test
    void AsdSectionAlreadyExistsException() {
        //given
        //when
        //then
        assertThatThrownBy(() -> singleSection.addSection(new Section(낙성대역, 잠실역, 3)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("상행, 하행역 둘다 노선에 등록되어 있지 않음!!");
    }

    @DisplayName("상행 좀점역을 재거한다.")
    @Test
    void headDeleteStation() {
        //given
        //when
        assertThatCode(() -> doubleSections.deleteStation(흑기역))
                .doesNotThrowAnyException();
        List<Section> sections = doubleSections.sortedSections();

        //then
        assertThat(sections).containsExactly(인천2호선_백기낙성대구간);
    }

    @DisplayName("하행 좀점역을 재거한다.")
    @Test
    void tailDeleteStation() {
        //given
        //when
        assertThatCode(() -> doubleSections.deleteStation(낙성대역))
                .doesNotThrowAnyException();
        List<Section> sections = doubleSections.sortedSections();

        //then
        assertThat(sections).containsExactly(인천2호선_흑기백기구간);
    }

    @DisplayName("중간에 포함된 역을 재거한다.")
    @Test
    void deleteBetwwenStation() {
        //given
        //when
        assertThatCode(() -> doubleSections.deleteStation(백기역))
                .doesNotThrowAnyException();
        List<Section> sections = doubleSections.sortedSections();

        //then
        assertThat(sections).containsExactly(new Section(인천2호선, 흑기역, 낙성대역, 인천2호선_흑기백기구간.distance() + 인천2호선_백기낙성대구간.distance()));
    }

    @DisplayName("구간이 1개있을 경우 상행역 삭제요청시 예외가 발생한다.")
    @Test
    void deleteHeadStationException() {
        //given
        //when
        //then
        assertThatThrownBy(() -> singleSection.deleteStation(흑기역))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("1개의 구간만 있기에 삭제 할 수 없음!");
    }

    @DisplayName("구간이 1개있을 경우 하행역 삭제요청시 예외가 발생한다.")
    @Test
    void deleteTailStationException() {
        //given
        //when
        //then
        assertThatThrownBy(() -> singleSection.deleteStation(백기역))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("1개의 구간만 있기에 삭제 할 수 없음!");
    }
}