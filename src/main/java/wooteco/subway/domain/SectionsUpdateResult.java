package wooteco.subway.domain;

import java.util.Collections;
import java.util.List;

public class SectionsUpdateResult {

    private final List<Section> deletedSections;
    private final List<Section> addedSections;

    public SectionsUpdateResult(final List<Section> deletedSections, final List<Section> addedSections) {
        this.deletedSections = deletedSections;
        this.addedSections = addedSections;
    }

    public List<Section> getDeletedSections() {
        return Collections.unmodifiableList(deletedSections);
    }

    public List<Section> getAddedSections() {
        return Collections.unmodifiableList(addedSections);
    }
}
