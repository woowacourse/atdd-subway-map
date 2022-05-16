package wooteco.subway.dto.request;

import java.util.Objects;
import wooteco.subway.domain.Station;

public class StationRequest {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private String name;

    private StationRequest() {
    }

    public StationRequest(String name) {
        Objects.requireNonNull(name, ERROR_NULL);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toEntity() {
        return new Station(this.name);
    }
}
