package com.weasudo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    @TableId(value = "id",type = IdType.INPUT)
    private String id;
    private String name;
    private String password;
    private String avatarUrl;//头像图片url
    private String role;//用户角色，默认为"user"，管理员为"admin"
    @TableLogic
    private boolean isDeleted;

}
