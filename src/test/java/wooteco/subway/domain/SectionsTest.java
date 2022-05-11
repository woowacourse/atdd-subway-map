package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.SubwayFixtures.GANGNAM;
import static wooteco.subway.SubwayFixtures.SAMSUNG;
import static wooteco.subway.SubwayFixtures.STATION_FIXTURE1;
import static wooteco.subway.SubwayFixtures.STATION_FIXTURE2;
import static wooteco.subway.SubwayFixtures.SUNGDAM;
import static wooteco.subway.SubwayFixtures.SUNNEUNG;
import static wooteco.subway.SubwayFixtures.YEOKSAM;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SectionDuplicateException;
import wooteco.subway.exception.SectionNotSuitableException;

public class SectionsTest {

    private Section YEOKSAM_TO_SUNNEUNG;
    private Section GANGNAM_TO_YEOKSAM;
    private Section SUNNEUNG_TO_SAMSUNG;
    private Section SUNGDAM_BUILDING_TO_SAMSUNG;
    private Section YEOKSAM_TO_SAMSUNG;

    @BeforeEach
    void setup() {
        YEOKSAM_TO_SUNNEUNG = new Section(1L, 2L, SUNNEUNG, YEOKSAM, 10);
        GANGNAM_TO_YEOKSAM = new Section(2L, 2L, YEOKSAM, GANGNAM, 10);
        SUNNEUNG_TO_SAMSUNG = new Section(3L, 2L, SAMSUNG, SUNNEUNG, 10);
        SUNGDAM_BUILDING_TO_SAMSUNG = new Section(4L, 2L, SAMSUNG, SUNGDAM, 5);
        YEOKSAM_TO_SAMSUNG = new Section(5L, 2L, SAMSUNG, YEOKSAM, 10);
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
}
