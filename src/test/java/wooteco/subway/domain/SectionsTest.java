package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static wooteco.subway.SubwayFixtures.STATION_FIXTURE1;
import static wooteco.subway.SubwayFixtures.STATION_FIXTURE2;
import static wooteco.subway.SubwayFixtures.강남역;
import static wooteco.subway.SubwayFixtures.대림역;
import static wooteco.subway.SubwayFixtures.삼성역;
import static wooteco.subway.SubwayFixtures.서초역;
import static wooteco.subway.SubwayFixtures.선릉역;
import static wooteco.subway.SubwayFixtures.성담빌딩;
import static wooteco.subway.SubwayFixtures.역삼역;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import wooteco.subway.SubwayFixtures;
import wooteco.subway.exception.validation.SectionDuplicateException;
import wooteco.subway.exception.validation.SectionNotSuitableException;

public class SectionsTest {

    private Section YEOKSAM_TO_SUNNEUNG;
    private Section GANGNAM_TO_YEOKSAM;
    private Section SUNNEUNG_TO_SAMSUNG;
    private Section SUNGDAM_BUILDING_TO_SAMSUNG;
    private Section YEOKSAM_TO_SAMSUNG;

    @BeforeEach
    void setup() {
        YEOKSAM_TO_SUNNEUNG = new Section(1L, 2L, 선릉역, 역삼역, 10);
        GANGNAM_TO_YEOKSAM = new Section(2L, 2L, 역삼역, 강남역, 10);
        SUNNEUNG_TO_SAMSUNG = new Section(3L, 2L, 삼성역, 선릉역, 10);
        SUNGDAM_BUILDING_TO_SAMSUNG = new Section(4L, 2L, 삼성역, 성담빌딩, 5);
        YEOKSAM_TO_SAMSUNG = new Section(5L, 2L, 삼성역, 역삼역, 10);
    }

