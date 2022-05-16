package wooteco.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import wooteco.subway.domain.line.Line;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineRequest {

    @NotNull
    private String name;

    @NotNull
    @Size(max=20)
    private String color;

    @NotNull
    private Long upStationId;

    @NotNull
    private Long downStationId;

    @Min(0)
    private int distance;

    public Line toLine(){
        return new Line(name, color);
    }
}
