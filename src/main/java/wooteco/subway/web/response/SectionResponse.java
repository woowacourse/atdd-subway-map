package wooteco.subway.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wooteco.subway.domain.Section;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectionResponse {

    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

    public static SectionResponse create(Section section) {
        return new SectionResponse(
                section.getId(),
                StationResponse.create(section.getUpStation()),
                StationResponse.create(section.getDownStation()),
                section.getDistance()
        );
    }
}
