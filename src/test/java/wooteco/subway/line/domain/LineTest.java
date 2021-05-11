package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class LineTest {
    private Station station1;
    private Station station2;
    private Line line;
    private Section section;

    @BeforeEach
    void setUp() {
        station1 = new Station(1L, "아마역");
        station2 = new Station(2L, "마찌역");
        section = new Section(1L, station1, station2, 10);
        line = new Line("9호선", "bg-red-600", Arrays.asList(section));
    }


    @Test
    @DisplayName("라인 정상 생성 테스트 ")
    void create() {
        assertThatCode(() -> new Line("신분당선", "bg-red-600"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("초기 구간을 설정한다.")
    void addSection() {
        Line line = new Line("9호선", "bg-red-600");
        Section section = new Section(station1, station2, 10);

        line.initSections(Arrays.asList(section));
        assertThat(line.stations()).hasSize(2);
    }

//    @Test
//    @DisplayName("구간을 추가한 노선을 반환한다.")
//    void addedSectionLine() {
//        // given
//        Station station3 = new Station(3L, "잠실역");
//        Section toAddSection = new Section(station3, station1, 1);
//
//        // when
//        Section affectedSection = line.addAndReturnAffectedSection(toAddSection);
//
//        // then
//        assertThat(affectedSection.id()).isEqualTo(1L);
//        assertThat(affectedSection.upStation()).isEqualTo(station1);
//        assertThat(affectedSection.downStation()).isEqualTo(station3);
//        assertThat(affectedSection.distance()).isEqualTo(5);
//    }

    //    @Test
//    @DisplayName("구간을 추가시 등록하려는 구간의 모든 역이 노선에 이미 등록되어 있으면 예외가 발생한다.")
//    void addedSectionLine() {
//        assertThatThrownBy(() -> line.addSection(section))
//                .isInstanceOf(IllegalStateException.class);
//    }
//
//    @Test
//    @DisplayName("구간 추가시 등록하려는 구간의 역이 노선에 등록되어 있지 않으면 예외가 발생한다.")
//    void addSectionException2() {
//        Section newSection = new Section(new Station(3L, "강변역"), new Station(4L, "선릉역"), 10);
//        assertThatThrownBy(() -> line.addSection(newSection))
//                .isInstanceOf(IllegalStateException.class);
//    }
}
