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

    public final static Map<String, Integer> iconsMap;

    static {
        iconsMap = new HashMap<>();
        iconsMap.put("/assets/app/images/emoticons/angel.png", R.drawable.emo_angel);
        iconsMap.put("/assets/app/images/emoticons/bad.png", R.drawable.emo_bad);
        iconsMap.put("/assets/app/images/emoticons/blush.png", R.drawable.emo_blush);
        iconsMap.put("/assets/app/images/emoticons/cool.png", R.drawable.emo_cool);
        iconsMap.put("/assets/app/images/emoticons/cry.png", R.drawable.emo_cry);
        iconsMap.put("/assets/app/images/emoticons/dry.png", R.drawable.emo_dry);
        iconsMap.put("/assets/app/images/emoticons/frown.png", R.drawable.emo_frown);
        iconsMap.put("/assets/app/images/emoticons/gasp.png", R.drawable.emo_gasp);
        iconsMap.put("/assets/app/images/emoticons/grin.png", R.drawable.emo_grin);
        iconsMap.put("/assets/app/images/emoticons/happy.png", R.drawable.emo_happy);
        iconsMap.put("/assets/app/images/emoticons/huh.png", R.drawable.emo_huh);
        iconsMap.put("/assets/app/images/emoticons/laugh.png", R.drawable.emo_laugh);
        iconsMap.put("/assets/app/images/emoticons/love.png", R.drawable.emo_love);
        iconsMap.put("/assets/app/images/emoticons/mad.png", R.drawable.emo_mad);
        iconsMap.put("/assets/app/images/emoticons/ohmy.png", R.drawable.emo_ohmy);
        iconsMap.put("/assets/app/images/emoticons/ok.png", R.drawable.emo_ok);
        iconsMap.put("/assets/app/images/emoticons/smile.png", R.drawable.emo_smile);
        iconsMap.put("/assets/app/images/emoticons/teat.png", R.drawable.emo_teat);
        iconsMap.put("/assets/app/images/emoticons/teeth.png", R.drawable.emo_teeth);
        iconsMap.put("/assets/app/images/emoticons/tongue.png", R.drawable.emo_tongue);
        iconsMap.put("/assets/app/images/emoticons/wacko.png", R.drawable.emo_wacko);
        iconsMap.put("/assets/app/images/emoticons/wink.png", R.drawable.emo_wink);
    }

    private EmoticonUtil() {}

    public static int map(String url) {
        Integer icon = iconsMap.get(url);
        if (icon == null)
            return -1;
        return icon;
    }
}
