package io.moonshard.moonshard.helpers;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

import java.util.Random;

import io.moonshard.moonshard.MainApplication;
import io.moonshard.moonshard.R;
import io.moonshard.moonshard.common.utils.Utils;
import io.moonshard.moonshard.models.dbEntities.ChatEntity;
import io.moonshard.moonshard.models.dbEntities.ChatUser;
import io.moonshard.moonshard.models.dbEntities.MessageEntity;
import io.moonshard.moonshard.repository.ChatListRepository;
import io.moonshard.moonshard.repository.MessageRepository;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import trikita.log.Log;

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
                    if (!MainApplication.getCurrentChatActivity().equals(chatJid)) {
                        MainApplication.getXmppConnection().loadAvatar(chatJid)
                                .observeOn(Schedulers.io())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe(bytes -> {
                            Bitmap avatar = null;
                            if (bytes != null) {
                                avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            }
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.amu_bubble_mask)
                                    .setContentTitle(chatJid)
                                    .setContentText(message.getBody())
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            if (avatar != null) {
                                notification.setLargeIcon(avatar);
                            }
                            notificationManager.notify(new Random().nextInt(), notification.build());
                        }, throwable -> Log.e(throwable.getMessage()));
                    }
                });
    }

    public void subscribeOnMessage(Observer<MessageEntity> observer) {
        messagePubsub.subscribe(observer);
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

    @SuppressLint("CheckResult")
    @Override
    public void processMessage(Message message) {

        if (message.getBody() != null) {

            String roomName = (message.getFrom().toString().split("@conference.moonshard.tech"))[0];
            String roomJid = (message.getFrom().toString().split("/"))[0];


            Jid fromJid = message.getFrom();
            if (fromJid.getResourceOrEmpty().toString().equals(MainApplication.getCurrentLoginCredentials().username)) {
                return; // this is our message, dropping
            }

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

            if (!MainApplication.getCurrentChatActivity().equals(chatID)) {
                MainApplication.getXmppConnection().loadAvatar(chatID)
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(bytes -> {
                            Bitmap avatar = null;
                            if (bytes != null) {
                                avatar = Utils.INSTANCE.bytesToBitmap(bytes);
                            }
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.amu_bubble_mask)
                                    .setContentTitle(chatID)
                                    .setContentText(message.getBody())
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            if (avatar != null) {
                                notification.setLargeIcon(avatar);
                            }
                            notificationManager.notify(new Random().nextInt(), notification.build());
                        }, throwable -> Log.e(throwable.getMessage()));
            }
        }
    }

}
