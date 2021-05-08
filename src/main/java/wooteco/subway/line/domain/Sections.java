package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.util.*;

public class Sections {
    private final List<Section> sections;

    public Sections() {
        this(new ArrayList<>());
    }

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void add(final Section section) {
        this.sections.add(section);
    }

    public List<Section> sections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Section> sortedSections() {
        List<Section> sortedSections = new LinkedList<>();
        Section headSection = headSection();
        sortedSections.add(headSection);
        for (int i = 0; i < sections.size(); i++) {
            Section finalHeadSection = headSection;
            Optional<Section> findSection = sections.stream()
                    .filter(section -> !section.equals(finalHeadSection))
                    .filter(section -> section.upStation().equals(finalHeadSection.downStation()))
                    .findFirst();
            if (findSection.isPresent()) {
                sortedSections.add(findSection.get());
                headSection = findSection.get();
            }
        }
        return Collections.unmodifiableList(sortedSections);
    }

    public void upwardEndPointRegistration(Line line, Station targetUpStation, Station targetDownStation, int targetDistance) {
        Section headSection = headSection();
        if (headSection.sameUpStation(targetDownStation)) {
            this.sections.add(new Section(line, targetUpStation, targetDownStation, targetDistance));
        }
    }

    private Section headSection() {
        for (Section source : sections) {
            if (matchesCount(sections, source) == 0) {
                return source;
            }
        }
        throw new IllegalStateException("구간이 제대로 등록되어있지 않음!");
    }

    private int matchesCount(List<Section> sections, Section section) {
        Long headStationId = section.upStation().getId();
        int checkCount = 0;
        for (Section target : sections) {
            if (section.equals(target)) {
                continue;
            }
            if (headStationId.equals(target.upStation().getId()) || headStationId.equals(target.downStation().getId())) {
                checkCount++;
            }
        }
        return checkCount;
    }

    public List<Section> changedSections(final Sections sections) {
        List<Section> changedSections = new ArrayList<>();
        for (Section section : sections.sections) {
            if (!this.sections.contains(section)) {
                changedSections.add(section);
            }
        }
        return changedSections;
    }
}
