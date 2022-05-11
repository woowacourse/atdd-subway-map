package wooteco.subway.domain.dto;

import wooteco.subway.domain.Section;

public class RemoveStationResult {
    private Section removedUpEndSection;
    private Section removedDownEndSection;
    private Section mergedSection;
    private Section removedUpSection;
    private Section removedDownSection;
    private boolean isUpperEndSectionDeleted;
    private boolean isDownEndSectionDeleted;

    public RemoveStationResult(Section removedUpEndSection, Section removedDownEndSection,
                               boolean isUpperEndSectionDeleted,
                               boolean isDownEndSectionDeleted) {
        this.removedUpEndSection = removedUpEndSection;
        this.removedDownEndSection = removedDownEndSection;
        this.isUpperEndSectionDeleted = isUpperEndSectionDeleted;
        this.isDownEndSectionDeleted = isDownEndSectionDeleted;
    }

    public RemoveStationResult(Section mergedSection, Section removedUpSection,
                               Section removedDownSection) {
        this.mergedSection = mergedSection;
        this.removedUpSection = removedUpSection;
        this.removedDownSection = removedDownSection;
    }

    public static RemoveStationResult createWithRemovedUpEndSection(Section removedUpEndSection) {
        return new RemoveStationResult(removedUpEndSection, null, true, false);
    }

    public static RemoveStationResult createWithRemovedDownEndSection(Section removedDownEndSection) {
        return new RemoveStationResult(null, removedDownEndSection, false, true);
    }

    public static RemoveStationResult createWithMergedAndRemovedSections(Section mergedSection,
                                                                         Section removedUpSection,
                                                                         Section removedDownSection) {
        return new RemoveStationResult(mergedSection, removedUpSection, removedDownSection);
    }

    public Section getRemovedUpEndSection() {
        return removedUpEndSection;
    }

    public Section getRemovedDownEndSection() {
        return removedDownEndSection;
    }

    public Section getMergedSection() {
        return mergedSection;
    }

    public Section getRemovedUpSection() {
        return removedUpSection;
    }

    public Section getRemovedDownSection() {
        return removedDownSection;
    }

    public boolean isUpperEndSectionDeleted() {
        return isUpperEndSectionDeleted;
    }

    public boolean isDownEndSectionDeleted() {
        return isDownEndSectionDeleted;
    }
}
