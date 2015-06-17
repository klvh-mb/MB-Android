package miniBean.viewmodel;

public class MessagePostVM {
    public Long receiver_id;
    public String msgText;
    boolean withPhotos;

    public MessagePostVM(Long receiver_id, String msgText, boolean withPhotos) {
        this.receiver_id = receiver_id;
        this.msgText = msgText;
        this.withPhotos = withPhotos;
    }
}
