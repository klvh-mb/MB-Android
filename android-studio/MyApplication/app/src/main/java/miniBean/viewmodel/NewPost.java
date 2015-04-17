package miniBean.viewmodel;

public class NewPost {
    public Long community_id;
    public String questionTitle,questionText;
    boolean withPhotos;
    boolean android;

    public NewPost(Long community_id, String questionTitle,String questionText, boolean withPhotos) {
        this.community_id = community_id;
        this.questionTitle = questionTitle;
        this.questionText=questionText;
        this.withPhotos = withPhotos;
        this.android = true;
    }

}
