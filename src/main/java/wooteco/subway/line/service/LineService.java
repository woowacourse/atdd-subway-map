package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.*;
import wooteco.subway.line.domain.rule.*;
import wooteco.subway.line.ui.dto.LineCreateRequest;
import wooteco.subway.line.ui.dto.LineModifyRequest;
import wooteco.subway.line.ui.dto.LineResponse;
import wooteco.subway.line.ui.dto.SectionAddRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

    @Transactional
    public LineResponse create(final LineCreateRequest lineCreateRequest) {
        Section section = new Section(lineCreateRequest.getUpStationId(),
                lineCreateRequest.getDownStationId(),
                lineCreateRequest.getDistance());
        Sections sections = new Sections(Collections.singletonList(section));

        Line line = new Line(lineCreateRequest.getName(), lineCreateRequest.getColor(), sections);
        checkCreateValidation(line);
        Line savedLine = lineRepository.save(line);

        List<Long> ids = Arrays.asList(section.getUpStationId(), section.getDownStationId());

        List<Station> stations = stationRepository.findByIds(ids);
        return new LineResponse(savedLine, stations);
    }


    public List<Station> getStations(final Long lineId) {
        Line line = lineRepository.findById(lineId);
        List<Section> sectionList = line.getSections().toList();
        List<Long> ids = sectionList.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());

        Long lastStationId = sectionList.get(sectionList.size() -1).getDownStationId();
        ids.add(lastStationId);

        List<Station> stations = stationRepository.findByIds(ids);
        sortStation(stations, ids);

        return stations;
    }

    private void sortStation(List<Station> stations, List<Long> key) {
        stations.sort(Comparator.comparing(station -> {
            int index = key.indexOf(station.getId());
            return index >= 0 ? index : Integer.MAX_VALUE;
        }));
    }

    public Lines allLines() {
        return lineRepository.findAll();
    }

    public LineResponse findById(final Long id) {
        Line savedLine = lineRepository.findById(id);
        List<Station> stations = getStations(savedLine.getId());


        return new LineResponse(savedLine, stations);
    }

    public void update(final Long id, final LineModifyRequest lineModifyRequest) {
        Line line = new Line(id, lineModifyRequest.getName(), lineModifyRequest.getName());
        lineRepository.update(line);
    }

    public void deleteById(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(final Long id, final Section section) {
        Line savedLine = lineRepository.findById(id);
        savedLine.validateEnableAddSection(section);
        boolean isEndPoint = savedLine.isEndPoint(section);
        if (isEndPoint) {
            lineRepository.addSection(id, section);
            return;
        }

        addSectionBetween(id, savedLine, section);
    }

    @Transactional
    public void addSection(final Long id, final SectionAddRequest sectionAddRequest) {
        Line savedLine = lineRepository.findById(id);
        Section section = new Section(sectionAddRequest.getUpStationId(),
                sectionAddRequest.getDownStationId(), sectionAddRequest.getDistance());

        savedLine.validateEnableAddSection(section);

        boolean isEndPoint = savedLine.isEndPoint(section);
        if (isEndPoint) {
            lineRepository.addSection(id, section);
            return;
        }

        addSectionBetween(id, savedLine, section);
    }

    private void addSectionBetween(final Long id, final Line line, final Section section) {
        List<FindSectionRule> findSectionRules = Arrays.asList(new FindSectionHaveSameUpRule(),
                new FindSectionHaveSameDownRule());
        Section deleteSection = line.findDeleteByAdding(section, findSectionRules);
        Section updateSection = deleteSection.updateWhenAdd(section);

        lineRepository.deleteSection(id, deleteSection);
        lineRepository.addSection(id, updateSection);
        lineRepository.addSection(id, section);
    }

    @Transactional
    public void deleteSection(final Long id, final Long stationId) {
        Line savedLine = lineRepository.findById(id);
        List<Section> deleteSections = savedLine.deleteSection(stationId);

        if (deleteSections.size() == 1) {
            lineRepository.deleteSection(id, deleteSections.get(0));
            return;
        }

        Section updateSection = savedLine.generateUpdateWhenDelete(deleteSections);
        deleteSections.forEach(section -> lineRepository.deleteSection(id, section));
        lineRepository.addSection(id, updateSection);
    }

    private void checkCreateValidation(final Line line) {
        if (lineRepository.hasLine(line.getName())) {
            throw new IllegalArgumentException(ERROR_DUPLICATED_LINE_NAME);
        }
    }
}
