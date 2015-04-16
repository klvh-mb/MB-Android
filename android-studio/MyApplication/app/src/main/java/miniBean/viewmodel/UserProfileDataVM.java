package miniBean.viewmodel;


public class UserProfileDataVM {

    public String parent_aboutme;           // "Its Me"
    public String parent_birth_year;        // "1992"
    public String parent_displayname;       //: "shashank"
    public String parent_firstname;         // "Shashank"
    public String parent_lastname;          // "Pohare"
   public int parent_location;          // 20

    public int getParent_location() {
        return parent_location;
    }

    public void setParent_location(int parent_location) {
        this.parent_location = parent_location;
    }

    public String getParent_aboutme() {
        return parent_aboutme;
    }

    public void setParent_aboutme(String parent_aboutme) {
        this.parent_aboutme = parent_aboutme;
    }

    public String getParent_birth_year() {
        return parent_birth_year;
    }

    public void setParent_birth_year(String parent_birth_year) {
        this.parent_birth_year = parent_birth_year;
    }

    public String getParent_displayname() {
        return parent_displayname;
    }

    public void setParent_displayname(String parent_displayname) {
        this.parent_displayname = parent_displayname;
    }

    public String getParent_firstname() {
        return parent_firstname;
    }

    public void setParent_firstname(String parent_firstname) {
        this.parent_firstname = parent_firstname;
    }

    public String getParent_lastname() {
        return parent_lastname;
    }

    public void setParent_lastname(String parent_lastname) {
        this.parent_lastname = parent_lastname;
    }

    /*public String getParent_location() {
        return parent_location;
    }

    public void setParent_location(String parent_location) {
        this.parent_location = parent_location;
    }*/
}

