package wooteco.subway.domain.line.value.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.line.NegativeIdException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionIdTest {

    @DisplayName("sectionId는 음수일 수 없다.")
    @Test
    void sectionId() {
        assertThatThrownBy(() -> new SectionId(-1L))
                .isInstanceOf(NegativeIdException.class)
                .hasMessage("id값은 음수일 수 없습니다.");
    }

    @Test
    void empty() {
        assertThat(SectionId.empty().intValue()).isEqualTo(-1);
    }

    @Test
    void intValue() {
        assertThat(new SectionId(0L).intValue()).isEqualTo(0);
    }

    @Test
    void longValue() {
        assertThat(new SectionId(0L).longValue()).isEqualTo(0L);
    }

    @Test
    void floatValue() {
        assertThat(new SectionId(0L).floatValue()).isEqualTo(0F);
    }

    @Test
    void doubleValue() {
        assertThat(new SectionId(0L).doubleValue()).isEqualTo(0D);
    }
}