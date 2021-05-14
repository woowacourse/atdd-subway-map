package wooteco.subway.web.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wooteco.subway.domain.Section;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SectionResponse {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public static SectionResponse create(Section section) {
        return new SectionResponse(
            section.getId(),
            section.upStationId(),
            section.downStationId(),
            section.getDistance()
        );
    }
}
