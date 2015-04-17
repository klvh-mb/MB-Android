package miniBean.viewmodel;

public class CommentPost {
    public Long post_id;
    public String answerText;
    boolean withPhotos;
    boolean android;

    public CommentPost(Long post_id, String answerText, boolean withPhotos) {
        this.post_id = post_id;
        this.answerText = answerText;
        this.withPhotos = withPhotos;
        this.android = true;
    }

}
