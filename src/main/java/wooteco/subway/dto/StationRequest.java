package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class StationRequest {
    @Length(max=255, message = "역 이름은 255자 이하여야 합니다.")
    @NotBlank(message = "역 이름은 공백일 수 없습니다.")
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
