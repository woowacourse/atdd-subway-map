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

    @DisplayName("상행이 겹치는 경우 겹치는 구간을 제외한 나머지 구간 반환")
    @Test
    void 상행_겹치는_구간_빼기() {
        Section origin = new Section(new Station("강남역"), new Station("사당역"), 2);
        Section other = new Section(new Station("강남역"), new Station("방배역"), 1);

        Section divided = origin.divideBy(other);
        Section expected = new Section(new Station("방배역"), new Station("사당역"), 1);

        assertThat(divided).isEqualTo(expected);
    }

    @DisplayName("하행이 겹치는 경우 겹치는 구간을 제외한 나머지 구간 반환")
    @Test
    void 하행_겹치는_구간_빼기() {
        Section origin = new Section(new Station("강남역"), new Station("양재시민의숲역"), 5);
        Section other = new Section(new Station("양재역"), new Station("양재시민의숲역"), 3);

        Section divided = origin.divideBy(other);
        Section expected = new Section(new Station("강남역"), new Station("양재역"), 2);

        assertThat(divided).isEqualTo(expected);
    }

    @DisplayName("겹치는 역이 없다면 예외 발생")
    @Test
    void 겹치는_구간_없으면_쪼개기_불가_예외발생() {
        Section origin = new Section(new Station("홍대입구역"), new Station("힙정역"), 5);
        Section other = new Section(new Station("사당역"), new Station("강남역"), 3);

        assertThatThrownBy(() -> origin.divideBy(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("겹치는 역");
    }

    @DisplayName("기존 구간보다 빼려는 구간이 더 길면 예외발생")
    @Test
    void 구간_나눌_시_1이하_예외발생() {
        Section origin = new Section(new Station("합정역"), new Station("홍대입구역"), 2);
        Section other = new Section(new Station("당산역"), new Station("홍대입구역"), 4);

        assertThatThrownBy(() -> origin.divideBy(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("거리");
    }
}