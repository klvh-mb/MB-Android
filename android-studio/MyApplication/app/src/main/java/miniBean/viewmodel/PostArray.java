package miniBean.viewmodel;

import java.util.List;

/**
 * Created by User on 2/4/15.
 */
public class PostArray {
    public List<CommunityPostVM> posts;

    public List<CommunityPostVM> getPosts() {
        return posts;
    }

    public void setPosts(List<CommunityPostVM> posts) {
        this.posts = posts;
    }
}
