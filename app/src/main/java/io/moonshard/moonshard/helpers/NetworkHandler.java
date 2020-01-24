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
import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Random;
import java.util.UUID;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import io.moonshard.moonshard.MainApplication;
import io.moonshard.moonshard.R;
import io.moonshard.moonshard.common.NotFoundException;
import io.moonshard.moonshard.models.dbEntities.ChatEntity;
import io.moonshard.moonshard.models.dbEntities.ChatUser;
import io.moonshard.moonshard.models.dbEntities.MessageEntity;
import io.moonshard.moonshard.repository.ChatListRepository;
import io.moonshard.moonshard.repository.ChatUserRepository;
import io.moonshard.moonshard.repository.MessageRepository;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import trikita.log.Log;

public class NetworkHandler extends DefaultParticipantStatusListener implements IncomingChatMessageListener, PresenceEventListener, MessageListener, InvitationListener {
    private final static String LOG_TAG = "NetworkHandler";
    private final static String NOTIFICATION_CHANNEL_ID = "InfluenceNotificationsChannel";
    private PublishSubject<MessageEntity> messagePubsub = PublishSubject.create();
    private NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainApplication.getContext());

    public NetworkHandler() {
        createNotificationChannel();
    }

    @Override
    public void kicked(EntityFullJid participant, Jid actor, String reason) {
        super.kicked(participant, actor, reason);

    }

    @SuppressLint("CheckResult")
    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        String chatJid = chat.getXmppAddressOfChatPartner().asUnescapedString();
        ChatListRepository.INSTANCE.getChatByJid(chat.getXmppAddressOfChatPartner().asBareJid())
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(chatEntity -> {
                    onIncomingMessageInternal(chatEntity, message, chatJid, from.asEntityBareJidString());
                }, e -> {
                    if (e.getClass() == NotFoundException.class) {
                        ChatEntity chatEntity = new ChatEntity(
                                0,
                                from.asEntityBareJidString(),
                                from.asEntityBareJidString().split("@")[0],
                                false,
                                0
                        );
                        ChatListRepository.INSTANCE.addChat(chatEntity)
                                .observeOn(Schedulers.io())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    onIncomingMessageInternal(chatEntity, message, chatJid, from.asEntityBareJidString());
                                });
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void onIncomingMessageInternal(ChatEntity chatEntity, Message message, String chatJid, String fromJid) {
        MessageEntity messageEntity = new MessageEntity(
                0,
                UUID.randomUUID().toString(),
                message.getStanzaId(),
                TrueTime.now().getTime(),
                message.getBody(),
                true,
                false,
                false,
                false
        );
        messageEntity.chat.setTarget(chatEntity);
        String messageAuthorNickname;

        if (fromJid.contains("conference")) {
            messageAuthorNickname = fromJid.split("/")[1];
        } else {
            messageAuthorNickname = fromJid.split("@")[0];
        }

        try {
            Jid jidFrom = JidCreate.from(fromJid);

            ChatUserRepository.INSTANCE.getUserAsSingle(jidFrom)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(user -> {
                                messageEntity.sender.setTarget(user);
                                saveMessage(messageEntity, chatJid, message, chatEntity);
                            }, throwable -> {
                                ChatUser user = new ChatUser(0, fromJid, messageAuthorNickname, -1, false);
                                ChatUserRepository.INSTANCE.saveUser(user);
                                messageEntity.sender.setTarget(user);
                                saveMessage(messageEntity, chatJid, message, chatEntity);
                            }
                    );
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    void saveMessage(MessageEntity messageEntity, String chatJid, Message message, ChatEntity chatEntity) {
        MessageRepository.INSTANCE.saveMessage(messageEntity)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    messagePubsub.onNext(messageEntity);
                    ChatListRepository.INSTANCE.updateUnreadMessagesCountByJid(JidCreate.bareFrom(chatEntity.getJid()), chatEntity.getUnreadMessagesCount() + 1).subscribe();
                    if (!MainApplication.getCurrentChatActivity().equals(chatJid)) {
                        Jid fromJid = message.getFrom();
                        Log.d(fromJid.asUnescapedString());
                        if (fromJid.getResourceOrEmpty().toString().equals(MainApplication.getCurrentLoginCredentials().username)) {
                            return; // this is our message, dropping
                        }
                        createNotification(chatJid, message);
                    }
                }, throwable -> {
                    Log.e(throwable.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    private void createNotification(String chatJid, Message message) {
        try {
            String myJid = SecurePreferences.getStringValue("jid", null);

            EntityBareJid groupId = JidCreate.entityBareFrom(chatJid);
            MultiUserChat muc =
                    MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().getConnection())
                            .getMultiUserChat(groupId);

            String jidFrom = muc.getOccupant(message.getFrom().asEntityFullJidIfPossible()).getJid().asBareJid().asUnescapedString();

            if (!myJid.equals(jidFrom)) {
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
        } catch (Exception e) {

        }
    }


    public void subscribeOnMessage(Observer<MessageEntity> observer) {
        messagePubsub.subscribe(observer);
    }

    @Override
    public void presenceAvailable(FullJid address, Presence availablePresence) {
        String kek = "";
        //   EventBus.getDefault().post(new UserPresenceChangedEvent(address.asBareJid().asUnescapedString(), availablePresence.isAvailable()));
    }

    @Override
    public void presenceUnavailable(FullJid address, Presence presence) {
        /*
        try {
            String kek = "";
            MUCUser user = (MUCUser) presence.getExtensions().get(0);
            HashSet<MUCUser.Status> statuses = (HashSet<MUCUser.Status>) user.getStatus();

            boolean isKicked = false;

            for (MUCUser.Status status : statuses) {
                if (status.getCode() == 110) {
                    isKicked = true;
                    break;
                }
            }

            if (isKicked) {
                    ChatListRepository.INSTANCE.getChatByJid(JidCreate.bareFrom(address.asUnescapedString().split("/")[0]))
                            .observeOn(Schedulers.io())
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(chatEntity -> {
                                ChatListRepository.INSTANCE.removeChat(chatEntity)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe();
                            }, exc -> {
                                String keks = "";
                            });
            }
        } catch (Exception e) {
            String keks = "";
        }

         */
    }

    @Override
    public void presenceError(Jid address, Presence errorPresence) {
        String kek = "";
    }

    @Override
    public void presenceSubscribed(BareJid address, Presence subscribedPresence) {
        String kek = "";
    }


    @Override
    public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {
        String kek = "";
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
        try {
            if (message.getBody() != null) {

                String roomName = (message.getFrom().toString().split("@conference.moonshard.tech"))[0];
                String roomJid = (message.getFrom().toString().split("/"))[0];

            Jid fromJid = message.getFrom();
            Log.d(fromJid.asUnescapedString());
            if (fromJid.getResourceOrEmpty().toString().equals(MainApplication.getCurrentLoginCredentials().username)) {
                return; // this is our message, dropping
            }

            String chatID = roomName;


                ChatListRepository.INSTANCE.getChatByJid(JidCreate.from(roomJid))
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(chatEntity -> {
                            onIncomingMessageInternal(chatEntity, message, roomJid, message.getFrom().asUnescapedString());
                        }, e -> {
                            if (e instanceof NotFoundException) {
                                ChatEntity chatEntity = new ChatEntity(
                                        0,
                                        roomJid,
                                        roomName,
                                        false,
                                        0
                                );
                                ChatListRepository.INSTANCE.addChat(chatEntity)
                                        .observeOn(Schedulers.io())
                                        .subscribeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            onIncomingMessageInternal(chatEntity, message, roomJid, message.getFrom().asUnescapedString());
                                        });
                            }
                        });

            if (!MainApplication.getCurrentChatActivity().equals(chatID)) {
                createNotification(chatID, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void banned(EntityFullJid participant, Jid actor, String reason) {
        super.banned(participant, actor, reason);
    }

    @Override
    public void invitationReceived(XMPPConnection conn, MultiUserChat room, EntityJid inviter, String reason, String password, Message message, MUCUser.Invite invitation) {

        ChatListRepository.INSTANCE.getChatByJid(room.getRoom())
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(chatEntity -> {
                    //onIncomingMessageInternal(chatEntity, message, chatJid, from.asEntityBareJidString());
                }, e -> {
                    if (e.getClass() == NotFoundException.class) {
                        try {
                            VCardManager vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().getConnection());
                            VCard card = vm.loadVCard();
                            Resourcepart nickname = Resourcepart.from(card.getNickName());

                            room.join(nickname); //while get invitation you need to join that room

                            RoomInfo info =
                                    MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().getConnection())
                                            .getRoomInfo(room.getRoom());

                            ChatEntity chatEntity = new ChatEntity(
                                    0,
                                    room.getRoom().asEntityBareJidString(),
                                    info.getName(),
                                    true,
                                    0
                            );
                            addChat(chatEntity);
                        } catch (Exception error) {
                            Logger.d(error);
                        }
                    }
                });
    }

    void addChat(ChatEntity chatEntity) {
        ChatListRepository.INSTANCE.addChat(chatEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    String sucess = "";
                    //  onIncomingMessageInternal(chatEntity, message,  room.getRoom().asEntityBareJidString(), inviter.asEntityBareJidString());
                }, e -> {
                    Logger.d(e);
                });
    }

    @Override
    public void joined(EntityFullJid participant) {
        super.joined(participant);

        try {
            String chatJid = participant.asUnescapedString().split("/")[0];
            String jidAuthor = participant.asUnescapedString().split("/")[1];

            ChatListRepository.INSTANCE.getChatByJid(JidCreate.from(chatJid))
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(chatEntity -> {
                        ChatUserRepository.INSTANCE.getUserAsSingle(JidCreate.from(participant))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(user -> {
                                            return;
                                        }, throwable -> {
                                            Logger.d(throwable);
                                            ChatUser user = new ChatUser(0, participant.asUnescapedString(), jidAuthor, -1, false);
                                            chatEntity.users.add(user);
                                            ChatUserRepository.INSTANCE.saveUser(user);
                                            onIncomingMessageJoin(chatEntity, chatJid, participant.asUnescapedString(), user);
                                        }
                                );
                    }, e -> {
                        Logger.d(e);
                    });
        } catch (Exception e) {
            Logger.d(e);
        }
    }

    @SuppressLint("CheckResult")
    private void onIncomingMessageJoin(ChatEntity chatEntity, String chatJid, String fromJid, ChatUser chatUser) {
        String author = fromJid.split("/")[1];
        String messageText = author + " присоединился к чату";

        MessageEntity messageEntity = new MessageEntity(
                0,
                UUID.randomUUID().toString(),
                null,
                TrueTime.now().getTime(),
                messageText,
                true,
                false,
                false,
                false
        );
        messageEntity.chat.setTarget(chatEntity);
        messageEntity.sender.setTarget(chatUser);
        saveMessageJoin(messageEntity, chatJid, chatEntity);
    }

    void saveMessageJoin(MessageEntity messageEntity, String chatJid, ChatEntity chatEntity) {
        MessageRepository.INSTANCE.saveMessage(messageEntity)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    messagePubsub.onNext(messageEntity);
                    ChatListRepository.INSTANCE.updateUnreadMessagesCountByJid(JidCreate.bareFrom(chatEntity.getJid()), chatEntity.getUnreadMessagesCount() + 1).subscribe();
                    if (!MainApplication.getCurrentChatActivity().equals(chatJid)) {
                        createNotificationJoin(chatJid, messageEntity.getText());
                    }
                }, throwable -> {
                    Log.e(throwable.getMessage());
                });
    }

    private void createNotificationJoin(String chatJid, String message) {
        MainApplication.getXmppConnection().loadAvatar(chatJid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bytes -> {
                    Bitmap avatar = null;
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.amu_bubble_mask)
                            .setContentTitle(chatJid)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    if (avatar != null) {
                        notification.setLargeIcon(avatar);
                    }
                    notificationManager.notify(new Random().nextInt(), notification.build());
                }, throwable -> Log.e(throwable.getMessage()));
    }

    @Override
    public void left(EntityFullJid participant) {
        super.left(participant);
    }

    @Override
    public void membershipRevoked(EntityFullJid participant) {
        super.membershipRevoked(participant);
    }
}
