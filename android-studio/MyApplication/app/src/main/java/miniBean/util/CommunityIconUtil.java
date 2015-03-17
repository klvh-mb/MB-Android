package miniBean.util;

import java.util.HashMap;
import java.util.Map;

import miniBean.R;

/**
 * Created by keithlei on 3/16/15.
 */
public class CommunityIconUtil {

    public final static int WIDTH = 18;
    public final static int HEIGHT = 18;

    public final static Map<String, Integer> iconsMap;

    static {
        iconsMap = new HashMap<>();
        iconsMap.put("/assets/app/images/general/icons/community/ball.png", R.drawable.ci_ball);
        iconsMap.put("/assets/app/images/general/icons/community/balloons.png", R.drawable.ci_balloons);
        iconsMap.put("/assets/app/images/general/icons/community/bath.png", R.drawable.ci_bath);
        iconsMap.put("/assets/app/images/general/icons/community/bean_blue.png", R.drawable.ci_bean_blue);
        iconsMap.put("/assets/app/images/general/icons/community/bean_green.png", R.drawable.ci_bean_green);
        iconsMap.put("/assets/app/images/general/icons/community/bean_orange.png", R.drawable.ci_bean_orange);
        iconsMap.put("/assets/app/images/general/icons/community/bean_red.png", R.drawable.ci_bean_red);
        iconsMap.put("/assets/app/images/general/icons/community/bean_yellow.png", R.drawable.ci_bean_yellow);
        iconsMap.put("/assets/app/images/general/icons/community/beans.png", R.drawable.ci_beans);
        iconsMap.put("/assets/app/images/general/icons/community/bed.png", R.drawable.ci_bed);
        iconsMap.put("/assets/app/images/general/icons/community/book.png", R.drawable.ci_book);
        iconsMap.put("/assets/app/images/general/icons/community/bottle.png", R.drawable.ci_bottle);
        iconsMap.put("/assets/app/images/general/icons/community/boy.png", R.drawable.ci_boy);
        iconsMap.put("/assets/app/images/general/icons/community/camera.png", R.drawable.ci_camera);
        iconsMap.put("/assets/app/images/general/icons/community/cat.png", R.drawable.ci_cat);
        iconsMap.put("/assets/app/images/general/icons/community/cloud.png", R.drawable.ci_cloud);
        iconsMap.put("/assets/app/images/general/icons/community/dad.png", R.drawable.ci_dad);
        iconsMap.put("/assets/app/images/general/icons/community/feedback.png", R.drawable.ci_feedback);
        iconsMap.put("/assets/app/images/general/icons/community/gift_box.png", R.drawable.ci_gift_box);
        iconsMap.put("/assets/app/images/general/icons/community/girl.png", R.drawable.ci_girl);
        iconsMap.put("/assets/app/images/general/icons/community/grad_hat.png", R.drawable.ci_grad_hat);
        iconsMap.put("/assets/app/images/general/icons/community/helmet.png", R.drawable.ci_helmet);
        iconsMap.put("/assets/app/images/general/icons/community/home.png", R.drawable.ci_home);
        iconsMap.put("/assets/app/images/general/icons/community/icecream.png", R.drawable.ci_icecream);
        iconsMap.put("/assets/app/images/general/icons/community/loc_area.png", R.drawable.ci_loc_area);
        iconsMap.put("/assets/app/images/general/icons/community/loc_city.png", R.drawable.ci_loc_city);
        iconsMap.put("/assets/app/images/general/icons/community/loc_district.png", R.drawable.ci_loc_district);
        iconsMap.put("/assets/app/images/general/icons/community/mom.png", R.drawable.ci_mom);
        iconsMap.put("/assets/app/images/general/icons/community/music_note.png", R.drawable.ci_music_note);
        iconsMap.put("/assets/app/images/general/icons/community/palette.png", R.drawable.ci_palette);
        iconsMap.put("/assets/app/images/general/icons/community/plane.png", R.drawable.ci_plane);
        iconsMap.put("/assets/app/images/general/icons/community/rainbow.png", R.drawable.ci_rainbow);
        iconsMap.put("/assets/app/images/general/icons/community/shirt.png", R.drawable.ci_shirt);
        iconsMap.put("/assets/app/images/general/icons/community/shopping_bag.png", R.drawable.ci_shopping_bag);
        iconsMap.put("/assets/app/images/general/icons/community/spoon_fork.png", R.drawable.ci_spoon_fork);
        iconsMap.put("/assets/app/images/general/icons/community/sports.png", R.drawable.ci_sports);
        iconsMap.put("/assets/app/images/general/icons/community/stroller.png", R.drawable.ci_stroller);
        iconsMap.put("/assets/app/images/general/icons/community/sun.png", R.drawable.ci_sun);
        iconsMap.put("/assets/app/images/general/icons/community/teddy.png", R.drawable.ci_teddy);
        iconsMap.put("/assets/app/images/general/icons/zodiac/dog.png", R.drawable.ci_z_dog);
        iconsMap.put("/assets/app/images/general/icons/zodiac/dragon.png", R.drawable.ci_z_dragon);
        iconsMap.put("/assets/app/images/general/icons/zodiac/goat.png", R.drawable.ci_z_goat);
        iconsMap.put("/assets/app/images/general/icons/zodiac/horse.png", R.drawable.ci_z_horse);
        iconsMap.put("/assets/app/images/general/icons/zodiac/monkey.png", R.drawable.ci_z_monkey);
        iconsMap.put("/assets/app/images/general/icons/zodiac/ox.png", R.drawable.ci_z_ox);
        iconsMap.put("/assets/app/images/general/icons/zodiac/pig.png", R.drawable.ci_z_pig);
        iconsMap.put("/assets/app/images/general/icons/zodiac/rabbit.png", R.drawable.ci_z_rabbit);
        iconsMap.put("/assets/app/images/general/icons/zodiac/rat.png", R.drawable.ci_z_rat);
        iconsMap.put("/assets/app/images/general/icons/zodiac/rooster.png", R.drawable.ci_z_rooster);
        iconsMap.put("/assets/app/images/general/icons/zodiac/snake.png", R.drawable.ci_z_snake);
        iconsMap.put("/assets/app/images/general/icons/zodiac/tiger.png", R.drawable.ci_z_tiger);
    }

    private CommunityIconUtil() {}

    public static int map(String url) {
        Integer icon = iconsMap.get(url);
        if (icon == null)
            return -1;
        return icon;
    }
}
