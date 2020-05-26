package wooteco.subway.admin.dto;

import javax.validation.constraints.NotBlank;

public class StationCreateRequest {
    @NotBlank(message = "역 이름을 입력해주세요.")
    private String name;

    public String getName() {
        return name;
    }
}
