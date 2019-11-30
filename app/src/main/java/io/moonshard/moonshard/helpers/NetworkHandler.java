package io.moonshard.moonshard.helpers;

import android.annotation.SuppressLint;
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

import java.util.Random;
import java.util.concurrent.ExecutionException;

import io.moonshard.moonshard.MainApplication;
import io.moonshard.moonshard.R;
import io.moonshard.moonshard.models.dbEntities.ChatEntity;
import io.moonshard.moonshard.models.dbEntities.ChatUser;
import io.moonshard.moonshard.models.dbEntities.MessageEntity;
import io.moonshard.moonshard.repository.ChatListRepository;
import io.moonshard.moonshard.repository.MessageRepository;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java9.util.concurrent.CompletableFuture;

public class NetworkHandler implements IncomingChatMessageListener, PresenceEventListener, MessageListener {
    private final static String LOG_TAG = "NetworkHandler";
    private final static String NOTIFICATION_CHANNEL_ID = "InfluenceNotificationsChannel";
    private PublishSubject<MessageEntity> messagePubsub = PublishSubject.create();
    private NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainApplication.getContext());
    private MessageRepository messageRepository = new MessageRepository();
    private ChatListRepository chatListRepository = new ChatListRepository();

    public NetworkHandler() {
        createNotificationChannel();
    }

    @SuppressLint("CheckResult")
    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        String chatJid = chat.getXmppAddressOfChatPartner().asUnescapedString();
        chatListRepository.getChatByJid(chat.getXmppAddressOfChatPartner().asBareJid())
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(chatEntity -> {
                    onIncomingMessageInternal(chatEntity, message, chatJid, from.asEntityBareJidString());
                }, e -> {
                    ChatEntity chatEntity = new ChatEntity(
                            0,
                            from.asEntityBareJidString(),
                            from.asEntityBareJidString().split("@")[0],
                            false,
                            0
                    );
                    chatListRepository.addChat(chatEntity)
                            .observeOn(Schedulers.io())
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                onIncomingMessageInternal(chatEntity, message, chatJid, from.asEntityBareJidString());
                            });
                });
    }

    @SuppressLint("CheckResult")
    private void onIncomingMessageInternal(ChatEntity chatEntity, Message message, String chatJid, String fromJid) {
        MessageEntity messageEntity = new MessageEntity(
                0,
                message.getStanzaId(),
                TrueTime.now().getTime(),
                message.getBody(),
                true,
                false,
                false
        );
        messageEntity.chat.setTarget(chatEntity);
        String messageAuthorNickname = fromJid.split("@")[0];
        messageEntity.sender.setTarget(new ChatUser(0, fromJid, messageAuthorNickname, -1, false));
        //noinspection ResultOfMethodCallIgnored
        messageRepository.saveMessage(messageEntity)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    messagePubsub.onNext(messageEntity);
                    chatListRepository.updateUnreadMessagesCountByJid(JidCreate.bareFrom(chatEntity.getJid()), chatEntity.getUnreadMessagesCount() + 1).subscribe();
                    // EventBus.getDefault().post(new NewMessageEvent(chatID, messageID));
                    // EventBus.getDefault().post(new LastMessageEvent(chatID, new GenericMessage(LocalDBWrapper.getMessageByID(messageID))));
                    if (!MainApplication.getCurrentChatActivity().equals(chatJid)) {

                        byte[] avatarBytes = new byte[0];
                        try {
                            CompletableFuture<byte[]> future = loadAvatar(chatJid);
                            if (future != null) {
                                avatarBytes = future.get();
                            }

                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                        Bitmap avatar = null;
                        if (avatarBytes != null) {
                            avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
                        }
                        NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                                .setSmallIcon(R.drawable.amu_bubble_mask)
                                .setContentTitle(chatJid)
                                .setContentText(message.getBody())
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        if (avatar != null) {
                            notification.setLargeIcon(avatar);
                        } else {
                            String firstLetter = Character.toString(Character.toUpperCase(chatJid.charAt(0)));
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
                });
    }

    public void subscribeOnMessage(Observer<MessageEntity> observer) {
        messagePubsub.subscribe(observer);
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

    public CompletableFuture<byte[]> loadAvatar(String senderID) {
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

        if (message.getBody() != null) {

            String roomName = (message.getFrom().toString().split("@conference.moonshard.tech"))[0];
            String roomJid = (message.getFrom().toString().split("/"))[0];


            message.getFrom();

            //LocalDBWrapper.getChatByChatID("qaz@conference.moonshard.tech")


            String chatID = roomName;
            //need if (LocalDBWrapper.getChatByChatID(roomJid) == null) {
            //need  LocalDBWrapper.createChatEntry(roomJid, roomJid.split("@")[0], new ArrayList<>(),true);
            //need   }


            try {
                chatListRepository.getChatByJid(JidCreate.from(roomJid))
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(chatEntity -> {
                            onIncomingMessageInternal(chatEntity, message, roomJid, message.getFrom().asUnescapedString());
                        }, e -> {
                            ChatEntity chatEntity = new ChatEntity(
                                    0,
                                    roomName,
                                    roomJid,
                                    false,
                                    0
                            );
                            chatListRepository.addChat(chatEntity)
                                    .observeOn(Schedulers.io())
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        onIncomingMessageInternal(chatEntity, message, roomJid, message.getFrom().asUnescapedString());
                                    });
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }

        /*long messageID = LocalDBWrapper.createMessageEntry(roomJid, message.getStanzaId(), message.getFrom().asUnescapedString(), TrueTime.now().getTime(), message.getBody(), true, false);
        messagePubsub.onNext(messageID);*/
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
            }
        }
    }

}
