package wooteco.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private LineRequest() {
    }

    public Line toLine() {
        return new Line(
                this.getName(),
                this.getColor()
        );
    }

    public Section toSection() {
        return new Section(
                this.getUpStationId(),
                this.getDownStationId(),
                this.getDistance()
        );
    }
}
