package com.ek.mobileapp.model;

//model
public class UserDTO {
    private Long id;
    private String loginName;
    private String password;
    private String name;
    private String status;
    private String email;
    private Integer version;
    private String dbCodes;

    //密码状态,0必须修改1已修改
    private String updatePwd;

    private String roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //@Override
    //public String toString() {
    //    return ToStringBuilder.reflectionToString(this);
    //}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbCodes() {
        return dbCodes;
    }

    public void setDbCodes(String dbCodes) {
        this.dbCodes = dbCodes;
    }

    public String getUpdatePwd() {
        return updatePwd;
    }

    public void setUpdatePwd(String updatePwd) {
        this.updatePwd = updatePwd;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    //public List<RoleDTO> getRoleList() {
    //    return roleList;
    //}

    //public void setRoleList(List<RoleDTO> roleList) {
    //    this.roleList = roleList;
    //}
}
