package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {

    @DisplayName("동일한 이름의 역으로 구간 생성 시 예외 발생")
    @Test
    void 동일_역으로_구간_생성_예외발생() {
        Station up = new Station("선릉역");
        Station down = new Station("선릉역");

        assertThatThrownBy(() -> new Section(up, down, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("유효하지 않은 길이로 구간 생성 시 예외 발생")
    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.8"})
    void 자연수가_아닌_길이로_구간_생성_예외발생(String distance) {
        Station up = new Station("선릉역");
        Station down = new Station("잠실역");

        assertThatThrownBy(() -> new Section(up, down, Integer.parseInt(distance)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("두 구간이 공유하는 역이 없으면, 아무 관계도 아님")
    @Test
    void 공유하는_역이_없는_두_구간_NONE() {
        Section section1 = new Section(new Station("홍대입구역"), new Station("합정역"), 1);
        Section section2 = new Section(new Station("용산역"), new Station("삼각지역"), 1);

        assertThat(section1.calculateRelation(section2)).isEqualTo(Relation.NONE);
    }
    
    @DisplayName("두 구간이 같은 상행 역을 공유하면 포함 관계")
    @Test
    void 같은_상행역_공유하는_두_구간_INCLUDE() {
        Section section1 = new Section(new Station("합정역"), new Station("신촌역"), 2);
        Section section2 = new Section(new Station("합정역"), new Station("홍대입구역"), 1);

        assertThat(section1.calculateRelation(section2)).isEqualTo(Relation.INCLUDE);
    }

    @DisplayName("두 구간이 같은 하행 역을 공유하면 포함 관계")
    @Test
    void 같은_하행역_공유하는_두_구간_INCLUDE() {
        Section section1 = new Section(new Station("합정역"), new Station("신촌역"), 2);
        Section section2 = new Section(new Station("홍대입구역"), new Station("신촌역"), 1);

        assertThat(section1.calculateRelation(section2)).isEqualTo(Relation.INCLUDE);
    }

    @DisplayName("두 구간이 서로 다른 방향 역을 공유하면 연장 관계")
    @Test
    void 서로_다른_방향역_공유하는_두_구간_EXTEND1() {
        Section section1 = new Section(new Station("합정역"), new Station("홍대입구역"), 2);
        Section section2 = new Section(new Station("홍대입구역"), new Station("신촌역"), 1);

        assertThat(section1.calculateRelation(section2)).isEqualTo(Relation.EXTEND);
    }

    @DisplayName("두 구간이 서로 다른 방향 역을 공유하면 연장 관계")
    @Test
    void 서로_다른_방향역_공유하는_두_구간_EXTEND2() {
        Section section1 = new Section(new Station("강남역"), new Station("선릉역"), 2);
        Section section2 = new Section(new Station("교대역"), new Station("강남역"), 1);

        assertThat(section1.calculateRelation(section2)).isEqualTo(Relation.EXTEND);
    }

}