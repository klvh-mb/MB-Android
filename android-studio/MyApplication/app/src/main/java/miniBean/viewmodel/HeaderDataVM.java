package miniBean.viewmodel;

import java.util.List;

public class HeaderDataVM {
    public String name;
    public Long notifyCount;
    public Long requestCount;
    public List<NotificationVM> allNotif;
    public List<NotificationVM> requestNotif;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNotifyCounts() {
        return notifyCount;
    }

    public void setNotifyCounts(Long notifyCounts) {
        this.notifyCount = notifyCounts;
    }

    public Long getRequestCounts() {
        return requestCount;
    }

    public void setRequestCounts(Long requestCounts) {
        this.requestCount = requestCounts;
    }

    public List<NotificationVM> getAllNotif() {
        return allNotif;
    }

    public void setAllNotif(List<NotificationVM> allNotif) {
        this.allNotif = allNotif;
    }

    public List<NotificationVM> getRequestNotif() {
        return requestNotif;
    }

    public void setRequestNotif(List<NotificationVM> requestNotif) {
        this.requestNotif = requestNotif;
    }
}
