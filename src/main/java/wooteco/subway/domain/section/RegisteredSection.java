package wooteco.subway.domain.section;

public class RegisteredSection {

    private final Long lineId;
    private final String lineName;
    private final String lineColor;
    private final Section section;

    public RegisteredSection(Long lineId,
                             String lineName,
                             String lineColor,
                             Section section) {
        this.lineId = lineId;
        this.lineName = lineName;
        this.lineColor = lineColor;
        this.section = section;
    }

    public Long getLineId() {
        return lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public String getLineColor() {
        return lineColor;
    }

    public Section getSection() {
        return section;
    }
}
