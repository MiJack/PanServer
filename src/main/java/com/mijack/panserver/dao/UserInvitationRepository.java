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

import com.mijack.panserver.model.UserInvitation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Repository;

/**
 * @author Mi&Jack
 */
@Repository
public interface UserInvitationRepository {
    /**
     * 根据邮箱查找UserInvitation
     *
     * @param email
     * @return
     */
    @Select("select id, email, invitation_token, operator, invite_time_millis, expire_time_millis, token_status" +
            " from user_invitation" +
            " where email = #{email}")
    @Results({
            @Result(column = "invitation_token", property = "token"),
            @Result(column = "token_status", property = "status"),
            @Result(column = "invite_time_millis", property = "inviteTime"),
            @Result(column = "operator", property = "operator",
                    one = @One(select = "com.mijack.panserver.dao.UserRepository.findUserById",
                            fetchType = FetchType.DEFAULT)
            ),
            @Result(column = "expire_time_millis", property = "expireTime")
    })
    UserInvitation findInvitationByEmail(@Param("email") String email);

    /**
     * 插入新的UserInvitation
     *
     * @param userInvitation
     */
    @Insert("INSERT INTO user_invitation(email, invitation_token, operator, invite_time_millis, expire_time_millis,token_status)" +
            " VALUES (#{invitation.email}," +
            " #{invitation.token}, " +
            " #{invitation.operator.id}, " +
            " #{invitation.inviteTime}," +
            " #{invitation.expireTime},#{invitation.status})")
    void insertInvitation(@Param("invitation") UserInvitation userInvitation);

    /**
     * 根据invitationToken查找UserInvitation
     *
     * @param invitationToken
     * @return
     */

    @Select("select id, email, invitation_token, operator, invite_time_millis, expire_time_millis, token_status" +
            " from user_invitation" +
            " where invitation_token = #{token} ")
    @Results({
            @Result(column = "invitation_token", property = "token"),
            @Result(column = "token_status", property = "status"),
            @Result(column = "invite_time_millis", property = "inviteTime"),
            @Result(column = "operator", property = "operator",
                    one = @One(select = "com.mijack.panserver.dao.UserRepository.findUserById",
                            fetchType = FetchType.DEFAULT)
            ),
            @Result(column = "expire_time_millis", property = "expireTime")
    })
    UserInvitation findInvitationByToken(@Param("token") String invitationToken);

    /**
     * 更新id为userInvitationId的UserInvitation
     *
     * @param userInvitationId
     * @param userInvitation
     */
    @Update("update  user_invitation" +
            " set " +
            " email = #{invitation.email}," +
            " invitation_token = #{invitation.token}, " +
            " operator = #{invitation.operator.id}, " +
            " invite_time_millis = #{invitation.inviteTime}," +
            " invite_time_millis = #{invitation.expireTime}," +
            " token_status = #{invitation.status}" +
            " where id = #{userInvitationId}")
    void updateUserInvitation(@Param("userInvitationId") long userInvitationId,
                              @Param("invitation") UserInvitation userInvitation);
}
