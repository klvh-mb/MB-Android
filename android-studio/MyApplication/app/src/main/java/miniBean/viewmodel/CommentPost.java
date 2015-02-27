package miniBean.viewmodel;

public class CommentPost {
    public Long post_id;
    public String answerText;
    boolean withPhotos;

    public CommentPost(Long post_id, String answerText, boolean withPhotos) {
        this.post_id = post_id;
        this.answerText = answerText;
        this.withPhotos = true;
    }

}
