package wooteco.subway.domain;

import wooteco.subway.utils.ExceptionMessage;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class Sections {
    @NotBlank(message = "구간은 하나이상 등록되어 있어야 합니다.")
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> sortStations() {
        List<Long> stations = new ArrayList<>();

        Section targetSection = getFirstSection(sections.get(0));
        stations.add(targetSection.getUpStationId());

        while(!isLastStation(targetSection.getDownStationId())){
            stations.add(targetSection.getDownStationId());
            targetSection = findSectionByUpStation(targetSection.getDownStationId());
        }

        stations.add(targetSection.getDownStationId());
        return stations;
    }

    private Section getFirstSection(Section targetSection) {
        if(isFirstStation(targetSection.getUpStationId())){
            return targetSection;
        }
        Section previousSection = findSectionByDownStation(targetSection.getUpStationId());
        return getFirstSection(previousSection);
    }

    private Section findSectionByDownStation(long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId() == stationId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_SECTION));
    }

    private Section findSectionByUpStation(long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId() == stationId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_SECTION));
    }

    public boolean isFirstStation(long stationId) {
        return sections.stream()
                .noneMatch(s -> s.getDownStationId() == stationId);
    }

    public boolean isLastStation(long stationId) {
        return sections.stream()
                .noneMatch(s -> s.getUpStationId() == stationId);
    }

    public boolean isOnlyOneSection() {
        return sections.size() == 1;
    }
}
