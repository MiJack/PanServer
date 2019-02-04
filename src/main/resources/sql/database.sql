CREATE DATABASE IF NOT EXISTS `pan-server`;


CREATE TABLE IF NOT EXISTS user_info
(
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  username      VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户名',
  user_password VARCHAR(255) NOT NULL DEFAULT '' COMMENT '加密后的密码',
  avatar_url    VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像对应的uri',
  email         VARCHAR(255) NOT NULL DEFAULT '' COMMENT '邮箱',
  user_status   TINYINT(1)            DEFAULT 0 NOT NULL COMMENT '是否启用，0为禁用',
  UNIQUE INDEX `user_name_index` (`username`) USING BTREE COMMENT '用户名称索引',
  UNIQUE INDEX `user_email_index` (`email`) USING HASH COMMENT '用户邮件索引'
) COMMENT ='用户信息表';

CREATE TABLE IF NOT EXISTS user_role
(
  user_id     BIGINT DEFAULT 0 NOT NULL COMMENT '用户Id',
  role_name   VARCHAR(255)     NOT NULL COMMENT '角色名',
  role_status TINYINT(1)       NOT NULL COMMENT '0表示禁用，1表示启用',
  UNIQUE INDEX `user_role_rel` (`user_id`, `role_name`) USING BTREE
) COMMENT = '描述用户和权限角色的关系表';

CREATE TABLE IF NOT EXISTS restful_token
(
  user_id             INT                  DEFAULT - 1 NOT NULL COMMENT '用户Id' PRIMARY KEY,
  request_time_millis TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  expire_time_millis  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '失效时间',
  restful_token       VARCHAR(50) NOT NULL DEFAULT '' COMMENT 'restful请求的token',
  token_status        TINYINT(3)  NOT NULL DEFAULT 1 COMMENT 'ENABLE(1), DISABLE(2), EXPIRED(3)'
) COMMENT  ='restful token 表';
