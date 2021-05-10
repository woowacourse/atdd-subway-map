package wooteco.subway.web.request;

import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class StationRequest {

    @NotEmpty
    private String name;

    public static StationRequest create(String name) {
        return new StationRequest(name);
    }
}
