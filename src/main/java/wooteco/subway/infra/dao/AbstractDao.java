package wooteco.subway.infra.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.ReflectionUtils;

public abstract class AbstractDao<T, ID> {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final Class<T> clazz;
    private final String table;
    private final List<String> fields;
    private final List<Method> getters;

    // 클래스명은 XXEntity 여야만 한다
    // 테이블의 PK와 필드명은 "id" 여야만 한다
    public AbstractDao(JdbcTemplate jdbcTemplate, DataSource dataSource,
                       NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        clazz = (Class<T>) getGenericClassType(0);
        table = clazz.getSimpleName().replace("Entity", "");

        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(table)
                .usingGeneratedKeyColumns("id");

        fields = Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());

        getters = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.getName().contains("get")
                        || method.getName().contains("is")
                        || method.getName().contains("has"))
                .collect(Collectors.toList());
    }

    private Type getGenericClassType(int index) {
        Type type = getClass().getGenericSuperclass();

        while (!(type instanceof ParameterizedType)) {
            if (type instanceof ParameterizedType) {
                type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
            } else {
                type = ((Class<?>) type).getGenericSuperclass();
            }
        }

        return ((ParameterizedType) type).getActualTypeArguments()[index];
    }

    public T save(T t) {
        Map<String, Object> params = getParamsByT(t);
        params.values().removeAll(Collections.singleton(null));

        final long generatedId = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        final Field idField = ReflectionUtils.findField(clazz, "id");
        ReflectionUtils.setField(idField, t, generatedId);
        return t;
    }

    private Map<String, Object> getParamsByT(T t) {
        Map<String, Object> params = new HashMap<>();

        for (String field : fields) {
            final Field found = ReflectionUtils.findField(clazz, field);
            found.setAccessible(true);

            final Method method = getters.stream()
                    .filter(getter -> {
                        final String name = getter.getName();
                        if (name.startsWith("get")) {
                            return name.replaceFirst("get", "").equalsIgnoreCase(field);
                        }
                        if (name.startsWith("has")) {
                            return name.replaceFirst("has", "").equalsIgnoreCase(field);
                        }
                        if (name.startsWith("is")) {
                            return name.replaceFirst("is", "").equalsIgnoreCase(field);
                        }
                        throw new NoSuchElementException("save를 위한 Entity 값 할당에 실패했습니다");
                    })
                    .findAny()
                    .orElseThrow(NoSuchElementException::new);

            try {
                params.put(replaceField(field), method.invoke(t));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return params;
    }

    private String replaceField(String field) {
        StringBuilder stringBuilder = new StringBuilder();

        final char[] chars = field.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                stringBuilder.append("_");
            }
            stringBuilder.append(chars[i]);
        }

        return stringBuilder.toString();
    }

    public List<T> findAll() {
        final String sql = "SELECT * FROM " + table;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            final T tEntity;
            try {
                tEntity = clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            for (String field : fields) {
                final Field found = ReflectionUtils.findField(clazz, field);
                found.setAccessible(true);
                ReflectionUtils.setField(found, tEntity, rs.getObject(field));
            }
            return tEntity;
        });
    }


    public Optional<T> findById(ID id) {
        final String sql = "SELECT * FROM " + table + " WHERE id = ?";

        try {
            final T t = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final T tEntity;
                try {
                    tEntity = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                for (String field : fields) {
                    final Field found = ReflectionUtils.findField(clazz, field);
                    found.setAccessible(true);
                    ReflectionUtils.setField(found, tEntity, rs.getObject(field));
                }
                return tEntity;
            }, id);

            return Optional.ofNullable(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    public long updateById(T t) {
        if (!existsById(getIdByFromTByReflection(t))) {
            return 0;
        }
        final Map<String, Object> params = getParamsByT(t);

        String sql = "UPDATE " + table + " SET ";
        sql += fields.stream()
                .map(field -> field + " = :" + field)
                .collect(Collectors.joining(", "));
        sql += " WHERE id = :id";

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    private ID getIdByFromTByReflection(T t) {
        final Method getIdMethod = getters.stream()
                .filter(getter -> "getId".equals(getter.getName()))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
        try {
            return (ID) getIdMethod.invoke(t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public long deleteById(ID id) {
        final String sql = "DELETE FROM " + table + " WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
