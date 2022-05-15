package wooteco.subway.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.RegisteredStationDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.SubwayMaps;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionsFactory;
import wooteco.subway.domain.station.RegisteredStation;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.request.UpdateLineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.RegisteredStationEntity;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService {

    private static final String DUPLICATE_LINE_NAME_EXCEPTION_MESSAGE = "중복되는 이름의 지하철 노선이 존재합니다.";
    private static final String LINE_NOT_FOUND_EXCEPTION_MESSAGE = "해당되는 노선은 존재하지 않습니다.";
    private static final String STATION_NOT_FOUND_EXCEPTION_MESSAGE = "존재하지 않는 역을 입력하였습니다.";

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final RegisteredStationDao registeredStationDao;

    public LineService(LineDao lineDao,
                       SectionDao sectionDao,
                       StationDao stationDao,
                       RegisteredStationDao registeredStationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.registeredStationDao = registeredStationDao;
    }

    public List<LineResponse> findAll() {
        List<RegisteredStation> registeredStations = registeredStationDao.findAll()
                .stream()
                .map(RegisteredStationEntity::toDomain)
                .collect(Collectors.toList());
        return SubwayMaps.of(registeredStations)
                .toList()
                .stream()
                .map(LineResponse::of)
                .sorted(Comparator.comparingLong(LineResponse::getId))
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponse find(Long id) {
        Line line = findExistingLine(id);
        List<Section> sections = sectionDao.findAllByLineId(id)
                .stream()
                .map(SectionEntity::toDomain)
                .collect(Collectors.toList());
        List<Station> stations = SectionsFactory.generate(sections).toSortedStations();
        return LineResponse.of(line, stations);
    }

    private Line findExistingLine(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND_EXCEPTION_MESSAGE))
                .toDomain();
    }

    @Transactional
    public LineResponse save(CreateLineRequest lineRequest) {
        validateUniqueLineName(lineRequest.getName());
        StationEntity upStation = findExistingStation(lineRequest.getUpStationId());
        StationEntity downStation = findExistingStation(lineRequest.getDownStationId());
        Section newSection = Section.of(upStation, downStation, lineRequest.getDistance());

        LineEntity lineEntity = lineDao.save(new LineEntity(lineRequest.getName(), lineRequest.getColor()));
        Line line = lineEntity.toDomain();
        sectionDao.save(newSection.toEntity(line.getId()));
        return LineResponse.of(line, newSection.toStations());
    }

    private StationEntity findExistingStation(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(STATION_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Transactional
    public void update(Long id, UpdateLineRequest lineRequest) {
        validateExistingLine(id);
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        validateUniqueLineName(name);

        Line updatedLine = new Line(id, name, color);
        lineDao.update(updatedLine.toEntity());
    }

    @Transactional
    public void delete(Long id) {
        validateExistingLine(id);
        lineDao.deleteById(id);
        sectionDao.deleteAllByLineId(id);
    }

    private void validateExistingLine(Long id) {
        boolean isExistingLine = lineDao.findById(id).isPresent();
        if (!isExistingLine) {
            throw new NotFoundException(LINE_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    private void validateUniqueLineName(String name) {
        boolean isDuplicateName = lineDao.findByName(name).isPresent();
        if (isDuplicateName) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME_EXCEPTION_MESSAGE);
        }
    }
}
