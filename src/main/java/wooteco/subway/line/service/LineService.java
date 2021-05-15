package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.domain.*;
import wooteco.subway.line.domain.rule.FindSectionHaveSameDownRule;
import wooteco.subway.line.domain.rule.FindSectionHaveSameUpRule;
import wooteco.subway.line.domain.rule.FindSectionRule;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    public static final String ERROR_DUPLICATED_LINE_NAME = "라인이 중복되었습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line create(Line line) {
        checkCreateValidation(line);
        return lineRepository.save(line);
    }

    public List<Station> getStations(Long lineId) {
        Line line = lineRepository.findById(lineId);
        List<Section> sectionList = line.getSections().toList();
        List<Station> stations = sectionList.stream()
                .map(section -> stationRepository.findById(section.getUpStationId()))
                .collect(Collectors.toList());

        Long lastStationId = sectionList.get(sectionList.size() -1).getDownStationId();
        stations.add(stationRepository.findById(lastStationId));

        return stations;
    }

    public Lines allLines() {
        return lineRepository.findAll();
    }

    public Line findById(final Long id) {
        return lineRepository.findById(id);
    }

    public void update(final Line line) {
        lineRepository.update(line);
    }

    public void deleteById(final Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(final Long id, final Section section) {
        Line line = findById(id);
        Sections sections = line.getSections();
        sections.validateEnableAddSection(section);
        boolean isEndPoint = sections.checkEndPoint(section);
        if (isEndPoint) {
            lineRepository.addSection(id, section);
            return;
        }

        addSectionBetween(id, sections, section);
    }

    private void addSectionBetween(final Long id, final Sections sections, final Section section) {
        List<FindSectionRule> findSectionRules = Arrays.asList(new FindSectionHaveSameUpRule(),
                new FindSectionHaveSameDownRule());
        Section deleteSection = sections.findDeleteByAdding(section, findSectionRules);
        Section updateSection = deleteSection.updateWhenAdd(section);

        lineRepository.deleteSection(id, deleteSection);
        lineRepository.addSection(id, updateSection);
        lineRepository.addSection(id, section);
    }

    public void deleteSection(final Long id, final Long stationId) {
        Line line = findById(id);
        Sections sections = line.getSections();
        List<Section> deleteSections = sections.deleteSection(stationId);

        if (deleteSections.size() == 1) {
            lineRepository.deleteSection(id, deleteSections.get(0));
            return;
        }

        Section updateSection = sections.generateUpdateWhenDelete(deleteSections);

        deleteSections.forEach(section -> lineRepository.deleteSection(id, section));
        lineRepository.addSection(id, updateSection);
    }

    private void checkCreateValidation(Line line) {
        if (lineRepository.hasLine(line.getName())) {
            throw new IllegalArgumentException(ERROR_DUPLICATED_LINE_NAME);
        }
    }
}
