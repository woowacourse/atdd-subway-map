package wooteco.subway.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.LineDto;
import wooteco.subway.service.dto.SectionDto;

@Repository
public class LineRepository {

    private static final String LINE_DUPLICATED = "이미 존재하는 노선입니다. ";

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineRepository(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Line save(final LineDto lineDto) {
        validateLineData(lineDto);
        final Long upStationId = lineDto.getUpStationId();
        final Long downStationId = lineDto.getDownStationId();
        final int distance = lineDto.getDistance();
        try {
            final Line line =  lineDao.save(lineDto);
            sectionDao.save(line.getId(), new SectionDto(upStationId, downStationId, distance));
            final Section newSection = new Section(stationDao.findById(upStationId),
                    stationDao.findById(downStationId), distance);
            line.addSection(newSection);
            return line;
        } catch (DuplicateKeyException e) {
            throw new IllegalStateException(LINE_DUPLICATED + lineDto);
        }
    }

    private void validateLineData(final LineDto lineDto) {
        checkExistedStation(lineDto.getUpStationId());
        checkExistedStation(lineDto.getDownStationId());
    }

    private void checkExistedStation(final Long stationId) {
        try {
            stationDao.findById(stationId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalStateException("잘못된 역 아이디입니다. id=" + stationId);
        }
    }

    @Transactional(readOnly = true)
    public Line findById(final Long id) {
        final List<SectionDto> sectionDtos = sectionDao.findAllByLineId(id);
        final Line line = lineDao.findById(id);
        return addSectionsToLine(line, sectionDtos);
    }


    private List<SectionDto> sortSectionDto(List<SectionDto> sectionDtos, final Long upStationId) {
        final List<SectionDto> sortedSectionDtos = new ArrayList<>();
        Long sectionId = upStationId;
        while (!sectionDtos.isEmpty()) {
            final SectionDto sectionDto = findSectionDtoById(sectionDtos, sectionId);
            sortedSectionDtos.add(sectionDto);
            sectionId = sectionDto.getDownStationId();
            sectionDtos = remove(sectionDtos, sectionDto);
        }
        return sortedSectionDtos;
    }

    private SectionDto findSectionDtoById(final List<SectionDto> sectionDtos, final Long id) {
        return sectionDtos.stream()
                .filter(sectionDto -> sectionDto.getUpStationId().equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private List<SectionDto> remove(final List<SectionDto> sectionDtos, final SectionDto target) {
        return sectionDtos.stream()
                .filter(sectionDto -> !(sectionDto.getUpStationId().equals(target.getUpStationId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Line> findAll() {
        final List<Line> lines = lineDao.findAll();
        final List<Line> linesWithSections = new ArrayList<>();
        for (Line line : lines) {
            final List<SectionDto> sectionDtos = sectionDao.findAllByLineId(line.getId());
            linesWithSections.add(addSectionsToLine(line, sectionDtos));
        }
        return linesWithSections;
    }

    private Line addSectionsToLine(final Line line, final List<SectionDto> sectionDtos) {
        final List<SectionDto> sortedSectionDtos = sortSectionDto(sectionDtos, line.getUpStationId());
        for (SectionDto sectionDto : sortedSectionDtos) {
            final Station upStation = stationDao.findById(sectionDto.getUpStationId());
            final Station downStation = stationDao.findById(sectionDto.getDownStationId());
            line.addSection(new Section(upStation, downStation, sectionDto.getDistance()));
        }
        return line;
    }

}
