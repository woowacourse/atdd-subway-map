package wooteco.subway.dto.response;

import wooteco.subway.domain.Section;

public class SectionResponse {
    private Section section;

    public SectionResponse() {
    }

    public SectionResponse(final Section section) {
//        this.section = section.getId();
    }

    public Section getSection() {
        return section;
    }
}
