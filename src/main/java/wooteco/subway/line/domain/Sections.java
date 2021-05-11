package wooteco.subway.line.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    public static final String ERROR_SECTION_GRATER_OR_EQUALS_LINE_DISTANCE = "구간의 길이가 노선의 길이보다 크거나 같을 수 없습니다.";
    public static final String ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE = "상행역과 하행역 둘 중 하나가 노선에 존재해야 합니다.";

    private final LinkedList<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sort(sections);
    }

    public List<Section> toList() {
        return sections;
    }


    public void checkAddSectionValidation(Section section) {
        if (hasStation(section.getUpStationId()) == hasStation(section.getDownStationId())) {
            throw new IllegalArgumentException(ERROR_SECTION_HAVE_TO_ONE_STATION_IN_LINE);
        }

        System.out.println("홀리몰리홀리몰리 : " + checkEndPoint(section));
        if (!checkEndPoint(section) && sumSectionDistance() <= section.getDistance()) {
            throw new IllegalArgumentException(ERROR_SECTION_GRATER_OR_EQUALS_LINE_DISTANCE);
        }
    }

    private boolean checkEndPoint(Section section) {
        Section upSection = sections.getFirst();
        Section downSection = sections.getLast();
        System.out.println("홀리몰리홀리몰리 업 : " + upSection);
        System.out.println("홀리몰리홀리몰리 다운 : " + downSection);
        System.out.println("홀리몰리홀리몰리 추가되는거 : " + section);
        return section.getDownStationId().equals(upSection.getUpStationId())
                || section.getUpStationId().equals(downSection.getDownStationId());
    }


    public int sumSectionDistance() {
        return sections.stream().mapToInt(Section::getDistance).sum();
    }

    private boolean hasStation(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId)
                        || section.getDownStationId().equals(stationId))
                .count() == 1;
    }

    private LinkedList<Section> sort(List<Section> sections) {
        LinkedList<Section> sortedSection = new LinkedList<>();
        Section firstSection = findStartSection(sections);
        sortedSection.add(firstSection);

        while (sortedSection.size() < sections.size()) {
            Section lastSection = sortedSection.get(sortedSection.size() - 1);
            Section nextSection = findNextSection(sections, lastSection.getDownStationId());
            sortedSection.add(nextSection);
        }

        return sortedSection;
    }


    private Section findNextSection(List<Section> sections, Long downStationId) {
        Optional<Section> nextSection = sections.stream()
                .filter(section -> downStationId.equals(section.getUpStationId()))
                .findFirst();

        if (nextSection.isPresent()) {
            return nextSection.get();
        }

        throw new IllegalArgumentException("다음역을 찾을 수 없습니다.");
    }


    private Section findStartSection(List<Section> sections) {
        List<Long> downStationIdList = getDownStationIdList(sections);


        Optional<Section> startSection = sections.stream()
                .filter(section -> !downStationIdList.contains(section.getUpStationId()))
                .findFirst();

        if (startSection.isPresent()) {
            return startSection.get();
        }

        throw new IllegalArgumentException("노선의 상행역을 찾을 수 없습니다.");
    }

    private List<Long> getDownStationIdList(List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }
}
