package wooteco.subway.dto;

import lombok.Getter;
import wooteco.subway.domain.Section;

@Getter
public class SectionRequest {

      public Long upStationId;
      public Long downStationId;
      public Integer distance;

    public Section toSection() {
        return new Section(
                this.getUpStationId(),
                this.getDownStationId(),
                this.getDistance()
        );
    }
}
