package com.wxl.backend.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user")
public class SysUser {

    /** 雪花算法生成，非数据库自增 */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long userId;

    private String nickname;

    private String avatar;

    /** 1-正常 2-禁用 3-锁定 */
    private Integer status;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime lockedUntil;

    private LocalDateTime lastLoginAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
