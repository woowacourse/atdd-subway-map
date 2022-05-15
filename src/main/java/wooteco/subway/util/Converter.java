package wooteco.subway.util;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.response.StationResponse;

public class Converter {

    public static List<StationResponse> convertFromSections(final List<Section> sections) {
        final Station lastUpStation = sections.get(0).getUpStation();
        final List<StationResponse> response = sections.stream()
                .map(Section::getDownStation)
                .map(StationResponse::from)
                .collect(Collectors.toList());
        response.add(StationResponse.from(lastUpStation));
        return response;
    }
}
