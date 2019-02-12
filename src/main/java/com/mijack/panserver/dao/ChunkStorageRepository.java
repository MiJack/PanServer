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

package com.mijack.panserver.dao;

import com.mijack.panserver.model.StorageStatus;
import com.mijack.panserver.model.StorageUnit;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mi&Jack
 */
@Repository
public interface ChunkStorageRepository {
    /**
     * 保存Chunk
     *
     * @param chunk
     */
    @Insert("INSERT INTO chunk_meta_data" +
            "( min_display_unit_id , uri , uploader_id , chunk_length , chunk_index , chunk_count , " +
            "  upload_token , storage_status , create_time , update_time )" +
            " VALUES (#{chunk.minDisplayUnit.id},#{chunk.uri},#{chunk.uploader.id},#{chunk.length},#{chunk.index}," +
            "  #{chunk.count},#{chunk.updateToken},#{chunk.status},#{chunk.createTime},#{chunk.updateTime})")
    void saveStorageUnitChunk(@Param("chunk") StorageUnit.Chunk chunk);

    /**
     * 更新Chunk
     *
     * @param mduId
     * @param chunkIndex
     * @param resourceUri
     * @param storageStatus
     */
    @Update(" UPDATE chunk_meta_data " +
            " set " +
            " storage_status  = #{status}, " +
            " uri = #{resourceUri} " +
            " where min_display_unit_id = #{mduId} " +
            " and chunk_index = #{chunkIndex} " +
            " and chunk_index = #{chunkIndex} ")
    void uploadChunkInfo(@Param("mduId") long mduId, @Param("chunkIndex") long chunkIndex,
                         @Param("resourceUri") String resourceUri, @Param("status") StorageStatus storageStatus);

    /**
     * 筛选所有的chunk
     *
     * @param mduId
     * @return
     */
    @Select(" select " +
            " min_display_unit_id , uri , uploader_id , chunk_length , chunk_index , chunk_count , " +
            " upload_token , storage_status , create_time , update_time " +
            " from chunk_meta_data where min_display_unit_id = #{mduId}")
    @Results(
            @Result(column = "storage_status", property = "status", jdbcType = JdbcType.INTEGER, javaType = StorageStatus.class)
    )
    List<StorageUnit.Chunk> findAllChunkInfoByStorageUnitId(@Param("mduId") long mduId);
}
