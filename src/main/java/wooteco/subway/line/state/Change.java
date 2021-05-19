package wooteco.subway.line.state;

import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.station.domain.Station;

import java.util.List;

public abstract class Change implements State {
    private final Sections sections;

    protected Change(Sections sections) {
        this.sections = sections;
    }

    public Sections sections() {
        return sections;
    }

    @Override
    public State addSection(final Section targetSection) {
        Sections originSections = new Sections(sections.sections());
        sections.upwardEndPointRegistration(targetSection);
        if (isChange(originSections)) {
            return new Modified(this.sections);
        }

        sections.downwardEndPointRegistration(targetSection);
        if (isChange(originSections)) {
            return new Modified(this.sections);
        }

        sections.betweenUpwardRegistration(targetSection);
        if (isChange(originSections)) {
            return new Modified(this.sections);
        }

        sections.betweenDownwardRegistration(targetSection);
        if (isChange(originSections)) {
            return new Modified(this.sections);
        }

        return new UnModified(this.sections);
    }

    @Override
    public State deleteStation(final Station station) {
        Sections originSections = new Sections(sections.sections());
        sections.deleteStation(station);
        if (isChange(originSections)) {
            return new Modified(this.sections);
        }

        return new UnModified(this.sections);
    }

    @Override
    public List<Section> sortedSections() {
        return sections.sortedSections();
    }

    @Override
    public boolean existSection(final Station upStation, final Station downStation) {
        return sections.existSection(upStation, downStation);
    }

    @Override
    public boolean noContainStation(final Station upStation, final Station downStation) {
        return sections.noContainStation(upStation, downStation);
    }

    @Override
    public int size() {
        return sections.size();
    }

    private boolean isChange(final Sections originSections) {
        return !originSections.dirtyChecking(this.sections).isEmpty();
    }
}
