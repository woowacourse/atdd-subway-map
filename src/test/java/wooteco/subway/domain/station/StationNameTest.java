package wooteco.subway.domain.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import wooteco.subway.domain.line.LineColor;

class StationNameTest {

    @DisplayName("지하철역 이름은 공백이 될 수 없다.")
    @ParameterizedTest(name = "{index} 입력 : \"{0}\"")
    @ValueSource(strings = {"", " "})
    void createWithBlankName(String name) {
        assertThatThrownBy(() -> new StationName(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철역 이름은 공백이 될 수 없습니다.");
    }

    @DisplayName("지하철역 이름을 비교한다.")
    @ParameterizedTest
    @CsvSource(value = {"강남역,선릉역,false", "광교역,광교역,true"})
    void equals(String name1, String name2, boolean expected) {
        StationName stationName1 = new StationName(name1);
        StationName stationName2 = new StationName(name2);
        assertThat(stationName1.equals(stationName2)).isEqualTo(expected);
    }
}
