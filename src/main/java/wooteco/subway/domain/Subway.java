package wooteco.subway.domain;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Subway {

    public void checkAbleToAdd(List<Line> presentLines, Line newLine) {
        Lines lines = new Lines(presentLines);
        lines.checkAbleToAdd(newLine);
    }

    public void checkAbleToAdd(List<Station> presentStations, Station newStation) {
        Stations stations = new Stations(presentStations);
        stations.checkAbleToAdd(newStation);
    }

    public List<Section> addSection(List<Section> presentSections, Section newSection) {
        Sections sections = new Sections(presentSections);
        sections.add(newSection);
        return sections.getSections();
    }

    public List<Section> deleteSection(List<Section> presentSections, Station deleteStation) {
        Sections sections = new Sections(presentSections);
        sections.delete(deleteStation);
        return sections.getSections();
    }
}
