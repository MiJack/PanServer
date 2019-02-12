/*
 * Copyright 2019 Mi&Jack
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mijack.panserver.mybatis.handlers;

import com.mijack.panserver.model.util.IdentifierEnum;
import com.mijack.panserver.util.EnumUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Mi&Jack
 */
public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private Class<E> type;
    private ValueType valueType;
    private int defaultId = -1;
    private String defaultValue = "";

    public EnumTypeHandler() {
    }

    public EnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        EnumData enumData = type.getAnnotation(EnumData.class);
        if (enumData != null) {
            valueType = enumData.valueType();
        } else {
            if (IdentifierEnum.class.isAssignableFrom(type)) {
                valueType = ValueType.ID;
            } else {
                valueType = ValueType.NAME;
            }
        }

        if (ValueType.ID.equals(valueType)) {
            if (!IdentifierEnum.class.isAssignableFrom(type)) {
                throw new IllegalStateException("ID必须与IdentifierEnum共用");
            }
            if (enumData != null) {
                defaultId = enumData.defaultId();
            }
        }
        if (ValueType.NAME.equals(valueType)) {
            if (enumData != null) {
                defaultValue = enumData.defaultValue();
            }
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        if (ValueType.ID.equals(valueType)) {
            ps.setInt(i, ((IdentifierEnum) parameter).id());
        }
        if (ValueType.NAME.equals(valueType)) {
            ps.setString(i, parameter.name());
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        if (ValueType.ID.equals(valueType)) {
            return EnumUtils.valueOf(rs.getInt(columnName), type, defaultId);
        }
        if (ValueType.NAME.equals(valueType)) {
            return EnumUtils.valueOf(rs.getString(columnName), type, defaultValue);
        }
        throw new IllegalStateException();
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        if (ValueType.ID.equals(valueType)) {
            return EnumUtils.valueOf(rs.getInt(columnIndex), type, defaultId);
        }
        if (ValueType.NAME.equals(valueType)) {
            return EnumUtils.valueOf(rs.getString(columnIndex), type, defaultValue);
        }
        throw new IllegalStateException();
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (ValueType.ID.equals(valueType)) {
            return EnumUtils.valueOf(cs.getInt(columnIndex), type, defaultId);
        }
        if (ValueType.NAME.equals(valueType)) {
            return EnumUtils.valueOf(cs.getString(columnIndex), type, defaultValue);
        }
        throw new IllegalStateException();
    }

}