package wooteco.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationTest {

    @DisplayName("지하철 역 객체 생성한다.")
    @Test
    void create() {
        //given
        String name = "강남역";

        //when
        Station station = new Station(name);

        //then
        assertThat(station).isInstanceOf(Station.class);
        assertThat(station).isEqualTo(new Station("강남역"));
    }

    @DisplayName("지하철 역 이름을 null로 생성한다.")
    @Test
    void createNullName() {
        //given
        String name = null;

        //when then
        assertThatThrownBy(() -> new Station(name))
                .isInstanceOf(NullPointerException.class).hasMessage("지하철 역의 이름은 null일 수 없습니다.");
    }

    @DisplayName("지하철 역 이름을 빈 값으로 생성한다.")
    @Test
    void createEmptyName() {
        //given
        String name = "";

        //when then
        assertThatThrownBy(() -> new Station(name))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("지하철 역의 이름은 빈 값일 수 없습니다.");
    }

    @DisplayName("역 이름이 같으면 true를 반환한다.")
    @Test
    void isSameName() {
        //given
        String name = "역이름";
        Station station1 = new Station(name);

        //when
        boolean result = station1.isSameName(name);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("역 이름이 같으면 true를 반환한다.")
    @Test
    void isSameNameInsertDifferentName() {
        //given
        String name = "역이름";
        Station station1 = new Station(name);
        String otherName = "다른 이름";

        //when
        boolean result = station1.isSameName(otherName);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("역 Id가 같으면 true를 반환한다.")
    @Test
    void isSameId() {
        //given
        Long id = 1L;
        String name = "역이름";
        Station station1 = new Station(1L, name);
        Long otherId = 100000L;

        //when
        boolean result = station1.isSameId(otherId);

        //then
        assertThat(result).isFalse();
    }
}