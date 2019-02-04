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

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

/**
 * @author Mi&Jack
 */
@Repository
public interface AuthTokenRepository {
    /**
     * 根据token查找User
     *
     * @param token 待查询的token
     * @return
     */
    @Select("select user_id from restful_token where restful_token  = #{token}")
    long findUserByToken(@Param("token") String token);


    /**
     * 添加新的UserToken
     *
     * @param userId            user Id
     * @param requestTimeMillis 请求的时间点
     * @param expireTimeMillis  token过期的时间点
     * @param restfulToken      token
     */
    @Insert("INSERT INTO restful_token(user_id, request_time_millis,expire_time_millis,restful_token,token_status)" +
            " VALUES (#{user_id}, #{request_time_millis},#{expire_time_millis},#{restful_token},0) " +
            " ON DUPLICATE KEY UPDATE " +
            "   request_time_millis = #{request_time_millis}," +
            "   expire_time_millis = #{expire_time_millis} ," +
            "   restful_token = #{restful_token} , " +
            "   token_status = 0 ")
    void saveRestfulToken(@Param("user_id") long userId, @Param("request_time_millis") Timestamp requestTimeMillis,
                          @Param("expire_time_millis") Timestamp expireTimeMillis, @Param("restful_token") String restfulToken);
}
