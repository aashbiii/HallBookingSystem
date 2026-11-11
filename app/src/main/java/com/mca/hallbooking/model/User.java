package com.mca.hallbooking.model;

public class User {
    public static final String ROLE_USER = "USER";
    public static final String ROLE_MANAGER = "MANAGER";

    private long id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;
    private String profileImage;
    private String department;

    public User() {
    }

    public User(long id, String name, String email, String phone, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public User(long id, String name, String email, String phone, String password, String role, String profileImage, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.profileImage = profileImage;
        this.department = department;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public boolean isManager() { return ROLE_MANAGER.equals(role); }
}
