package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private static final String LINE_DUPLICATION_NAME_EXCEPTION_MESSAGE = "중복되는 이름의 지하철 노선이 존재합니다.";
    private static final String LINE_DUPLICATION_COLOR_EXCEPTION_MESSAGE = "중복되는 색깔의 지하철 노선이 존재합니다.";
    private static final String NO_SUCH_LINE_EXCEPTION_MESSAGE = "해당하는 ID의 지하철 노선이 존재하지 않습니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse createLine(@RequestBody LineRequest lineRequest) {
        if (isDuplicateName(lineRequest.getName())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_NAME_EXCEPTION_MESSAGE);
        }
        if (isDuplicateColor(lineRequest.getColor())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_COLOR_EXCEPTION_MESSAGE);
        }
        Line createdLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        Section createdSection = sectionDao.save(new Section(createdLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
        Optional<Station> upStation = stationDao.findById(lineRequest.getUpStationId());
        Optional<Station> downStation = stationDao.findById(lineRequest.getDownStationId());
        if (upStation.isEmpty() || downStation.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 지하철역입니다.");
        }
        List<StationResponse> stationResponses = List.of(new StationResponse(upStation.get().getId(), upStation.get().getName()),
            new StationResponse(downStation.get().getId(), downStation.get().getName()));
        return new LineResponse(createdLine.getId(), createdLine.getName(), createdLine.getColor(), stationResponses);
    }

    public List<LineResponse> showLines() {
        List<LineResponse> responses = new ArrayList<>();
        List<Line> lines = lineDao.findAll();
        List<Station> stations = stationDao.findAll();

        for (Line line : lines) {
            Sections sections = sectionDao.findByLineId(line.getId());
            List<Long> stationsIds = sections.getStationIds();
            List<StationResponse> stationResponses = stations.stream().filter(station -> stationsIds.contains(station.getId()))
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
            responses.add(new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses));
        }
        return responses;
    }

    public LineResponse showLine(Long id) {
        Line line = findLineById(id);
        List<Station> stations = stationDao.findAll();
        Sections sections = sectionDao.findByLineId(line.getId());
        List<Long> stationsIds = sections.getStationIds();
        List<StationResponse> stationResponses = stations.stream().filter(station -> stationsIds.contains(station.getId()))
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    private Line findLineById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_LINE_EXCEPTION_MESSAGE));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line originLine = findLineById(id);
        if (isDuplicateName(lineRequest.getName()) && isNotSameName(originLine.getName(), lineRequest.getName())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_NAME_EXCEPTION_MESSAGE);
        }
        if (isDuplicateColor(lineRequest.getColor()) && isNotSameColor(originLine.getColor(), lineRequest.getColor())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_COLOR_EXCEPTION_MESSAGE);
        }
        lineDao.update(originLine, new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLine(Long id) {
        Line line = findLineById(id);
        lineDao.delete(line);
    }

    private boolean isDuplicateName(String name) {
        return lineDao.findByName(name).isPresent();
    }

    private boolean isDuplicateColor(String color) {
        return lineDao.findByColor(color).isPresent();
    }

    private boolean isNotSameName(String originName, String updateName) {
        return !originName.equals(updateName);
    }

    private boolean isNotSameColor(String originColor, String updateColor) {
        return !originColor.equals(updateColor);
    }

    public void addSection(long id, SectionRequest sectionRequest) {
        List<Section> originSections = sectionDao.findByLineId(id).getSections();
        Sections addedSections = new Sections(originSections);
        Section addSection = new Section(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        addedSections.add(addSection);
        List<Section> fullSection = new ArrayList<>((addedSections.getSections()));
        List<Section> deleteSection = new ArrayList<>();
        for (Section section : originSections) {
            if (fullSection.contains(section)) {
                fullSection.remove(section);
                continue;
            }
            deleteSection.add(section);
            fullSection.remove(section);
        }
        List<Section> newSection = fullSection.stream()
            .map(section -> new Section(id, section.getUpStationId(), section.getDownStationId(), section.getDistance())
            ).collect(Collectors.toList());
        if (!deleteSection.isEmpty()) {
            deleteSection.forEach(section -> sectionDao.delete(section.getId()));
        }
        if (!newSection.isEmpty()) {
            newSection.forEach(sectionDao::save);
        }
    }

    public void deleteSection(long id, Long stationId) {
        List<Section> originSections = sectionDao.findByLineId(id).getSections();
        Sections removedSection = new Sections(originSections);
        removedSection.remove(stationId);
        List<Section> fullSection = new ArrayList<>((removedSection.getSections()));
        List<Section> deleteSection = new ArrayList<>();
        for (Section section : originSections) {
            if (fullSection.contains(section)) {
                fullSection.remove(section);
                continue;
            }
            deleteSection.add(section);
            fullSection.remove(section);
        }
        List<Section> newSection = fullSection.stream()
            .map(section -> new Section(id, section.getUpStationId(), section.getDownStationId(), section.getDistance())
            ).collect(Collectors.toList());
        if (!deleteSection.isEmpty()) {
            deleteSection.forEach(section -> sectionDao.delete(section.getId()));
        }
        if (!newSection.isEmpty()) {
            newSection.forEach(sectionDao::save);
        }
    }
}