    @Test
    @DisplayName("노선 id, 상행 지하철역 id, 하행 지하철역 id, 거리를 전달하여 구간을 생성할 수 있다")
    void createSection() {
        // given
        final Long id = 1L;
        final Long lineId = 2L;
        final Station upStation = STATION_FIXTURE1;
        final Station downStation = STATION_FIXTURE2;
        final int distance = 10;

        // when & then
        assertThatCode(() -> new Section(id, lineId, upStation, downStation, distance))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("기존 구간에 하행 종점을 추가하여 노선을 연장할 수 있다")
    void extendDownStation() {
        // given
        final Sections sections = new Sections(List.of(YEOKSAM_TO_SUNNEUNG));

        // when
        SectionResult sectionResult = sections.add(GANGNAM_TO_YEOKSAM);

        // then
        assertThat(sectionResult).isEqualTo(SectionResult.DOWN_EXTENDED);
    }

    @Test
    @DisplayName("기존 구간에 상행 종점을 추가하여 노선을 연장할 수 있다")
    void extendUpStation() {
        // given
        final Sections sections = new Sections(List.of(YEOKSAM_TO_SUNNEUNG));

        // when
        SectionResult sectionResult = sections.add(SUNNEUNG_TO_SAMSUNG);

        // then
        assertThat(sectionResult).isEqualTo(SectionResult.UP_EXTENDED);
    }

    @Test
    @DisplayName("구간을 추가함으로써 두 지하철역 사이에 역을 추가할 수 있다")
    void addSectionInMiddle() {
        // given
        final Sections sections = new Sections(List.of(SUNNEUNG_TO_SAMSUNG));

        // when
        SectionResult sectionResult = sections.add(SUNGDAM_BUILDING_TO_SAMSUNG);

        // then
        assertThat(sectionResult).isEqualTo(SectionResult.MIDDLE_ADDED);
    }

    @Test
    @DisplayName("이미 존재하는 두 지하철역을 연결한 구간을 추가 시도하면 예외가 발생한다")
    void addingSectionWithTwoExistStationShouldFail() {
        // given
        final Sections sections = new Sections(List.of(YEOKSAM_TO_SUNNEUNG, SUNNEUNG_TO_SAMSUNG));
        final Station upStation = YEOKSAM_TO_SAMSUNG.getUpStation();
        final Station downStation = YEOKSAM_TO_SAMSUNG.getDownStation();

        // when & then
        assertThatThrownBy(() -> sections.add(YEOKSAM_TO_SAMSUNG))
                .isInstanceOf(SectionDuplicateException.class)
                .hasMessage(String.format("이미 존재하는 구간입니다 : 상행역 - %s, 하행역 - %s",
                        upStation.getName(),
                        downStation.getName()));
    }

    @Test
    @DisplayName("일치하는 지하철역이 하나도 없을 경우 예외가 발생한다")
    void addingSectionWithNoExistStationShouldFail() {
        // given
        final Sections sections = new Sections(List.of(GANGNAM_TO_YEOKSAM, YEOKSAM_TO_SUNNEUNG));
        final Station upStation = SUNGDAM_BUILDING_TO_SAMSUNG.getUpStation();
        final Station downStation = SUNGDAM_BUILDING_TO_SAMSUNG.getDownStation();

        // when & then
        assertThatThrownBy(() -> sections.add(SUNGDAM_BUILDING_TO_SAMSUNG))
                .isInstanceOf(SectionNotSuitableException.class)
                .hasMessage(String.format("일치하는 역이 없어 구간 추가에 실패하였습니다 : 상행역 - %s, 하행역 - %s",
                        upStation.getName(),
                        downStation.getName()));
    }

    @Test
    @DisplayName("상행역과 하행역을 엮어 정렬이 가능하다")
    void sectionsSortTest() {
        // given
        final Sections sections = new Sections(List.of(SUNNEUNG_TO_SAMSUNG, GANGNAM_TO_YEOKSAM, YEOKSAM_TO_SUNNEUNG));

        // when
        final List<Section> sortedSections = sections.getSections();

        // then
        assertThat(sortedSections).containsExactly(SUNNEUNG_TO_SAMSUNG, YEOKSAM_TO_SUNNEUNG, GANGNAM_TO_YEOKSAM);
    }

    @TestFactory
    @DisplayName("구간을 추가, 삭제하더라도 상행역 -> 하행역 순으로 정렬된다")
    Stream<DynamicTest> sectionAddRemoveSortTest() {
        // 역삼(상행) <> 강남(하행) 구간 생성
        final Sections sections = new Sections(List.of(GANGNAM_TO_YEOKSAM));

        return Stream.of(
                dynamicTest("역삼-강남 에 선릉-역삼 을 추가하면 선릉-역삼-강남 이 된다", () -> {
                    // given
                    final Section input = SubwayFixtures.YEOKSAM_TO_SUNNEUNG;

                    // when
                    final SectionResult result = sections.add(input);
                    final List<Station> sortedStations = getSortedStations(sections);

                    // then
                    assertAll(
                            () -> assertThat(result).isEqualTo(SectionResult.UP_EXTENDED),
                            () -> assertThat(sortedStations).containsExactly(선릉역, 역삼역, 강남역)
                    );
                }),

                dynamicTest("선릉-역삼-강남 에 강남-서초 를 추가하면 선릉-역삼-강남-서초 가 된다", () -> {
                    // given
                    final Section input = SubwayFixtures.SEOCHO_TO_GANGNAM;

                    // when
                    final SectionResult result = sections.add(input);
                    final List<Station> sortedStations = getSortedStations(sections);

                    // then
                    assertAll(
                            () -> assertThat(result).isEqualTo(SectionResult.DOWN_EXTENDED),
                            () -> assertThat(sortedStations).containsExactly(선릉역, 역삼역, 강남역, 서초역)
                    );
                }),

                dynamicTest("선릉-역삼-강남-서초 에 서초-대림 을 추가하면 선릉-역삼-강남-서초-대림 이 된다", () -> {
                    // given
                    final Section input = SubwayFixtures.DAELIM_TO_SEOCHO;

                    // when
                    final SectionResult result = sections.add(input);
                    final List<Station> sortedStations = getSortedStations(sections);

                    // then
                    assertAll(
                            () -> assertThat(result).isEqualTo(SectionResult.DOWN_EXTENDED),
                            () -> assertThat(sortedStations).containsExactly(선릉역, 역삼역, 강남역, 서초역, 대림역)
                    );
                }),

                dynamicTest("선릉-역삼-강남-서초-대림 에 성담빌딩-선릉 을 추가하면 성담빌딩-선릉-역삼-강남-서초-대림 이 된다", () -> {
                    // given
                    final Section input = SubwayFixtures.SUNNEUNG_TO_SUNGDAM;

                    // when
                    final SectionResult result = sections.add(input);
                    final List<Station> sortedStations = getSortedStations(sections);

                    // then
                    assertAll(
                            () -> assertThat(result).isEqualTo(SectionResult.UP_EXTENDED),
                            () -> assertThat(sortedStations).containsExactly(성담빌딩, 선릉역, 역삼역, 강남역, 서초역, 대림역)
                    );
                }),

                dynamicTest("성담빌딩-선릉-역삼-강남-서초-대림 에서 역삼역을 제거하면 성담빌딩-선릉-강남-서초-대림 이 된다", () -> {
                    // given
                    final Long id = 역삼역.getId();
                    final Long lineId = GANGNAM_TO_YEOKSAM.getLineId();

                    // when
                    final SectionResult result = sections.remove(lineId, id);
                    final List<Station> sortedStations = getSortedStations(sections);

                    // then
                    assertAll(
                            () -> assertThat(result).isEqualTo(SectionResult.MIDDLE_REMOVED),
                            () -> assertThat(sortedStations).containsExactly(성담빌딩, 선릉역, 강남역, 서초역, 대림역)
                    );
                }),

                dynamicTest("성담빌딩-선릉-강남-서초-대림 에서 성담빌딩을 제거하면 선릉-강남-서초-대림 이 된다", () -> {
                    // given
                    final Long id = 성담빌딩.getId();
                    final Long lineId = GANGNAM_TO_YEOKSAM.getLineId();

                    // when
                    final SectionResult result = sections.remove(lineId, id);
                    final List<Station> sortedStations = getSortedStations(sections);

                    // then
                    assertAll(
                            () -> assertThat(result).isEqualTo(SectionResult.UP_REDUCED),
                            () -> assertThat(sortedStations).containsExactly(선릉역, 강남역, 서초역, 대림역)
                    );
                }),

                dynamicTest("선릉-강남-서초-대림 에서 대림을 제거하면 선릉-강남-서초 가 된다", () -> {
                    // given
                    final Long id = 대림역.getId();
                    final Long lineId = GANGNAM_TO_YEOKSAM.getLineId();

                    // when
                    final SectionResult result = sections.remove(lineId, id);
                    final List<Station> sortedStations = getSortedStations(sections);

                    // then
                    assertAll(
                            () -> assertThat(result).isEqualTo(SectionResult.DOWN_REDUCED),
                            () -> assertThat(sortedStations).containsExactly(선릉역, 강남역, 서초역)
                    );
                })
        );
    }

    private List<Station> getSortedStations(Sections sections) {
        final List<Section> sortedSections = sections.getSections();

        return sortedSections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
}
