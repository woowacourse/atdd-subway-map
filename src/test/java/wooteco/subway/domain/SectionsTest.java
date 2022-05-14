package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.SubwayException;

public class SectionsTest {

    @DisplayName("전체 구간들을 정렬한 결과가 올바른지 테스트")
    @Test
    void get_all_stations() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(1L, 2L, 3L, 4L));
    }

    @DisplayName("사이클이 아닌 노선에서 하행 종점이 바뀌고 사이클을 만들지 않는 구간을 오른쪽으로 더할 때 결과가 올바른지 테스트")
    @Test
    void add_right_when_change_end_point_no_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        sections.add(new Section(4L, 5L, 2L));
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(1L, 2L, 3L, 4L, 5L));
    }

    @DisplayName("사이클이 아닌 노선에서 사이클을 만들지 않는 구간을 오른쪽으로 더할 때 결과가 올바른지 테스트")
    @Test
    void add_right_no_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        sections.add(new Section(3L, 5L, 2L));
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(1L, 2L, 3L, 5L, 4L));
    }

    @DisplayName("사이클이 아닌 노선에서 상행 종점이 바뀌고 사이클을 만들지 않는 구간을 왼쪽으로 더할 때 결과가 올바른지 테스트")
    @Test
    void add_left_when_change_end_point_no_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        sections.add(new Section(5L, 1L, 2L));
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(5L, 1L, 2L, 3L, 4L));
    }

    @DisplayName("사이클이 아닌 노선에서 사이클을 만들지 않는 구간을 왼쪽으로 더할 때 결과가 올바른지 테스트")
    @Test
    void add_left_no_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        sections.add(new Section(5L, 3L, 2L));
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(1L, 2L, 5L, 3L, 4L));
    }

    @DisplayName("사이클이 아닌 노선에서 사이클을 만드는 구간을 더할 때 결과가 올바른지 테스트")
    @Test
    void add_then_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        sections.add(new Section(4L, 1L, 2L));
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(1L, 2L, 3L, 4L, 1L));
    }

    @DisplayName("사이클인 노선에서 구간을 왼쪽으로 더할 때 결과가 올바른지 테스트")
    @Test
    void add_left_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L),
                new Section(4L, 1L, 5L)));
        sections.add(new Section(5L, 1L, 2L));
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(1L, 2L, 3L, 4L, 5L, 1L));
    }

    @DisplayName("사이클인 노선에서 구간을 오른쪽으로 더할 때 결과가 올바른지 테스트")
    @Test
    void add_right_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L),
                new Section(4L, 1L, 5L)));
        sections.add(new Section(1L, 5L, 2L));
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(1L, 5L, 2L, 3L, 4L, 1L));
    }

    @DisplayName("사이클이 아닌 노선에서 거리 조건이 맞지 않는 구간을 오른쪽으로 더할 때 예외가 발생하는지 테스트")
    @Test
    void cannot_add_right_when_distance_is_too_large_no_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        assertThatThrownBy(() -> sections.add(new Section(3L, 5L, 100L)))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("사이클이 아닌 노선에서 거리 조건이 맞지 않는 구간을 왼쪽으로 더할 때 예외가 발생하는지 테스트")
    @Test
    void cannot_add_left_when_distance_is_too_large_no_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        assertThatThrownBy(() -> sections.add(new Section(5L, 3L, 100L)))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("사이클인 노선에서 거리 조건이 맞지 않는 구간을 오른쪽으로 더할 때 예외가 발생하는지 테스트")
    @Test
    void cannot_add_right_when_distance_is_too_large_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L),
                new Section(4L, 1L, 8L)));
        assertThatThrownBy(() -> sections.add(new Section(1L, 5L, 100L)))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("사이클인 노선에서 거리 조건이 맞지 않는 구간을 왼쪽으로 더할 때 예외가 발생하는지 테스트")
    @Test
    void cannot_add_left_when_distance_is_too_large_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L),
                new Section(4L, 1L, 8L)));
        assertThatThrownBy(() -> sections.add(new Section(5L, 1L, 100L)))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("사이클이 아닌 노선에서 갈 수 있는 구간을 추가하는 경우 예외가 발생하는지 테스트")
    @Test
    void cannot_add_when_already_exist_no_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        assertThatThrownBy(() -> sections.add(new Section(1L, 4L, 100L)))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("사이클인 노선에서 갈 수 있는 구간을 추가하는 경우 예외가 발생하는지 테스트")
    @Test
    void cannot_add_when_already_exist_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L),
                new Section(4L, 1L, 10L)));
        assertThatThrownBy(() -> sections.add(new Section(4L, 3L, 100L)))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("전체 사이클이 아닌 부분 사이클을 형성하는 구간을 추가할 경우 예외가 발생하는지 테스트")
    @Test
    void cannot_add_when_sub_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        assertThatThrownBy(() -> sections.add(new Section(4L, 2L, 100L)))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("구간의 상행역과 하행역이 현재 노선에 없는 구간을 추가할 경우 예외가 발생하는지 테스트")
    @Test
    void cannot_add_when_sub_no_shared_station() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        assertThatThrownBy(() -> sections.add(new Section(5L, 6L, 100L)))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("사이클이 아닌 노선에서 종점이 아닌 역을 삭제할 경우 올바른지 테스트")
    @Test
    void delete_no_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        sections.delete(1L, 2L);
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(1L, 3L, 4L));
    }

    @DisplayName("사이클이 아닌 노선에서 상행 종점인 역을 삭제할 경우 올바른지 테스트")
    @Test
    void delete_up_end_point_no_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        sections.delete(1L, 1L);
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(2L, 3L, 4L));
    }

    @DisplayName("사이클이 아닌 노선에서 하행 종점인 역을 삭제할 경우 올바른지 테스트")
    @Test
    void delete_down_end_point_no_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L)));
        sections.delete(1L, 4L);
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(1L, 2L, 3L));
    }

    @DisplayName("사이클인 노선에서 특정 역을 삭제할 경우 올바른지 테스트")
    @Test
    void delete_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 3L, 7L),
                new Section(3L, 4L, 9L),
                new Section(4L, 1L, 10L)));
        sections.delete(1L, 1L);
        List<Long> result = sections.getStations();
        assertThat(result).isEqualTo(List.of(2L, 3L, 4L));
    }

    @DisplayName("사이클이 아닌 노선에서 구간이 하나만 있을 때 특정 역을 삭제할 경우 예외가 발생하는지 테스트")
    @Test
    void delete_when_only_one_section_no_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L)));
        assertThatThrownBy(() -> sections.delete(1L, 1L))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("사이클인 노선에서 구간이 두 개만 있을 때 특정 역을 삭제할 경우 예외가 발생하는지 테스트")
    @Test
    void delete_when_only_two_section_cycle() {
        Sections sections = new Sections(List.of(new Section(1L, 2L, 10L),
                new Section(2L, 1L, 10L)));
        assertThatThrownBy(() -> sections.delete(1L, 1L))
                .isInstanceOf(SubwayException.class);
    }
}
