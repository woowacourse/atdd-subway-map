package wooteco.subway.dto.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@NotBlank
@Pattern(regexp = "^[가-힣0-9]+$")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Name {

    String message() default "적절한 형식의 이름을 입력해주세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
