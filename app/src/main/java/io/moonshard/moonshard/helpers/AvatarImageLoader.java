package io.moonshard.moonshard.helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import io.moonshard.moonshard.MainApplication;
import io.moonshard.moonshard.R;
import java9.util.concurrent.CompletableFuture;

public class AvatarImageLoader  {
    private Fragment fragment = null;
    private Activity activity = null;

    public AvatarImageLoader(Fragment fragment) {
        this.fragment = fragment;
    }

    public AvatarImageLoader(Activity activity) {
        this.activity = activity;
    }

    public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
        if(url.length() != 0) {
            if(!url.contains("http")) {
                if(MainApplication.avatarsCache.containsKey(url)) {
                    byte[] avatarBytes = MainApplication.avatarsCache.get(url);
                    Bitmap avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
                    imageView.setImageBitmap(avatar);
                    return;
                }
                String firstLetter = Character.toString(Character.toUpperCase(url.charAt(0)));
                imageView.setImageDrawable(TextDrawable.builder()
                        .beginConfig()
                        .width(64)
                        .height(64)
                        .endConfig()
                        .buildRound(firstLetter, ColorGenerator.MATERIAL.getColor(firstLetter)));
                CompletableFuture.supplyAsync(() -> {
                    while (MainApplication.getXmppConnection() == null);
                    while (MainApplication.getXmppConnection().isConnectionAlive() != true);
                    EntityBareJid jid = null;
                    try {
                        jid = JidCreate.entityBareFrom(url);
                    } catch (XmppStringprepException e) {
                        e.printStackTrace();
                    }
                    return MainApplication.getXmppConnection().getAvatar(jid);
                }).thenAccept((avatarBytes) -> {
                    MainApplication.getMainUIThread().post(() -> {
                        if(avatarBytes != null) {
                            Bitmap avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
                            imageView.setImageBitmap(avatar);
                            MainApplication.avatarsCache.put(url, avatarBytes);
                        }
                    });
                });
            } else {
                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.circle)
                        .error(R.drawable.circle);
                if(fragment != null) {
                    Glide.with(fragment).setDefaultRequestOptions(requestOptions).load(url).into(imageView);
                } else if(activity != null) {
                    Glide.with(activity).setDefaultRequestOptions(requestOptions).load(url).into(imageView);
                }
            }
        }
    }
}
