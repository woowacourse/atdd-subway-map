package wooteco.subway.line.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.*;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineUpdateRequest;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.line.entity.SectionEntity;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;
import wooteco.subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
        validateDuplication(lineRequest);
        Line line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        Section section = new Section(line, findStationById(lineRequest.getUpStationId()), findStationById(lineRequest.getDownStationId()), lineRequest.getDistance());

        sectionDao.save(new SectionEntity(section.line().id(), section.upStation().getId(), section.downStation().getId(), section.distance()));
        return new LineResponse(line.id(), line.nameAsString(), line.color(), toStationsResponses(Collections.singletonList(section)));
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long lineId) {
        Line line = findLineById(lineId);
        Sections sections = new Sections(toSections(line));

        List<Section> sortedSections = sections.sortedSections();
        return new LineResponse(line.id(), line.nameAsString(), line.color(), toStationsResponses(sortedSections));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.id(), line.nameAsString(), line.color(), toStationsResponses(new Sections(toSections(line)).sortedSections())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(final Long id, final LineUpdateRequest lineUpdateRequest) {
        Line line = findLineById(id);
        line.changeName(lineUpdateRequest.getName());
        line.changeColor(lineUpdateRequest.getColor());
        lineDao.update(line);
    }

    @Transactional
    public void delete(final Long id) {
        Line line = findLineById(id);
        lineDao.delete(line.id());
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        Sections originSections = new Sections(toSections(line));
        Section targetSection = new Section(findStationById(sectionRequest.getUpStationId()), findStationById(sectionRequest.getDownStationId()), sectionRequest.getDistance());

        line.addSection(targetSection);

        dirtyChecking(originSections, line.sections());
    }

    @Transactional
    public void deleteSectionByStationId(final Long lineId, final Long stationId) {
        Line line = findLineById(lineId);
        Sections originSections = new Sections(toSections(line));
        Station targetStation = findStationById(stationId);

        line.deleteStation(targetStation);

        deleteDirtyChecking(originSections, line.sections());
    }

    private void deleteDirtyChecking(final Sections originSections, final Sections sections) {
        List<Section> changedSections = sections.changedSections(originSections);
        for (Section section : changedSections) {
            SectionEntity changedSectionEntity = new SectionEntity(section.id(), section.line().id(), section.upStation().getId(), section.downStation().getId(), section.distance());
            sectionDao.delete(changedSectionEntity.getId());

            Optional<Section> findStation = sections.findByUpwardStation(section.upStation());
            if (findStation.isPresent()) {
                Section saveSection = findStation.get();
                sectionDao.save(new SectionEntity(saveSection.line().id(), saveSection.upStation().getId(), saveSection.downStation().getId(), saveSection.distance()));
            }
        }
    }

    private void dirtyChecking(final Sections originSections, final Sections sections) {
        List<Section> changedSections = originSections.changedSections(sections);
        for (Section section : changedSections) {
            SectionEntity changedSectionEntity = new SectionEntity(section.id(), section.line().id(), section.upStation().getId(), section.downStation().getId(), section.distance());
            if (sectionDao.findByLineIdWithUpStationId(changedSectionEntity.getLineId(), changedSectionEntity.getUpStationId()).isPresent()) {
                sectionDao.deleteByLineIdWithUpStationId(changedSectionEntity.getLineId(), changedSectionEntity.getUpStationId());
            }

            if (sectionDao.findByLineIdWithDownStationId(changedSectionEntity.getLineId(), changedSectionEntity.getDownStationId()).isPresent()) {
                sectionDao.deleteByLineIdWithDownStationId(changedSectionEntity.getLineId(), changedSectionEntity.getDownStationId());
            }
            sectionDao.save(changedSectionEntity);
        }
    }

    private List<Section> toSections(final Line line) {
        List<SectionEntity> sectionEntities = sectionDao.findByLineId(line.id());
        return sectionEntities.stream()
                .map(sectionEntity -> new Section(sectionEntity.getId(), line, findStationById(sectionEntity.getUpStationId()), findStationById(sectionEntity.getDownStationId()), sectionEntity.getDistance()))
                .collect(Collectors.toList());
    }

    private List<StationResponse> toStationsResponses(final List<Section> sections) {
        return sections.stream()
                .flatMap(section -> Stream.of(
                        section.upStation(), section.downStation()
                ))
                .distinct()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private void validateDuplication(final LineRequest lineRequest) {
        if (lineDao.existByName(lineRequest.getName())) {
            throw new IllegalStateException("이미 등록된 이름임!");
        }

        if (lineDao.existByColor(lineRequest.getColor())) {
            throw new IllegalStateException("이미 있는 색깔임!");
        }
    }

    private Line findLineById(final Long id) {
        return lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("없는 노선임!"));
    }

    private Station findStationById(final Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new IllegalStateException("없는 역임!"));
    }
}
