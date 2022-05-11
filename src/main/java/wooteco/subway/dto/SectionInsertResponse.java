package wooteco.subway.dto;

import java.util.List;

public class SectionInsertResponse {
    private List<SectionResponse> savedSectionResponses;
    private SectionResponse deletedSectionResponse;

    public SectionInsertResponse(List<SectionResponse> savedSectionResponses,
                                 SectionResponse deletedSectionResponse) {
        this.savedSectionResponses = savedSectionResponses;
        this.deletedSectionResponse = deletedSectionResponse;
    }

    public SectionInsertResponse(List<SectionResponse> savedSectionResponses) {
        this.savedSectionResponses = savedSectionResponses;
    }

    public SectionInsertResponse(SectionResponse deletedSectionResponse) {
        this.deletedSectionResponse = deletedSectionResponse;
    }

    public SectionResponse getDeletedSectionResponse() {
        return deletedSectionResponse;
    }
}
