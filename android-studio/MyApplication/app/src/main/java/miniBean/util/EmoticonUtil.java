package miniBean.util;

import java.util.HashMap;
import java.util.Map;

import miniBean.R;

/**
 * Created by keithlei on 3/16/15.
 */
public class EmoticonUtil {

    public final static int WIDTH = 18;
    public final static int HEIGHT = 18;

    public final static Map<String, Integer> emoticonsMap;

    static {
        emoticonsMap = new HashMap<>();
        emoticonsMap.put("/assets/app/images/emoticons/smile.png", R.drawable.emo_smile);
        emoticonsMap.put("/assets/app/images/emoticons/angel.png", R.drawable.emo_angel);
        emoticonsMap.put("/assets/app/images/emoticons/bad.png", R.drawable.emo_bad);
        emoticonsMap.put("/assets/app/images/emoticons/blush.png", R.drawable.emo_blush);
        emoticonsMap.put("/assets/app/images/emoticons/cool.png", R.drawable.emo_cool);
        emoticonsMap.put("/assets/app/images/emoticons/cry.png", R.drawable.emo_cry);
        emoticonsMap.put("/assets/app/images/emoticons/dry.png", R.drawable.emo_dry);
        emoticonsMap.put("/assets/app/images/emoticons/frown.png", R.drawable.emo_frown);
        emoticonsMap.put("/assets/app/images/emoticons/gasp.png", R.drawable.emo_gasp);
        emoticonsMap.put("/assets/app/images/emoticons/grin.png", R.drawable.emo_grin);
        emoticonsMap.put("/assets/app/images/emoticons/happy.png", R.drawable.emo_happy);
        emoticonsMap.put("/assets/app/images/emoticons/huh.png", R.drawable.emo_huh);
        emoticonsMap.put("/assets/app/images/emoticons/laugh.png", R.drawable.emo_laugh);
        emoticonsMap.put("/assets/app/images/emoticons/love.png", R.drawable.emo_love);
        emoticonsMap.put("/assets/app/images/emoticons/ohmy.png", R.drawable.emo_ohmy);
        emoticonsMap.put("/assets/app/images/emoticons/ok.png", R.drawable.emo_ok);
        emoticonsMap.put("/assets/app/images/emoticons/smile.png", R.drawable.emo_smile);
        emoticonsMap.put("/assets/app/images/emoticons/teat.png", R.drawable.emo_teat);
        emoticonsMap.put("/assets/app/images/emoticons/teeth.png", R.drawable.emo_teeth);
        emoticonsMap.put("/assets/app/images/emoticons/tongue.png", R.drawable.emo_tongue);
        emoticonsMap.put("/assets/app/images/emoticons/wacko.png", R.drawable.emo_wacko);
        emoticonsMap.put("/assets/app/images/emoticons/wink.png", R.drawable.emo_wink);
    }

    private EmoticonUtil() {}

    public static int map(String url) {
        Integer emoticon = emoticonsMap.get(url);
        if (emoticon == null)
            return -1;
        return emoticon;
    }
}
