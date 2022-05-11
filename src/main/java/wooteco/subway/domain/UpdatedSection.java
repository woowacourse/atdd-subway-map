package wooteco.subway.domain;

public class UpdatedSection {
    private final Long deletedSectionId;
    private final Section updatedSection;
    private final boolean hasUpdatedSection;

    public UpdatedSection(Long deletedSectionId, Section updatedSection, boolean hasUpdatedSection) {
        this.deletedSectionId = deletedSectionId;
        this.updatedSection = updatedSection;
        this.hasUpdatedSection = hasUpdatedSection;
    }

    public static UpdatedSection of(Long deletedSectionId) {
        return new UpdatedSection(deletedSectionId, new Section(), false);
    }

    public static UpdatedSection from(Long deletedSectionId, Section updatedSection) {
        return new UpdatedSection(deletedSectionId, updatedSection, true);
    }

    public boolean hasUpdatedSection() {
        return this.hasUpdatedSection;
    }

    public Long getDeletedSectionId() {
        return deletedSectionId;
    }

    public Section getUpdatedSection() {
        return updatedSection;
    }
}
