package wooteco.subway.web.request;

import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LineRequest {

    @NotEmpty
    private String name;
    @NotEmpty
    private String color;

    public static LineRequest create(String name, String color) {
        return new LineRequest(name, color);
    }
}

