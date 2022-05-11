package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateLineException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    private final StationService stationService;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse create(LineRequest lineRequest) {
        Line persistLine = createLine(lineRequest);

        Section persistSection = createSectionByLineRequest(lineRequest, persistLine);
        List<Station> stations = List.of(persistSection.getUpStation(), persistSection.getDownStation());

        return new LineResponse(persistLine, stations);
    }

    private Line createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateUnique(line);
        return lineDao.save(line);
    }

    private void validateUnique(Line line) {
        if (lineDao.existsName(line)) {
            throw new DuplicateLineException("이미 존재하는 노선 이름입니다.");
        }
        if (lineDao.existsColor(line)) {
            throw new DuplicateLineException("이미 존재하는 노선 색상입니다.");
        }
    }

    private Section createSectionByLineRequest(LineRequest lineRequest, Line line) {
        Section section = new Section(
                stationService.findById(lineRequest.getUpStationId()),
                stationService.findById(lineRequest.getDownStationId()),
                lineRequest.getDistance()
        );
        return sectionDao.save(line, section);
    }

    @Transactional
    public void createSectionBySectionRequest(Long lineId, SectionRequest sectionRequest) {
        validateExist(lineId);
        Line line = lineDao.findById(lineId);
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();
        Section newSection = new Section(upStation, downStation, distance);

        addNewSection(line, newSection);
    }

    private void addNewSection(Line line, Section newSection) {
        Sections originSections = new Sections(sectionDao.findAllByLine(line));
        Sections newSections = new Sections(originSections.getValues());

        newSections.addSection(newSection);
        saveModifications(line, originSections, newSections);
    }

    private void saveModifications(Line line, Sections originSections, Sections newSections) {
        List<Section> removedSections = originSections.getNotContainSections(newSections);
        deleteRemovedSections(line, removedSections);
        List<Section> addedSections = newSections.getNotContainSections(originSections);
        saveAddedLine(line, addedSections);
    }

    private void deleteRemovedSections(Line line, List<Section> deleteSections) {
        for (Section section : deleteSections) {
            sectionDao.deleteByLineAndSection(line, section);
        }
    }

    private void saveAddedLine(Line line, List<Section> insertSections) {
        for (Section section : insertSections) {
            sectionDao.save(line, section);
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse find(Long id) {
        validateExist(id);
        Line line = lineDao.findById(id);
        return new LineResponse(line);
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        validateExist(id);
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        validateUnique(line);
        lineDao.updateById(id, line);
    }

    private void validateExist(Long id) {
        if (!lineDao.existsId(id)) {
            throw new DataNotFoundException("존재하지 않는 노선입니다.");
        }
    }

    @Transactional
    public void delete(Long id) {
        validateExist(id);
        lineDao.deleteById(id);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Station station = stationService.findById(stationId);
        Line line = lineDao.findById(lineId);
        Sections originSections = new Sections(sectionDao.findAllByLine(line));
        Sections newSections = new Sections(originSections.getValues());

        newSections.removeSectionByStation(station);
        saveModifications(line, originSections, newSections);
    }

}
