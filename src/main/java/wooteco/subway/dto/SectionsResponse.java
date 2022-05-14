package wooteco.subway.dto;

import wooteco.subway.domain.Section;

import java.util.List;

public class SectionsResponse {

    private List<Section> sections;

    public SectionsResponse(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }
}
