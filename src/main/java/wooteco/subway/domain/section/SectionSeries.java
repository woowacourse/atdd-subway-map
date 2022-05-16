package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.IdMissingException;
import wooteco.subway.exception.RowNotFoundException;
import wooteco.subway.exception.SectionNotEnoughException;
import wooteco.subway.util.CollectorsUtils;

public class SectionSeries {

    private static final SectionSeriesSorter SORTER = new SectionSeriesSorter();

    private final List<Section> sections;

    public SectionSeries(List<Section> sections) {
        validateHasId(sections);
        this.sections = new ArrayList<>(SORTER.sort(sections));
    }

    private void validateHasId(List<Section> sections) {
        sections.stream()
            .filter(section -> section.getId() == null)
            .findAny()
            .ifPresent(section -> {
                throw new IdMissingException(
                    String.format("상행 %s, 하행 %s 구간에 ID가 없습니다.", section.getUpStation().getName(),
                        section.getDownStation().getName()));
            });
    }

    public void add(Section section) {
        if (sections.isEmpty() || isAppending(section.getUpStation(), section.getDownStation())) {
            this.sections.add(section);
            SORTER.sort(sections);
            return;
        }
        insertSection(section);
        SORTER.sort(sections);
    }

    private boolean isAppending(Station upStation, Station downStation) {
        return isUpTerminal(downStation) || isDownTerminal(upStation);
    }

    private boolean isDownTerminal(Station station) {
        return sections.get(getLastIndex()).isDownStationSame(station);
    }

    private boolean isUpTerminal(Station station) {
        return sections.get(getFirstIndex()).isUpStationSame(station);
    }

    private int getFirstIndex() {
        return 0;
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    private void insertSection(Section newSection) {
        final Section findSection = findIntermediateSection(newSection);
        this.sections.remove(findSection);
        this.sections.add(newSection);
        this.sections.add(findSection.divide(newSection));
    }

    private Section findIntermediateSection(Section newSection) {
        return sections.stream()
            .filter(section -> section.isDividable(newSection))
            .collect(CollectorsUtils.findOneCertainly())
            .orElseThrow(() -> new RowNotFoundException(
                String.format("%s 혹은 %s와 같은 방향의 구간을 찾지 못했습니다.",
                    newSection.getUpStation().getName(),
                    newSection.getDownStation().getName())
            ));
    }

    public void remove(Station deleteStation) {
        validateSectionEnough();
        if (isUpTerminal(deleteStation)) {
            this.sections.remove(getFirstIndex());
            SORTER.sort(sections);
            return;
        }
        if (isDownTerminal(deleteStation)) {
            this.sections.remove(getLastIndex());
            SORTER.sort(sections);
            return;
        }
        removeIntermediate(deleteStation);
        SORTER.sort(sections);
    }

    private void validateSectionEnough() {
        if (this.sections.size() <= 1) {
            throw new SectionNotEnoughException("구간이 하나인 경우에는 삭제할 수 없습니다.");
        }
    }

    private void removeIntermediate(Station deleteStation) {
        final int upIndex = findIntermediateStation(deleteStation);
        final Section upSection = sections.get(upIndex);
        final Section downSection = sections.get(upIndex + 1);

        sections.add(upSection.connect(downSection));
        sections.remove(upSection);
        sections.remove(downSection);
        SORTER.sort(sections);
    }

    private int findIntermediateStation(Station station) {
        return IntStream.range(getFirstIndex(), sections.size())
            .filter(it -> sections.get(it).isDownStationSame(station))
            .findAny()
            .orElseThrow(() -> new RowNotFoundException("삭제하려는 역이 구간에 등록되어 있지 않습니다."));
    }

    public List<Section> getSections() {
        return sections;
    }
}
