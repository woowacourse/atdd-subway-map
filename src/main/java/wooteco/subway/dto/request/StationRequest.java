package wooteco.subway.dto.request;

import javax.validation.constraints.NotBlank;

public class StationRequest {

    @NotBlank(message = "역의 이름이 입력되지 않았습니다.")
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StationRequest{" + "name='" + name + '\'' + '}';
    }
}
