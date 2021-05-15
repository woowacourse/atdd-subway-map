package wooteco.subway.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.HashMap;
import java.util.Map;

public class MapSqlParameterSourceBuilder {
    private final Map<String, Object> param;

    public MapSqlParameterSourceBuilder() {
        this.param = new HashMap<>();
    }

    public MapSqlParameterSourceBuilder setParam(String key, Object value) {
        this.param.put(key, value);
        return this;
    }

    public SqlParameterSource build() {
        return new MapSqlParameterSource(param);
    }
}
