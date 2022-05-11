package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateLineException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
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
        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();
        validateStationsExist(upStationId, downStationId);
        Section section = new Section(
                stationDao.findById(lineRequest.getUpStationId()),
                stationDao.findById(lineRequest.getDownStationId()),
                lineRequest.getDistance()
        );
        return sectionDao.save(line, section);
    }

    @Transactional
    public void createSectionBySectionRequest(Long lineId, SectionRequest sectionRequest) {
        validateExist(lineId);
        Line line = lineDao.findById(lineId);
        Station upStation = stationDao.findById(sectionRequest.getUpStationId());
        Station downStation = stationDao.findById(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();
        Section newSection = new Section(upStation, downStation, distance);

        addNewSection(line, newSection);
    }

    private void addNewSection(Line line, Section newSection) {
        Sections originSections = new Sections(sectionDao.findAllByLine(line));
        Sections newSections = new Sections(originSections.getValues());

        newSections.addSection(newSection);
        List<Section> removedSections = originSections.getNotContainSections(newSections);
        removeDeletedSections(line, removedSections);
        List<Section> addedSections = newSections.getNotContainSections(originSections);
        insertAddedSections(line, addedSections);
    }

    private void removeDeletedSections(Line line, List<Section> deleteSections) {
        for (Section section : deleteSections) {
            sectionDao.deleteByLineAndSection(line, section);
        }
    }

    private void insertAddedSections(Line line, List<Section> insertSections) {
        for (Section section : insertSections) {
            sectionDao.save(line, section);
        }
    }

    private void validateStationsExist(Long upStationId, Long downStationId) {
        if (!(stationDao.existsId(upStationId) && stationDao.existsId(downStationId))) {
            throw new DataNotFoundException("노선에 있는 역이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse show(Long id) {
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

}
