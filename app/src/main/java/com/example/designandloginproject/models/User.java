package com.example.designandloginproject.models;

import com.example.designandloginproject.R;
import com.example.designandloginproject.application.MyApplication;
import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.util.regex.Pattern;

public class User implements Serializable {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private String city;

    public User(String email, String firstName, String lastName, String phone, String gender, String city) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.gender = gender;
        this.city = city;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    /**
     * check if the user inserted email and password are correct
     *
     * @param email          email text
     * @param password       password text
     * @param emailLayout    TextInputLayout for the email
     * @param passwordLayout TextInputLayout for the password
     * @return true if user is valid, false if the inserted fields are not valid
     */
    public static boolean isUserValidateByEmailandPassword(
            String email, String password,
            TextInputLayout emailLayout, TextInputLayout passwordLayout) {

        boolean validateBoolean = true;
        if (!User.isEmailValid(email)) {
            emailLayout.setError(MyApplication.getInstance().getString(R.string.error_email));
            validateBoolean = false;
        } else {
            emailLayout.setError(null);
        }
        if (!Pattern.compile(MyApplication.getInstance().getString(R.string.password_special_char)).matcher(password).matches()) {
            passwordLayout.setError(MyApplication.getInstance().getString(R.string.error_password));
            validateBoolean = false;
        } else {
            passwordLayout.setError(null);
        }
        return validateBoolean;
    }
}
