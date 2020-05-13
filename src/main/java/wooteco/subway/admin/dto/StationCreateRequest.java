package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Station;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class StationCreateRequest {
    @NotBlank(message = "이름을 입력해주세요.")
    @Pattern(regexp = "^[^0-9].*", message = "숫자로 시작할 수 없습니다.")
    @Pattern(regexp = "[^\\s]*", message = "공백이 입력될 수 없습니다.")
    private String name;

    public StationCreateRequest() {
    }

    public StationCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
