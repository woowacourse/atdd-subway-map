package wooteco.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import wooteco.subway.domain.section.Section;

import javax.validation.constraints.Min;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectionRequest {

    @NotNull
    private Long upStationId;

    @NotNull
    private Long downStationId;

    @Min(0)
    private int distance;

    public Section toSection(Long lineId){
        return new Section(lineId, upStationId, downStationId, distance);
    }
}
