package com.wxl.backend.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_auth")
public class UserAuth {

    /** 雪花算法生成，非数据库自增 */
    @TableId(value = "user_auth_id", type = IdType.ASSIGN_ID)
    private Long userAuthId;

    private Long userId;

    /** email_password / phone */
    private String authType;

    private String authValue;

    /** BCrypt 哈希，仅密码类凭证有值 */
    private String password;

    /** 1-已验证 0-未验证 */
    private Integer verified;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
