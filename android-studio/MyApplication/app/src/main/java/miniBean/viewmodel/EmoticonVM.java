package miniBean.viewmodel;


public class EmoticonVM {
     public String name;
     public String code;
     public String url;

    public EmoticonVM() {
    }

    public EmoticonVM(String name, String code, String url) {
        this.name = name;
        this.code = code;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
