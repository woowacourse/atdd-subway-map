package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.*;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = lineDao.save(lineRequest);
        sectionDao.saveInitialSection(lineRequest, line.getId());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), makeSectionsToStations(line.getId()).sortStations());
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();

        return lines.stream()
                .map((line) -> new LineResponse(line.getId(), line.getName(), line.getColor(),makeSectionsToStations(line.getId()).sortStations()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다."));
        return new LineResponse(line.getId(), line.getName(), line.getColor(), makeSectionsToStations(id).sortStations());
    }

    private Sections makeSectionsToStations(long lineId) {
        List<SectionWithStation> sections = new ArrayList<>();
        List<Section> getSections = sectionDao.findByLine(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선에 구간이 존재하지 않습니다."));

        for (Section section : getSections) {
            Station upStation = stationDao.findById(section.getUpStationId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));
            Station downStation = stationDao.findById(section.getDownStationId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));
            sections.add(SectionWithStation.of(section, upStation, downStation));
        }

        return new Sections(sections);
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
    }

    public void addSection(SectionRequest sectionRequest, long lineId) {
        Section newSection = new Section(sectionRequest, lineId);
        if(isStartOrEndOfSection(newSection, lineId)){
            sectionDao.save(newSection);
            return;
        }
        insertToBetweenSections(newSection);
    }

    private void insertToBetweenSections(Section newSection) {
        Section findSection = sectionDao.findBySameUpOrDownStation(newSection)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 구간입니다."));
        validateDistance(findSection, newSection);
        changeExistSection(newSection, findSection);
        sectionDao.save(newSection);
    }

    private void validateDistance(Section findSection, Section newSection) {
        if(findSection.getDistance() < newSection.getDistance()){
            throw new IllegalArgumentException("추가하려는 구간의 길이는 기존 구간길이보다 길 수 없습니다.");
        }
    }

    private void changeExistSection(Section newSection, Section findSection) {
        sectionDao.updateDistance(findSection, newSection);
        if(newSection.hasSameUpstation(findSection)){
            sectionDao.updateUpStation(findSection, newSection);
            return;
        }
        sectionDao.updateDownStation(findSection, newSection);
    }

    private boolean isStartOrEndOfSection(Section section, long id) {
        boolean isFirst = sectionDao.findByUpStationId(section.getDownStationId(), id).isPresent();
        boolean isLast = sectionDao.findByDownStationId(section.getUpStationId(), id).isPresent();
        if(isFirst || isLast){
           if(isBetweenStation(section, id)){
               return false;
           }
            return true;
        }
        return false;
    }

    private boolean isBetweenStation(Section section, long id) {
        return sectionDao.findByUpStationId(section.getUpStationId(), id).isPresent() || sectionDao.findByDownStationId(section.getDownStationId(), id).isPresent();
    }

    public void deleteSection(long stationId, long lineId) {

        Station station = stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));


        Sections sections = makeSectionsToStations(lineId);
        validateSectionSize(sections);

        if(sections.isFirstStation(station)){
            deleteFirstStation(stationId, lineId);
        }

        if(sections.isLastStation(station)){
            deleteLastStation(stationId ,lineId);
        }

        deleteBetweenStation(stationId, lineId);
    }

    private void validateSectionSize(Sections sections) {
        if(sections.isLessThanOneSection()){
            throw new IllegalArgumentException("노선 내의 구간이 하나 이하라면 삭제할 수 없습니댜.");
        }
    }

    private void deleteBetweenStation(long stationId, long lineId) {
        Section previousSection = sectionDao.findByDownStationId(stationId, lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 구간이 존재하지 않습니다."));
        Section nextSection = sectionDao.findByUpStationId(stationId, lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 구간이 존재하지 않습니다."));

        sectionDao.deleteById(previousSection.getId(), lineId);
        sectionDao.deleteById(nextSection.getId(), lineId);
        int distance = previousSection.getDistance() + nextSection.getDistance();
        Section mergeSection = new Section(lineId, previousSection.getUpStationId(), nextSection.getDownStationId(), distance);
        sectionDao.save(mergeSection);
    }

    private void deleteLastStation(long stationId, long lineId) {
            Section section = sectionDao.findByDownStationId(stationId, lineId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 구간이 존재하지 않습니다."));
            sectionDao.deleteById(section.getId(), section.getLineId());
    }

    private void deleteFirstStation(long stationId, long lineId) {
            Section section = sectionDao.findByUpStationId(stationId, lineId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 구간이 존재하지 않습니다."));
            sectionDao.deleteById(section.getId(), section.getLineId());
    }
}
