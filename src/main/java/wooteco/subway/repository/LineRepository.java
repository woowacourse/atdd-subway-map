package wooteco.subway.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.SubwayException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class LineRepository {
    private static final int ONE_SECTION = 1;
    private final JdbcTemplate jdbcTemplate;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineRepository(JdbcTemplate jdbcTemplate, LineDao lineDao, SectionDao sectionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line saveLineWithSection(String name, String color, Long upStationId, Long downStationId, int distance) {
        Long createdLineId = lineDao.create(name, color);
        Long createdSectionId = sectionDao.create(createdLineId, upStationId, downStationId, distance);
        List<Section> sections = new ArrayList<>();
        Section section = new Section(createdSectionId, createdLineId, upStationId, downStationId, distance);
        sections.add(section);
        return new Line(createdLineId, name, color, sections);
    }

    public List<Line> findAllLine() {
        return lineDao.findAll();
    }

    public Line findLineWithSectionsById(Long lineId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        Optional<Line> line = lineDao.findById(lineId);
        return new Line(line.get(), sections);
    }
    public Optional<Line> findLineByName(String name) {
        return lineDao.findByName(name);
    }


    public int edit(Long lineId, String name, String color) {
        return lineDao.edit(lineId, name, color);
    }

    public Long deleteLineWithSectionByLineId(Long lineId) {
        String selectSectionQuery = "SELECT * FROM section WHERE line_id = ?";
        List<Long> sectionIds = jdbcTemplate.query(
                selectSectionQuery,
                (resultSet, rowNum) -> resultSet.getLong("id"),
                lineId
        );

        for (Long sectionId : sectionIds) {
            String deleteSectionQuery = "DELETE FROM section WHERE id = ?";
            jdbcTemplate.update(deleteSectionQuery, sectionId);
        }

        String deleteLineQuery = "DELETE FROM LINE WHERE id = ?";
        return (long) jdbcTemplate.update(deleteLineQuery, lineId);
    }

    public void createSectionInLine(Long lineId, Long upStationId, Long downStationId, int distance) {
        boolean containsUpStation = containsStationInLine(lineId, upStationId);
        boolean containsDownStation = containsStationInLine(lineId, downStationId);

        // 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
        if (!containsUpStation && !containsDownStation) {
            throw new SubwayException("상행역과 하행역 둘 중 하나도 라인에 포함되어 있지 않으면 구간을 추가할 수 없습니다.");
        }

        // 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
        // -> A-B, B-C 구간이 등록된 상황에서 B-C 구간을 등록할 수 없음(A-C 구간도 등록할 수 없음)
        if (containsUpStation && containsDownStation) {
            throw new SubwayException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
        }

        // 상행 종점(구간) 등록
        if (containsDownStation && isStartStation(lineId, downStationId)) {
            sectionDao.create(lineId, upStationId, downStationId, distance);
            return;
        }


        // 하행 종점(구간) 등록
        if (containsUpStation && isEndStation(lineId, upStationId)) {
            sectionDao.create(lineId, upStationId, downStationId, distance);
            return;
        }

        // 중간 구간 등록
        if (containsUpStation) {
            Section originSection = sectionDao.findByUpStationIdAndLineId(upStationId, lineId).get();
            validateSectionDistance(distance, originSection);
            sectionDao.deleteById(originSection.getId());
            sectionDao.create(lineId, originSection.getUpStationId(), downStationId, distance);
            sectionDao.create(lineId,
                    downStationId,
                    originSection.getDownStationId(),
                    originSection.getDistance() - distance);
            return;
        }

        if (containsDownStation) {
            Section originSection = sectionDao.findByDownStationIdAndLineId(downStationId, lineId).get();
            validateSectionDistance(distance, originSection);
            sectionDao.deleteById(originSection.getId());
            sectionDao.create(lineId,
                    originSection.getUpStationId(),
                    upStationId,
                    originSection.getDistance() - distance);
            sectionDao.create(lineId, upStationId, originSection.getDownStationId(), distance);
            return;
        }

        throw new SubwayException("잘못된 요청입니다.");
    }

    private void validateSectionDistance(int newSectionDistance, Section originSection) {
        if (originSection.getDistance() <= newSectionDistance) {
            throw new SubwayException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
    }

    private boolean containsStationInLine(Long lineId, Long stationId) {
        Optional<Section> foundSectionByDownStationId
                = sectionDao.findByDownStationIdAndLineId(stationId, lineId);
        Optional<Section> foundSectionByUpStationId
                = sectionDao.findByUpStationIdAndLineId(stationId, lineId);
        return foundSectionByUpStationId.isPresent() || foundSectionByDownStationId.isPresent();
    }


    public void deleteSectionInLine(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        if (sections.size() == ONE_SECTION) {
            throw new SubwayException("구간이 하나인 노선에서는 역을 삭제할 수 없습니다.");
        }

        Optional<Section> upSectionOptional = sectionDao.findByDownStationIdAndLineId(stationId, lineId);
        Optional<Section> downSectionOptional = sectionDao.findByUpStationIdAndLineId(stationId, lineId);

        if (isEndStation(lineId, stationId)) {
            sectionDao.deleteById(upSectionOptional.get().getId());
            return;
        }
        if (isStartStation(lineId, stationId)) {
            sectionDao.deleteById(downSectionOptional.get().getId());
            return;
        }

        Section upSection = upSectionOptional.get();
        Section downSection = downSectionOptional.get();
        sectionDao.deleteById(upSection.getId());
        sectionDao.deleteById(downSection.getId());
        sectionDao.create(
                lineId,
                upSection.getUpStationId(),
                downSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance()
        );
    }

    private boolean isEndStation(Long lineId, Long stationId) {
        Optional<Section> downSectionOptional = sectionDao.findByUpStationIdAndLineId(stationId, lineId);
        return !downSectionOptional.isPresent();
    }

    private boolean isStartStation(Long lineId, Long stationId) {
        Optional<Section> upSectionOptional = sectionDao.findByDownStationIdAndLineId(stationId, lineId);
        return !upSectionOptional.isPresent();
    }
}
