package wooteco.subway.dto;

import wooteco.subway.domain.Section;

public class SectionsToBeCreatedAndUpdated {

    private final Section sectionToBeCreated;
    private final Section sectionToBeUpdated;

    public SectionsToBeCreatedAndUpdated(Section sectionToBeCreated, Section sectionToBeUpdated) {
        this.sectionToBeCreated = sectionToBeCreated;
        this.sectionToBeUpdated = sectionToBeUpdated;
    }

    public SectionsToBeCreatedAndUpdated(Section sectionToBeCreated) {
        this(sectionToBeCreated, null);
    }

    public Section getSectionToBeCreated() {
        return sectionToBeCreated;
    }

    public Section getSectionToBeUpdated() {
        return sectionToBeUpdated;
    }
}
