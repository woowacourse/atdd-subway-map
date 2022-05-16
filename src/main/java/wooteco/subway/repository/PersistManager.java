package wooteco.subway.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.UpdateDao;
import wooteco.subway.entity.Entity;

@Repository
public class PersistManager<T extends Entity> {

    public Long persist(UpdateDao<T> dao, T entity, List<Long> persistedIds) {
        if (entity.getId() == null) {
            return dao.save(entity);
        }
        if (persistedIds.contains(entity.getId())) {
            return dao.update(entity);
        }
        return dao.delete(entity.getId());
    }

    public void deletePersistedAll(UpdateDao<T> dao, List<Long> persistedIds) {
        for (Long persistedId : persistedIds) {
            dao.delete(persistedId);
        }
    }
}
