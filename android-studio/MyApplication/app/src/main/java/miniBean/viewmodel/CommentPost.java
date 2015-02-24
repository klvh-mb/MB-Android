package miniBean.viewmodel;

public class CommentPost {
    public Long post_id;
    public String answerText;

    public CommentPost(Long post_id, String answerText) {
        this.post_id = post_id;
        this.answerText = answerText;
    }
}
