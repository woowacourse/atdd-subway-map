package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.Stations;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.LineWithStationsResponse;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.StationEntity;

@Service
public class LineService {

    private static final String DUPLICATE_NAME_ERROR = "이미 같은 이름의 노선이 존재합니다.";
    private static final String NOT_EXIST_ERROR = "해당 노선이 존재하지 않습니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineWithStationsResponse createLine(LineRequest lineRequest) {
        LineEntity lineEntity = lineRequest.toLineEntity();
        checkNameDuplication(lineRequest);
        LineEntity savedLineEntity = lineDao.save(lineEntity);
        Stations stations = createSection(savedLineEntity.getId(), lineRequest);

        Line savedLine = new Line(savedLineEntity.getId(), savedLineEntity.getName(), savedLineEntity.getColor(),
                stations);
        return LineWithStationsResponse.of(savedLine);
    }

    private void checkNameDuplication(LineRequest lineRequest) {
        Optional<LineEntity> wrappedLineEntity = lineDao.findByName(lineRequest.getName());
        if (wrappedLineEntity.isPresent()) {
            throw new DuplicateKeyException(DUPLICATE_NAME_ERROR);
        }
    }

    private Stations createSection(Long lineId, LineRequest lineRequest) {
        StationEntity upStationEntity = getStation(lineRequest.getUpStationId());
        StationEntity downStationEntity = getStation(lineRequest.getDownStationId());

        SectionEntity sectionEntity = new SectionEntity.Builder(lineId, lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance())
                .build();
        sectionDao.save(sectionEntity);

        Station upStation = new Station(upStationEntity.getId(), upStationEntity.getName());
        Station downStation = new Station(downStationEntity.getId(), downStationEntity.getName());

        return new Stations(List.of(upStation, downStation));
    }

    private StationEntity getStation(Long id) {
        Optional<StationEntity> stationEntity = stationDao.findById(id);
        if (stationEntity.isEmpty()) {
            throw new NoSuchElementException(NOT_EXIST_ERROR);
        }
        return stationEntity.get();
    }

    public List<LineWithStationsResponse> findAllLines() {
        List<Line> lines = getAllLines();
        return lines.stream()
                .map(LineWithStationsResponse::of)
                .collect(Collectors.toList());
    }

    private List<Line> getAllLines() {
        List<LineEntity> lineEntities = lineDao.findAll();
        List<Line> lines = new ArrayList<>();

        for (LineEntity lineEntity : lineEntities) {
            Line line = getLine(lineEntity);
            lines.add(line);
        }
        return lines;
    }

    private Line getLine(LineEntity lineEntity) {
        Sections sections = getSections(sectionDao.findAllByLineId(lineEntity.getId()));
        List<Station> orderedStations = sections.getOrderedStations();

        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), new Stations(orderedStations));
    }

    private Sections getSections(List<SectionEntity> sectionEntities) {
        List<Section> sections = new ArrayList<>();
        for (SectionEntity sectionEntity : sectionEntities) {
            StationEntity upStationEntity = getStation(sectionEntity.getUpStationId());
            StationEntity downStationEntity = getStation(sectionEntity.getDownStationId());

            Station upStation = new Station(upStationEntity.getId(), upStationEntity.getName());
            Station downStation = new Station(downStationEntity.getId(), downStationEntity.getName());

            Section section = new Section(sectionEntity.getId(), sectionEntity.getLineId(), upStation, downStation,
                    sectionEntity.getDistance());
            sections.add(section);
        }
        return new Sections(sections);
    }

    public LineResponse findLineById(Long id) {
        Optional<LineEntity> wrappedLineEntity = lineDao.findById(id);
        checkLineExist(wrappedLineEntity);
        LineEntity lineEntity = wrappedLineEntity.get();
        Line line = getLine(lineEntity);
        return LineResponse.of(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        checkLineExist(lineDao.findById(id));
        LineEntity lineEntity = lineRequest.toLineEntity();
        lineDao.update(id, lineEntity);
    }

    public void deleteLine(Long id) {
        checkLineExist(lineDao.findById(id));
        lineDao.deleteById(id);
    }

    private void checkLineExist(Optional<LineEntity> wrappedLine) {
        if (wrappedLine.isEmpty()) {
            throw new NoSuchElementException(NOT_EXIST_ERROR);
        }
    }
}
