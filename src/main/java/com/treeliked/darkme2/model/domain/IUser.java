package com.treeliked.darkme2.model.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * TODO
 *
 * @author lss
 * @date 2020/12/29, 周二
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class IUser extends IBase {

    private String name;

    private String nick;

    private String email;

    private String password;

    private String gender;

    public IUser(String id) {
        super.setId(id);
    }
}
