package wooteco.subway.domain;

import java.util.Optional;

public class EnrollSections {
    private final Section createSection;
    private final Optional<Section> updateSection;

    public EnrollSections(Section createSection, Optional<Section> updateSection) {
        this.createSection = createSection;
        this.updateSection = updateSection;
    }

    public Section getCreateSection() {
        return createSection;
    }

    public Optional<Section> getUpdateSection() {
        return updateSection;
    }
}
