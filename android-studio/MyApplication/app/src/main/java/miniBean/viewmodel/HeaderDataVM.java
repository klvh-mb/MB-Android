package miniBean.viewmodel;

import java.util.List;

public class HeaderDataVM {
    public String name;
    public Long notifyCounts;
    public Long requestCounts;
    public List<NotificationVM> allNotif;
    public List<NotificationVM> requestNotif;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNotifyCounts() {
        return notifyCounts;
    }

    public void setNotifyCounts(Long notifyCounts) {
        this.notifyCounts = notifyCounts;
    }

    public Long getRequestCounts() {
        return requestCounts;
    }

    public void setRequestCounts(Long requestCounts) {
        this.requestCounts = requestCounts;
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
