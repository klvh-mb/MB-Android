package miniBean.viewmodel;

public class UserVM {
    public Long id;
    public String firstName;
    public String lastName;
    public String displayName;
    public String email;
    public String birthYear;
    public String gender;
    public String aboutMe;
    public LocationVM location;
    public Long noOfFriends;
    public int noOfGroups;
    public boolean isLoggedIn = false;
    public boolean isSA = false;
    public boolean isBA = false;
    public boolean isCA = false;
    public boolean isE = false;
    public boolean isAdmin = false;
    public boolean isMobile = false;
    public boolean isFbLogin = false;
    public boolean isHomeTourCompleted = false;
    public boolean enableSignInForToday = false;
    public Long questionsCount;
    public Long answersCount;
    public boolean emailValidated = false;
    public boolean newUser = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public LocationVM getLocation() {
        return location;
    }

    public void setLocation(LocationVM location) {
        this.location = location;
    }

    public Long getNoOfFriends() {
        return noOfFriends;
    }

    public void setNoOfFriends(Long noOfFriends) {
        this.noOfFriends = noOfFriends;
    }

    public int getNoOfGroups() {
        return noOfGroups;
    }

    public void setNoOfGroups(int noOfGroups) {
        this.noOfGroups = noOfGroups;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean isSA() {
        return isSA;
    }

    public void setSA(boolean isSA) {
        this.isSA = isSA;
    }

    public boolean isBA() {
        return isBA;
    }

    public void setBA(boolean isBA) {
        this.isBA = isBA;
    }

    public boolean isCA() {
        return isCA;
    }

    public void setCA(boolean isCA) {
        this.isCA = isCA;
    }

    public boolean isE() {
        return isE;
    }

    public void setE(boolean isE) {
        this.isE = isE;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isMobile() {
        return isMobile;
    }

    public void setMobile(boolean isMobile) {
        this.isMobile = isMobile;
    }

    public boolean isFbLogin() {
        return isFbLogin;
    }

    public void setFbLogin(boolean isFbLogin) {
        this.isFbLogin = isFbLogin;
    }

    public boolean isHomeTourCompleted() {
        return isHomeTourCompleted;
    }

    public void setHomeTourCompleted(boolean isHomeTourCompleted) {
        this.isHomeTourCompleted = isHomeTourCompleted;
    }

    public boolean isEnableSignInForToday() {
        return enableSignInForToday;
    }

    public void setEnableSignInForToday(boolean enableSignInForToday) {
        this.enableSignInForToday = enableSignInForToday;
    }

    public Long getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(Long questionsCount) {
        this.questionsCount = questionsCount;
    }

    public Long getAnswersCount() {
        return answersCount;
    }

    public void setAnswersCount(Long answersCount) {
        this.answersCount = answersCount;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }
}

