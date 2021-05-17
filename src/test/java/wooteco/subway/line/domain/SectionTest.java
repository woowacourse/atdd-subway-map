package wooteco.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class SectionTest {

    @DisplayName("구간의 상행, 하행은 다른 역이어야 합니다.")
    @Test
    public void testValidateCreateSection_whenSameUpDownStation() {
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new Section(1L, 1L, 10);
        });
    }

    @DisplayName("구간의 길이는 0이상 이어야 합니다.")
    @Test
    public void testValidateCreateSection_whenDistamceZeroOrNegative() {
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new Section(1L, 1L, 0);
        });

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new Section(1L, 1L, -1);
        });
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    public void testValidateUpdateWhenAdd() {
        //given
        Section oldSection = new Section(2L, 3L, 10);
        Section newSection = new Section(2L, 5L, 30);

        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            oldSection.updateWhenAdd(newSection);
        });
    }
}
