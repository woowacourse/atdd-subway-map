package wooteco.subway.dto.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotBlank
public @interface SectionInfo {

    String message() default "생성할 구간 정보를 입력해주세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}