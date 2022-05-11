package wooteco.subway.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.response.StationResponse;

public class Converter {

    public static List<StationResponse> convertFromSections(final Sections sections) {
        final List<Section> existedSections = sections.getValue();
        final Station lastUpStation = existedSections.get(0).getUpStation();
        final List<StationResponse> response = existedSections.stream()
                .map(Section::getDownStation)
                .map(StationResponse::from)
                .collect(Collectors.toList());
        response.add(StationResponse.from(lastUpStation));
        return response;
    }
}
