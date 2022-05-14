package wooteco.subway.dto;

import wooteco.subway.domain.Section;

public class DeleteAndUpdateSectionsInfo {

    private final Section sectionToBeRemoved;
    private final Section sectionToBeUpdated;

    public DeleteAndUpdateSectionsInfo(Section sectionToBeRemoved, Section sectionToBeUpdated) {
        this.sectionToBeRemoved = sectionToBeRemoved;
        this.sectionToBeUpdated = sectionToBeUpdated;
    }

    public DeleteAndUpdateSectionsInfo(Section sectionToBeRemoved) {
        this(sectionToBeRemoved, null);
    }

    public Section getSectionToBeRemoved() {
        return sectionToBeRemoved;
    }

    public Section getSectionToBeUpdated() {
        return sectionToBeUpdated;
    }
}
