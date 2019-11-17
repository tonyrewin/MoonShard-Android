package io.moonshard.moonshard.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.instacart.library.truetime.TrueTime;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import io.moonshard.moonshard.MainApplication;
import io.moonshard.moonshard.R;
import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;
import java9.util.concurrent.CompletableFuture;

public class NetworkHandler implements IncomingChatMessageListener, PresenceEventListener, MessageListener {
    private final static String LOG_TAG = "NetworkHandler";
    private final static String NOTIFICATION_CHANNEL_ID = "InfluenceNotificationsChannel";

    PublishSubject<Long> msgs = PublishSubject.create();


    private NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainApplication.getContext());

    public NetworkHandler() {
        createNotificationChannel();
    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        String chatID = chat.getXmppAddressOfChatPartner().asUnescapedString();
        if (LocalDBWrapper.getChatByChatID(from.asEntityBareJidString()) == null) {
            LocalDBWrapper.createChatEntry(chatID, chat.getXmppAddressOfChatPartner().asBareJid().asUnescapedString().split("@")[0], new ArrayList<>(),false);
        }
        long messageID = LocalDBWrapper.createMessageEntry(chatID, message.getStanzaId(), from.asUnescapedString(), TrueTime.now().getTime(), message.getBody(), true, false);
        msgs.onNext(messageID);
        int newUnreadMessagesCount = LocalDBWrapper.getChatByChatID(chatID).unreadMessagesCount + 1;
        LocalDBWrapper.updateChatUnreadMessagesCount(chatID, newUnreadMessagesCount);

        //   EventBus.getDefault().post(new NewMessageEvent(chatID, messageID));
        // EventBus.getDefault().post(new LastMessageEvent(chatID, new GenericMessage(LocalDBWrapper.getMessageByID(messageID))));
        if (!MainApplication.getCurrentChatActivity().equals(chatID)) {

            byte[] avatarBytes = new byte[0];
            try {
                CompletableFuture<byte[]> future = loadAvatar(chatID);
                if (future != null) {
                    avatarBytes = future.get();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Bitmap avatar = null;
            if (avatarBytes != null) {
                avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
            }
            NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.amu_bubble_mask)
                    .setContentTitle(chatID)
                    .setContentText(message.getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            if (avatar != null) {
                notification.setLargeIcon(avatar);
            } else {
                String firstLetter = Character.toString(Character.toUpperCase(chatID.charAt(0)));
                Drawable avatarText = TextDrawable.builder()
                        .beginConfig()
                        .width(64)
                        .height(64)
                        .endConfig()
                        .buildRound(firstLetter, ColorGenerator.MATERIAL.getColor(firstLetter));
                notification.setLargeIcon(drawableToBitmap(avatarText));
            }
            notificationManager.notify(new Random().nextInt(), notification.build());
        }
    }

    public void subscribeOnMessage(Observer<Long> observer) {
        msgs.subscribe(observer);
    }

    public void unSubscribeOnMessage() {
        //msgs.onComplete();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void presenceAvailable(FullJid address, Presence availablePresence) {
        //   EventBus.getDefault().post(new UserPresenceChangedEvent(address.asBareJid().asUnescapedString(), availablePresence.isAvailable()));
    }

    @Override
    public void presenceUnavailable(FullJid address, Presence presence) {
        //EventBus.getDefault().post(new UserPresenceChangedEvent(address.asBareJid().asUnescapedString(), presence.isAvailable()));
    }

    @Override
    public void presenceError(Jid address, Presence errorPresence) {
        // EventBus.getDefault().post(new UserPresenceChangedEvent(address.asBareJid().asUnescapedString(), errorPresence.isAvailable()));
    }

    @Override
    public void presenceSubscribed(BareJid address, Presence subscribedPresence) {

    }

    @Override
    public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = MainApplication.getContext().getString(R.string.notification_channel_name);
            String description = MainApplication.getContext().getString(R.string.notification_channel_desc);
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            NotificationManager notificationManager = MainApplication.getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private CompletableFuture<byte[]> loadAvatar(String senderID) {
        if (senderID.length() != 0) {
            if (MainApplication.avatarsCache.containsKey(senderID)) {
                return CompletableFuture.completedFuture(MainApplication.avatarsCache.get(senderID));
            }
            CompletableFuture<byte[]> completableFuture = CompletableFuture.supplyAsync(() -> {
                while (MainApplication.getXmppConnection() == null) ;
                while (MainApplication.getXmppConnection().isConnectionAlive() != true) ;
                EntityBareJid jid = null;
                try {
                    jid = JidCreate.entityBareFrom(senderID);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
                return MainApplication.getXmppConnection().getAvatar(jid);
            }).thenApply((avatarBytes) -> {
                if (avatarBytes != null) {
                    MainApplication.avatarsCache.put(senderID, avatarBytes);
                }
                return avatarBytes;
            });
            return completableFuture;
        }
        return null;
    }

    @Override
    public void processMessage(Message message) {

        if(message.getBody()!=null){

        String roomName = (message.getFrom().toString().split("@conference.moonshard.tech"))[0];
        String roomJid = (message.getFrom().toString().split("/"))[0];

        //LocalDBWrapper.getChatByChatID("qaz@conference.moonshard.tech")

        String chatID = roomName;
        if (LocalDBWrapper.getChatByChatID(roomJid) == null) {
           LocalDBWrapper.createChatEntry(roomJid, roomJid.split("@")[0], new ArrayList<>(),true);
        }
        long messageID = LocalDBWrapper.createMessageEntry(roomJid, message.getStanzaId(), message.getFrom().asUnescapedString(), TrueTime.now().getTime(), message.getBody(), true, false);
        msgs.onNext(messageID);
       // int newUnreadMessagesCount = LocalDBWrapper.getChatByChatID(chatID).unreadMessagesCount + 1;
      //  LocalDBWrapper.updateChatUnreadMessagesCount(chatID, newUnreadMessagesCount);

        //   EventBus.getDefault().post(new NewMessageEvent(chatID, messageID));
        // EventBus.getDefault().post(new LastMessageEvent(chatID, new GenericMessage(LocalDBWrapper.getMessageByID(messageID))));
        if (!MainApplication.getCurrentChatActivity().equals(chatID)) {

            byte[] avatarBytes = new byte[0];
            try {
                CompletableFuture<byte[]> future = loadAvatar(chatID);
                if (future != null) {
                    avatarBytes = future.get();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Bitmap avatar = null;
            if (avatarBytes != null) {
                avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
            }
            NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.amu_bubble_mask)
                    .setContentTitle(chatID)
                    .setContentText(message.getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            if (avatar != null) {
                notification.setLargeIcon(avatar);
            } else {
                String firstLetter = Character.toString(Character.toUpperCase(chatID.charAt(0)));
                Drawable avatarText = TextDrawable.builder()
                        .beginConfig()
                        .width(64)
                        .height(64)
                        .endConfig()
                        .buildRound(firstLetter, ColorGenerator.MATERIAL.getColor(firstLetter));
                notification.setLargeIcon(drawableToBitmap(avatarText));
            }
            notificationManager.notify(new Random().nextInt(), notification.build());
        }}
    }

}
