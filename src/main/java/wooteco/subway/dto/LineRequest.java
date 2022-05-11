package wooteco.subway.dto;

import lombok.Getter;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

@Getter
public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

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
