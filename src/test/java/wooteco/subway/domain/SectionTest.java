package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Section 도메인 객체 테스트")
class SectionTest {

    @DisplayName("구간 거리가 1 보다 작을 경우 예외가 발생한다.")
    @Test
    void createSectionUnderDistance1() {
        // when & then
        assertThatThrownBy(() -> new Section(1L, 2L, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간 거리는 1 이상이어야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @DisplayName("지하철역의 아이디를 이용하여 구간에 속해있는지 확인한다.")
    void existStation(long stationId) {
        // given
        Section section = new Section(1L, 2L, 10);

        // when & then
        assertThat(section.existStation(stationId)).isTrue();
    }

    @DisplayName("추가하려는 구간이 종점인지 확인한다.")
    @Test
    void isAddingEndSection() {
        // given
        Section section = new Section(1L, 2L, 10);

        // when & then
        assertThat(section.isAddingEndSection(new Section(2L, 3L, 10)))
                .isTrue();
    }
}
