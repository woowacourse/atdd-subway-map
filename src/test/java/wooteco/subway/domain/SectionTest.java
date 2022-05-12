package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.IllegalSectionCreatedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {

    @DisplayName("구간이 정상적으로 생성되는 지 확인한다.")
    @Test
    void create_section() {
        new Section(1L, 2L, 5);
    }

    @DisplayName("null 입력 시 예외 발생을 확인한다.")
    @Test
    void invalid_station_null() {
        assertThatThrownBy(() -> new Section(null, 2L, 10))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("거리가 0일 때 예외 발생을 확인한다.")
    @Test
    void invalid_distance() {
        assertThatThrownBy(() -> new Section(1L, 2L, 0))
                .isInstanceOf(IllegalSectionCreatedException.class);
    }

    @DisplayName("역 아이디가 같을 때 예외 발생을 확인한다.")
    @Test
    void same_station_ids() {
        assertThatThrownBy(() -> new Section(2L, 2L, 10))
                .isInstanceOf(IllegalSectionCreatedException.class);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같은 경우 예외 발생을 확인한다.")
    @Test
    void distance_larger_than_exception() {
        final Section section = new Section(1L, 2L, 10);
        final Section targetSection = new Section(1L, 3L, 5);
        assertThatThrownBy(() -> targetSection.validateDistanceLargerThan(section))
                .isInstanceOf(IllegalSectionCreatedException.class);
    }

    @DisplayName("순 방향으로 연결되는 구간 일 때 True 를 반환하는 지 확인한다.")
    @Test
    void is_connected() {
        final Section section1 = new Section(1L, 2L, 10);
        final Section section2 = new Section(2L, 3L, 10);
        final boolean actual = section1.isConnected(section2);

        assertThat(actual).isTrue();
    }

    @DisplayName("역 방향으로 연결되는 구간 일 때 False 를 반환하는 지 확인한다.")
    @Test
    void is_connected_reverse() {
        final Section section1 = new Section(1L, 2L, 10);
        final Section section2 = new Section(2L, 3L, 10);
        final boolean actual = section2.isConnected(section1);

        assertThat(actual).isFalse();
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 하행역이 같을 때 추가하는 구간이 제대로 생성되는 지 확인한다.")
    @Test
    void divide_right() {
        final Section targetSection = new Section(1L, 3L, 10);
        final Section section = new Section(2L, 3L, 4);
        final Section dividedSection = targetSection.divideLeft(section);
        final int actual = dividedSection.getDistance();

        assertThat(actual).isEqualTo(6);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 상행역이 같을 때 추가하는 구간이 제대로 생성되는 지 확인한다.")
    @Test
    void divide_left() {
        final Section targetSection = new Section(1L, 3L, 10);
        final Section section = new Section(1L, 2L, 4);
        final Section dividedSection = targetSection.divideRight(section);
        final int actual = dividedSection.getDistance();

        assertThat(actual).isEqualTo(6);
    }

    @DisplayName("두 구간의 상행역 Id가 같으면 True 를 반환하는 지 확인한다.")
    @Test
    void equals_up_station_id_true() {
        final Section section1 = new Section(1L, 2L, 10);
        final Section section2 = new Section(1L, 3L, 10);
        final boolean actual = section1.equalsUpStation(section2);

        assertThat(actual).isTrue();
    }

    @DisplayName("두 구간의 상행역 Id가 다르면 False 를 반환하는 지 확인한다.")
    @Test
    void equals_up_station_id_false() {
        final Section section1 = new Section(1L, 2L, 10);
        final Section section2 = new Section(4L, 3L, 10);
        final boolean actual = section1.equalsUpStation(section2);

        assertThat(actual).isFalse();
    }

    @DisplayName("두 구간의 하행역 Id가 같으면 True 를 반환하는 지 확인한다.")
    @Test
    void equals_down_station_id_true() {
        final Section section1 = new Section(1L, 3L, 10);
        final Section section2 = new Section(2L, 3L, 10);
        final boolean actual = section1.equalsDownStation(section2);

        assertThat(actual).isTrue();
    }

    @DisplayName("두 구간의 하행역 Id가 다르면 False 를 반환하는 지 확인한다.")
    @Test
    void equals_down_station_id_false() {
        final Section section1 = new Section(1L, 3L, 10);
        final Section section2 = new Section(2L, 4L, 10);
        final boolean actual = section1.equalsUpStation(section2);

        assertThat(actual).isFalse();
    }

    @DisplayName("인자로 전달 된 구간의 상행역 Id가 있으면 True 를 반환하는 지 확인한다.")
    @Test
    void contains_up_station_id_by_true() {
        final Section section1 = new Section(2L, 3L, 10);
        final Section section2 = new Section(3L, 4L, 10);
        final boolean actual = section1.containsUpStationIdBy(section2);

        assertThat(actual).isTrue();
    }

    @DisplayName("인자로 전달 된 구간의 상행역 Id가 없으면 False 를 반환하는 지 확인한다.")
    @Test
    void contains_up_station_id_by_false() {
        final Section section1 = new Section(3L, 4L, 10);
        final Section section2 = new Section(2L, 3L, 10);
        final boolean actual = section1.containsUpStationIdBy(section2);

        assertThat(actual).isFalse();
    }

    @DisplayName("인자로 전달 된 구간의 하행역 Id가 있으면 True 를 반환하는 지 확인한다.")
    @Test
    void contains_down_station_id_by_true() {
        final Section section1 = new Section(2L, 3L, 10);
        final Section section2 = new Section(4L, 3L, 10);
        final boolean actual = section1.containsDownStationIdBy(section2);

        assertThat(actual).isTrue();
    }

    @DisplayName("인자로 전달 된 구간의 하행역 Id가 없으면 False 를 반환하는 지 확인한다.")
    @Test
    void contains_down_station_id_by_false() {
        final Section section1 = new Section(4L, 3L, 10);
        final Section section2 = new Section(2L, 5L, 10);
        final boolean actual = section1.containsDownStationIdBy(section2);

        assertThat(actual).isFalse();
    }
}