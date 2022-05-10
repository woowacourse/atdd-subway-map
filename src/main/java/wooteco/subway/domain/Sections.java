package wooteco.subway.domain;

import java.util.List;

public class Sections {
    private List<Section> sections;


    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isFirstStation(Station station){
        return sections.get(0).equals(station);
    }

    public boolean isLastStation(Station station){
        return sections.get(sections.size()-1).equals(station);
    }
}
