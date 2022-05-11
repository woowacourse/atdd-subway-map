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
import wooteco.subway.dto.StationResponse;

@Service
@Transactional
public class LineService {

    private static final int midPointCount = 2;
    private static final int MINIMUM_SECTIONS_SIZE = 1;

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
        sections.findUpSection(sectionRequest.getUpStationId())
                .ifPresent(section -> sectionDao.deleteById(section.getId()));
        sections.findDownSection(sectionRequest.getDownStationId())
                .ifPresent(section -> sectionDao.updateDistanceById(section.getId(),
                        section.getDistance() - sectionRequest.getDistance()));
    }

    private void validSection(Sections sections, SectionRequest sectionRequest) {
        sections.validSameStations(sectionRequest);
        sections.validNonLinkSection(sectionRequest);
        sections.validExistingSectionDistance(sectionRequest);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLineAll() {
        return lineDao.findAll().stream()
                .map(this::findLineResponseByLine)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
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

    public void deleteSectionByLineIdAndStationId(Long lineId, Long stationId) {
        validSectionSize(lineId);
        linkSection(lineId, stationId);
        sectionDao.deleteByLineIdAndStationId(lineId, stationId);
    }

    private void validSectionSize(Long lineId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        if (sections.size() <= MINIMUM_SECTIONS_SIZE) {
            throw new IllegalArgumentException("노선에 구간이 1개 이상은 존재해야합니다.");
        }
    }

    private void linkSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        if (sections.requiredLink(stationId)) {
            sections.findUpSection(stationId).ifPresent(section -> sectionDao.deleteById(section.getId()));
            sections.findDownSection(stationId).ifPresent(section -> sectionDao.deleteById(section.getId()));
            sectionDao.save(sections.findLinkSection(lineId, stationId));
        }
    }
}
