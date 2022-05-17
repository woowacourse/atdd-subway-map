package wooteco.subway.dto;

import wooteco.subway.domain.Section;

public class SectionsToBeDeletedAndUpdated {

    private final Section sectionToBeRemoved;
    private final Section sectionToBeUpdated;

    public SectionsToBeDeletedAndUpdated(Section sectionToBeRemoved, Section sectionToBeUpdated) {
        this.sectionToBeRemoved = sectionToBeRemoved;
        this.sectionToBeUpdated = sectionToBeUpdated;
    }

    public SectionsToBeDeletedAndUpdated(Section sectionToBeRemoved) {
        this(sectionToBeRemoved, null);
    }

    public Section getSectionToBeRemoved() {
        return sectionToBeRemoved;
    }

    public Section getSectionToBeUpdated() {
        return sectionToBeUpdated;
    }
}
