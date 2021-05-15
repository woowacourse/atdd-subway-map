package wooteco.subway.line.state;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.station.domain.Station;

public abstract class Change implements State {
    private final Sections sections;

    protected Change(Sections sections) {
        this.sections = sections;
    }

    public Sections sections() {
        return sections;
    }

    @Override
    public State addSection(Line line, Section targetSection) {
        Sections originSections = new Sections(sections.sections());
        sections.upwardEndPointRegistration(line, targetSection);
        if (isChange(originSections)) {
            return new Modified(this.sections);
        }

        sections.downwardEndPointRegistration(line, targetSection);
        if (isChange(originSections)) {
            return new Modified(this.sections);
        }

        sections.betweenUpwardRegistration(line, targetSection);
        if (isChange(originSections)) {
            return new Modified(this.sections);
        }

        sections.betweenDownwardRegistration(line, targetSection);
        if (isChange(originSections)) {
            return new Modified(this.sections);
        }

        return new UnModified(this.sections);
    }

    @Override
    public State deleteStation(Station station) {
        Sections originSections = new Sections(sections.sections());
        sections.deleteStation(station);
        if (isChange(originSections)) {
            return new Modified(this.sections);
        }

        return new UnModified(this.sections);
    }

    @Override
    public boolean containStation(Station station) {
        return sections.containStation(station);
    }

    @Override
    public State changeSections(Sections sections) {
        return new UnModified(sections);
    }

    private boolean isChange(Sections originSections) {
        return !originSections.changedSections(this.sections).isEmpty();
    }
}
