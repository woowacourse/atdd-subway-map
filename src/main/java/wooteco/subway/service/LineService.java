package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequestDto;
import wooteco.subway.dto.request.SectionRequestDto;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.repository.dao.JdbcSectionDao;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.entity.LineEntity;
import wooteco.subway.repository.entity.SectionEntity;

@Service
public class LineService {

    private final LineDao lineDao;
    // 인터페이스 SectionDao 로 바꿔야 함
    private final JdbcSectionDao sectionDao;

    private final StationService stationService;

    public LineService(final LineDao lineDao, final JdbcSectionDao sectionDao, final StationService stationService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public Line registerLine(final LineRequestDto lineRequestDto) {
        final Line line = new Line(lineRequestDto.getName(), lineRequestDto.getColor());
        try {
            final LineEntity savedLineEntity = lineDao.save(new LineEntity(line));
            SectionRequestDto sectionRequestDto = new SectionRequestDto(lineRequestDto);
            final Station upStation = stationService.searchById(sectionRequestDto.getUpStationId());
            final Station downStation = stationService.searchById(sectionRequestDto.getDownStationId());
            final Section section = new Section(upStation, downStation, sectionRequestDto.getDistance());
            sectionDao.save(new SectionEntity(savedLineEntity.getId(), section));
            return savedLineEntity.generateLine();
        } catch (DuplicateKeyException exception) {
            throw new DuplicateLineNameException();
        }
    }

    public Line searchLineById(final Long id) {
        return lineDao.findById(id).generateLine();
    }

    public List<Line> searchAllLines() {
        return lineDao.findAll()
                .stream()
                .map(lineEntity -> new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor()))
                .collect(Collectors.toList());
    }

    public void modifyLine(final Long id, final LineRequestDto lineRequestDto) {
        lineDao.update(new LineEntity(id, lineRequestDto.getName(), lineRequestDto.getColor()));
    }

    public void removeLine(final Long id) {
        lineDao.deleteById(id);
    }

    public void registerSection(final Long lineId, final SectionRequestDto sectionRequestDto) {
        final Line line = searchLineById(lineId);
        final Station upStation = stationService.searchById(sectionRequestDto.getUpStationId());
        final Station downStation = stationService.searchById(sectionRequestDto.getDownStationId());
        final Section section = new Section(upStation, downStation, sectionRequestDto.getDistance());
        final Sections sections = new Sections(searchSectionsByLineId(line.getId()));
        if (sections.isAddableOnLine(section)) {
            final Section overlapSection = sections.findOverlapSection(section);
            registerSectionWhenOnLine(line.getId(), section, overlapSection);
            return;
        }
        sectionDao.save(new SectionEntity(lineId, section));
    }

    private void registerSectionWhenOnLine(final Long lineId,
                                           final Section section,
                                           final Section overlapSection) {
        if (overlapSection.isUpStationMatch(section.getUpStation())) {
            final SectionEntity sectionEntity = new SectionEntity(
                    overlapSection.getId(),
                    lineId,
                    section.getDownStation().getId(),
                    overlapSection.getDownStation().getId(),
                    overlapSection.getDistance() - section.getDistance());
            sectionDao.update(sectionEntity);
            sectionDao.save(new SectionEntity(lineId, section));
            return;
        }
        final SectionEntity sectionEntity = new SectionEntity(
                overlapSection.getId(),
                lineId,
                overlapSection.getUpStation().getId(),
                section.getUpStation().getId(),
                overlapSection.getDistance() - section.getDistance());
        sectionDao.update(sectionEntity);
        sectionDao.save(new SectionEntity(lineId, section));
        return;
    }

    public List<Section> searchSectionsByLineId(final Long lineId) {
        return sectionDao.findByLineId(lineId).stream()
                .map(sectionEntity -> new Section(
                        sectionEntity.getId(),
                        stationService.searchById(sectionEntity.getUpStationId()),
                        stationService.searchById(sectionEntity.getDownStationId()),
                        sectionEntity.getDistance()
                )).collect(Collectors.toList());
    }
}
