package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
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

    public static final int midPointCount = 2;
    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        validDuplicatedLine(lineRequest.getName(), lineRequest.getColor());
        Long id = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        Section section = new Section(id, lineRequest);
        sectionDao.save(section);
        List<StationResponse> responses = findStationBySection(section);
        return new LineResponse(id, lineRequest.getName(), lineRequest.getColor(), responses);
    }

    private void validDuplicatedLine(String name, String color) {
        if (lineDao.existByName(name) || lineDao.existByColor(color)) {
            throw new IllegalArgumentException("중복된 Line 이 존재합니다.");
        }
    }

    private List<StationResponse> findStationBySection(Section section) {
        List<Station> stations = findBySection(section);
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private List<Station> findBySection(Section section) {
        Station upStation = stationDao.findById(section.getUpStationId());
        Station downStation = stationDao.findById(section.getDownStationId());
        return List.of(upStation, downStation);
    }

    public void saveSection(Long lineId, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        validSection(sections, sectionRequest);
        if (sections.countLinkedSection(sectionRequest) == midPointCount) {
            processBiDirectionSection(sectionRequest, sections);
        }
        sectionDao.save(new Section(lineId, sectionRequest));
    }

    private void processBiDirectionSection(SectionRequest sectionRequest, Sections sections) {
        sections.findUpSection(sectionRequest)
                .ifPresent(section -> sectionDao.deleteById(section.getId()));
        sections.findDownSection(sectionRequest).ifPresent(section -> sectionDao.updateDistanceById(section.getId(),
                section.getDistance() - sectionRequest.getDistance()));
    }

    private void validSection(Sections sections, SectionRequest sectionRequest) {
        sections.validSameStations(sectionRequest);
        sections.validNonLinkSection(sectionRequest);
        sections.validExistingSectionDistance(sectionRequest);
    }

    public List<LineResponse> findLineAll() {
        return lineDao.findAll().stream()
                .map(this::findLineResponseByLine)
                .collect(Collectors.toList());
    }

    public LineResponse findLineResponseById(Long id) {
        Line line = lineDao.findById(id);
        return findLineResponseByLine(line);
    }

    private LineResponse findLineResponseByLine(Line line) {
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()));
        List<Long> stationIdsInOrder = sections.findStationIdsInOrder();
        List<StationResponse> stationResponses = stationIdsInOrder.stream()
                .map(id -> new StationResponse(stationDao.findById(id)))
                .collect(Collectors.toUnmodifiableList());
        return new LineResponse(line, stationResponses);
    }

    public void update(Long id, LineRequest lineRequest) {
        validDuplicatedLine(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(id, lineRequest);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
