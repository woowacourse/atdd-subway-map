package wooteco.subway.controller.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionRequestTest {
    @Test
    @DisplayName("Request 생성에 필요한 정보가 Null 일 때 예외처리")
    void nullCreateException() {
        // given
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        // when
        SectionRequest sectionRequest = new SectionRequest(null, null,1);

        //then
        assertThat(validator.validate(sectionRequest).size()).isEqualTo(2);
    }
}