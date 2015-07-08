package miniBean.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import miniBean.R;
import miniBean.activity.FullScreenActivity;
import miniBean.app.MyImageGetter;
import miniBean.app.UserInfoCache;
import miniBean.util.DateTimeUtil;
import miniBean.util.ImageUtil;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.MessageVM;

public class MessageListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MessageVM> messageVMs;
    private ImageView messageImages;
    private ImageView senderImage;
    private MyImageGetter imageGetter;

    public MessageListAdapter(Activity activity, List<MessageVM> messageVMs) {
        this.activity = activity;
        this.messageVMs = messageVMs;
        this.imageGetter = new MyImageGetter(activity);
    }

    @Override
    public int getCount() {
        return messageVMs.size();
    }

    @Override
    public MessageVM getItem(int location) {
        return messageVMs.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        final MessageVM m = messageVMs.get(position);

        Long userId1 = UserInfoCache.getUser().getId();
        Long userId2 = m.getSuid();

        // Identifying the message owner
        if (userId1.longValue() == userId2.longValue()) {
            // message belongs to you, so load the right aligned layout
            convertView = inflater.inflate(R.layout.list_item_message_right, null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = inflater.inflate(R.layout.list_item_message_left, null);
            senderImage = (ImageView) convertView.findViewById(R.id.senderImage);
            ImageUtil.displayThumbnailProfileImage(m.getSuid(), senderImage);
        }

        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        TextView dateMsg = (TextView) convertView.findViewById(R.id.messageDate);
        ViewUtil.setHtmlText(m.getTxt(), imageGetter, txtMsg, true, true);

        dateMsg.setText(DateTimeUtil.getTimeAgo(m.getCd()));

        messageImages = (ImageView) convertView.findViewById(R.id.messageImages);
        if(m.isHasImage()) {
            if(m.getImgs()!=null) {
                loadImages(m, messageImages);
            }
            messageImages.setVisibility(View.VISIBLE);
        } else {
            messageImages.setVisibility(View.GONE);
        }

        messageImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, FullScreenActivity.class);
                intent.putExtra("image",m.getImgs());
                activity.startActivity(intent);

                //Start details activity

                //ViewUtil.fullscreenImagePopup(activity, AppController.BASE_URL + "/image/get-original-private-image-by-id/"+m.getImgs());
            }
        });

        return convertView;
    }

    private void loadImages(MessageVM item, final ImageView messageImage) {

        messageImage.setAdjustViewBounds(true);
        messageImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        messageImage.setPadding(0, 0, 0, ViewUtil.getRealDimension(10, this.activity.getResources()));

        ImageUtil.displayOriginalMessageImage(item.getImgs(), messageImage, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage != null) {
                    Log.d(this.getClass().getSimpleName(), "onLoadingComplete: loaded bitmap - " + loadedImage.getWidth() + "|" + loadedImage.getHeight());

                    int width = loadedImage.getWidth();
                    int height = loadedImage.getHeight();

                    // always stretch to message width
                    int displayWidth = ViewUtil.getDisplayDimensions(MessageListAdapter.this.activity).width();
                    float scaleAspect = (float) displayWidth / (float) width;
                    width = displayWidth;
                    height = (int) (height * scaleAspect);

                    Log.d(this.getClass().getSimpleName(), "onLoadingComplete: after resize - " + width + "|" + height + " with scaleAspect=" + scaleAspect);

                    Drawable d = new BitmapDrawable(
                            MessageListAdapter.this.activity.getResources(),
                            Bitmap.createScaledBitmap(loadedImage, width, height, false));
                    ImageView imageView = (ImageView) view;
                    imageView.setImageDrawable(d);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
