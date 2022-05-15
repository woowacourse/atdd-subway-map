package wooteco.subway.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.RegisteredSectionDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.Lines;
import wooteco.subway.domain.section.RegisteredSection;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.section.SectionsFactory;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.request.UpdateLineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.RegisteredSectionEntity;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.exception.ExceptionType;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService {

    private static final String DUPLICATE_LINE_NAME_EXCEPTION_MESSAGE = "중복되는 이름의 지하철 노선이 존재합니다.";

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final RegisteredSectionDao registeredSectionDao;

    public LineService(LineDao lineDao,
                       SectionDao sectionDao,
                       StationDao stationDao,
                       RegisteredSectionDao registeredSectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.registeredSectionDao = registeredSectionDao;
    }

    public List<LineResponse> findAll() {
        return Lines.of(findAllRegisteredSections())
                .toSortedList()
                .stream()
                .map(LineResponse::of)
                .sorted(Comparator.comparingLong(LineResponse::getId))
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponse find(Long id) {
        Line line = findExistingLine(id);
        Sections sections = SectionsFactory.generate(findAllStationsRegisteredToLine(id));
        List<Station> stations = sections.toSortedStations();
        return LineResponse.of(line, stations);
    }

    @Transactional
    public LineResponse save(CreateLineRequest lineRequest) {
        validateUniqueLineName(lineRequest.getName());
        Station upStation = findExistingStation(lineRequest.getUpStationId());
        Station downStation = findExistingStation(lineRequest.getDownStationId());
        Section newSection = new Section(upStation, downStation, lineRequest.getDistance());

        LineEntity lineEntity = lineDao.save(new LineEntity(lineRequest.getName(), lineRequest.getColor()));
        sectionDao.save(SectionEntity.of(lineEntity.getId(), newSection));
        return LineResponse.of(lineEntity.toDomain(), newSection.toStations());
    }

    @Transactional
    public void update(Long id, UpdateLineRequest lineRequest) {
        validateExistingLine(id);
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        validateUniqueLineName(name);

        LineEntity updatedLine = new LineEntity(id, name, color);
        lineDao.update(updatedLine);
    }

    @Transactional
    public void delete(Long id) {
        validateExistingLine(id);
        lineDao.deleteById(id);
        sectionDao.deleteAllByLineId(id);
    }

    private List<RegisteredSection> findAllRegisteredSections() {
        return registeredSectionDao.findAll()
                .stream()
                .map(RegisteredSectionEntity::toDomain)
                .collect(Collectors.toList());
    }

    private Line findExistingLine(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionType.LINE_NOT_FOUND))
                .toDomain();
    }

    private Station findExistingStation(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.STATION_NOT_FOUND))
                .toDomain();
    }

    private List<Section> findAllStationsRegisteredToLine(Long id) {
        return sectionDao.findAllByLineId(id)
                .stream()
                .map(SectionEntity::toDomain)
                .collect(Collectors.toList());
    }

    private void validateExistingLine(Long id) {
        boolean isExistingLine = lineDao.findById(id).isPresent();
        if (!isExistingLine) {
            throw new NotFoundException(ExceptionType.LINE_NOT_FOUND);
        }
    }

    private void validateUniqueLineName(String name) {
        boolean isDuplicateName = lineDao.findByName(name).isPresent();
        if (isDuplicateName) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME_EXCEPTION_MESSAGE);
        }
    }
}
