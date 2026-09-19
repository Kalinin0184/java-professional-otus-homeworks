package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final String selectAllSql;
    private final String selectByIdSql;
    private final String insertSql;
    private final String updateSql;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {
        String tableName = entityClassMetaData.getName().toLowerCase();
        String idColumn = entityClassMetaData.getIdField().getName().toLowerCase();
        String columnsWithoutId = entityClassMetaData.getFieldsWithoutId().stream()
                .map(Field::getName)
                .map(String::toLowerCase)
                .collect(Collectors.joining(", "));
        String placeholders = entityClassMetaData.getFieldsWithoutId().stream()
                .map(field -> "?")
                .collect(Collectors.joining(", "));
        String setClause = entityClassMetaData.getFieldsWithoutId().stream()
                .map(field -> field.getName().toLowerCase() + " = ?")
                .collect(Collectors.joining(", "));

        this.selectAllSql = "select * from " + tableName;
        this.selectByIdSql = "select * from " + tableName + " where " + idColumn + " = ?";
        this.insertSql = "insert into " + tableName + "(" + columnsWithoutId + ") values (" + placeholders + ")";
        this.updateSql = "update " + tableName + " set " + setClause + " where " + idColumn + " = ?";
    }

    @Override
    public String getSelectAllSql() {
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        return updateSql;
    }
}
