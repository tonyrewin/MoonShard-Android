package io.moonshard.moonshard.helpers;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.webkit.URLUtil;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.instacart.library.truetime.TrueTimeRx;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.muc.UserStatusListener;
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
import io.moonshard.moonshard.ui.activities.MainActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import trikita.log.Log;

public class NetworkHandler extends DefaultParticipantStatusListener implements IncomingChatMessageListener, PresenceEventListener, MessageListener, InvitationListener, UserStatusListener, FileTransferListener {
    private final static String LOG_TAG = "NetworkHandler";
    private final static String NOTIFICATION_CHANNEL_ID = "InfluenceNotificationsChannel";
    PublishSubject<MessageEntity> messagePubsub = PublishSubject.create();
    private NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainApplication.getContext());

    public NetworkHandler() {
        createNotificationChannel();
    }


    //дергается у всех пользователей в т.ч и у того кто кикнул(им сообщается что пользователя кикнули)
    @Override
    public void kicked(EntityFullJid participant, Jid actor, String reason) {
        super.kicked(participant, actor, reason);
        try {
            EntityBareJid chatJid = participant.asEntityBareJidIfPossible();

            /*
            MultiUserChat muc = MainApplication.getXmppConnection().multiUserChatManager
                            .getMultiUserChat(chatJid);

            String nickNameAdmin = muc.getOccupant(actor.asEntityFullJidIfPossible()).getNick().toString();
             */

            String nickNameParticipant = participant.asUnescapedString().split("/")[1];
            String messageText = "Администратор" + " удалил " + nickNameParticipant + " из чата";

            MessageEntity messageEntity = new MessageEntity(
                    0,
                    UUID.randomUUID().toString(),
                    null,
                    TrueTimeRx.now().getTime(),
                    messageText,
                    true,
                    false,
                    false,
                    true, false
            );

            ChatListRepository.INSTANCE.getChatByJid(chatJid.asBareJid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(chatEntity -> {
                        messageEntity.chat.setTarget(chatEntity);
                        ChatUser user = new ChatUser(0, participant.asUnescapedString(), nickNameParticipant, -1, false);
                        messageEntity.sender.setTarget(user);
                        saveMessageKick(messageEntity, chatJid.asBareJid().asUnescapedString(), chatEntity, actor.asBareJid().asUnescapedString());
                    }, e -> {

                    });
        } catch (Exception e) {
            Logger.d(e);
        }
    }

    @SuppressLint("CheckResult")
    void saveMessageKick(MessageEntity messageEntity, String chatJid, ChatEntity chatEntity, String adminJid) {
        MessageRepository.INSTANCE.saveMessage(messageEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    messagePubsub.onNext(messageEntity);
                    ChatListRepository.INSTANCE.updateUnreadMessagesCountByJid(JidCreate.bareFrom(chatEntity.getJid()),
                            chatEntity.getUnreadMessagesCount() + 1).subscribe(() -> {

                    }, Logger::d);
                    if (!MainApplication.getCurrentChatActivity().equals(chatJid)) {
                        createNotificationKicked(chatJid, messageEntity.getText(), adminJid);
                    }
                }, throwable -> {
                    Log.e(throwable.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    private void createNotificationKicked(String chatJid, String message, String adminJid) {
        try {
            if (SecurePreferences.getBooleanValue("notification_state", true)) {

                String myJid = SecurePreferences.getStringValue("jid", null);

                EntityBareJid groupId = JidCreate.entityBareFrom(chatJid);

                MultiUserChat muc =
                        MainApplication.getXmppConnection().multiUserChatManager
                                .getMultiUserChat(groupId);

                if (!myJid.equals(adminJid)) {
                    String nickNameChat = MainApplication.getXmppConnection().multiUserChatManager
                            .getRoomInfo(muc.getRoom()).getName();
                    MainApplication.getXmppConnection().loadAvatar(chatJid, nickNameChat)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(bytes -> {
                                        Bitmap avatar = null;
                                        if (bytes != null) {
                                            avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        }
                                        NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                                                .setSmallIcon(R.drawable.amu_bubble_mask)
                                                .setContentTitle(nickNameChat)
                                                .setContentText(message)
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                        if (avatar != null) {
                                            notification.setLargeIcon(getCircleBitmap(avatar));
                                        }
                                        notificationManager.notify(new Random().nextInt(), notification.build());
                                    }, throwable ->
                                            Log.e(throwable.getMessage())
                            );
                }
            }
        } catch (Exception e) {
            Logger.d(e.getMessage());
        }
    }



    /*
        This method for 1x1 chat
     */
    @SuppressLint("CheckResult")
    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        String chatJid = chat.getXmppAddressOfChatPartner().asUnescapedString();
        ChatListRepository.INSTANCE.getChatByJid(chat.getXmppAddressOfChatPartner().asBareJid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chatEntity -> {
                    onIncomingMessageInternal(chatEntity, message, chatJid, from.asEntityBareJidString());
                }, e -> {
                    if (e.getClass() == NotFoundException.class) {
                            VCardManager vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().getConnection());
                            VCard card = vm.loadVCard(from);
                            String nickname = card.getNickName();
                            ChatEntity chatEntity = new ChatEntity(0, from.asEntityBareJidString(), nickname, false, 0);


                        ChatListRepository.INSTANCE.addChat(chatEntity)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    MainApplication.getXmppConnection().addInRoster(from);
                                    onIncomingMessageInternal(chatEntity, message, chatJid, from.asEntityBareJidString());
                                });
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void onIncomingMessageInternal(ChatEntity chatEntity, Message message, String chatJid, String fromJid) {
        try {
            MessageEntity messageEntity;
            if (URLUtil.isValidUrl(message.getBody())) {
                messageEntity = new MessageEntity(
                        0,
                        UUID.randomUUID().toString(),
                        message.getStanzaId(),
                        TrueTimeRx.now().getTime(),
                        message.getBody(),
                        true,
                        false,
                        false,
                        false,
                        true
                );
            } else {
                messageEntity = new MessageEntity(
                        0,
                        UUID.randomUUID().toString(),
                        message.getStanzaId(),
                        TrueTimeRx.now().getTime(),
                        message.getBody(),
                        true,
                        false,
                        false,
                        false,
                        false
                );
            }


            messageEntity.chat.setTarget(chatEntity);
            String messageAuthorNickname;

            if (fromJid.contains("conference")) {
                messageAuthorNickname = fromJid.split("/")[1];
            } else {
                messageAuthorNickname = fromJid.split("@")[0];
            }


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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    messagePubsub.onNext(messageEntity);
                    ChatListRepository.INSTANCE.updateUnreadMessagesCountByJid(JidCreate.bareFrom(chatEntity.getJid()), chatEntity.getUnreadMessagesCount() + 1).subscribe(() -> {

                    }, Logger::d);
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
            if (SecurePreferences.getBooleanValue("notification_state", true)) {

                String myJid = SecurePreferences.getStringValue("jid", null);

                if (chatJid.contains("conference")) {
                    EntityBareJid groupId = JidCreate.entityBareFrom(chatJid);


                    MultiUserChat muc =
                            MainApplication.getXmppConnection().multiUserChatManager
                                    .getMultiUserChat(groupId);

                    String jidFrom = muc.getOccupant(message.getFrom().asEntityFullJidIfPossible()).getJid().asBareJid().asUnescapedString();

                    if (!myJid.equals(jidFrom)) {
                        String nickNameChat = MainApplication.getXmppConnection().multiUserChatManager
                                .getRoomInfo(muc.getRoom()).getName();
                        MainApplication.getXmppConnection().loadAvatar(chatJid, nickNameChat)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(bytes -> {
                                            Bitmap avatar = null;
                                            if (bytes != null) {
                                                avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            }

                                            Intent intent = new Intent(MainApplication.getContext(), MainActivity.class);
                                            intent.putExtra("screen", "chat");
                                            intent.putExtra("chatId", chatJid);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            PendingIntent pendingIntent = PendingIntent.getActivity(MainApplication.getContext(), 0, intent, 0);

                                            NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.amu_bubble_mask)
                                                    .setContentTitle(nickNameChat)
                                                    .setContentText(message.getBody())
                                                    .setContentIntent(pendingIntent)
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            if (avatar != null) {
                                                notification.setLargeIcon(getCircleBitmap(avatar));
                                            }
                                            notificationManager.notify(new Random().nextInt(), notification.build());
                                        }, throwable ->
                                                Log.e(throwable.getMessage())
                                );
                    }
                } else {
                    EntityBareJid userId = JidCreate.entityBareFrom(chatJid);

                    VCardManager vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().getConnection());
                    VCard card = vm.loadVCard(userId);
                    String nickname = card.getNickName();

                    if (!myJid.equals(chatJid)) {
                        MainApplication.getXmppConnection().loadAvatar(chatJid, nickname)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(bytes -> {
                                            Bitmap avatar = null;
                                            if (bytes != null) {
                                                avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            }
                                            NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.amu_bubble_mask)
                                                    .setContentTitle(nickname)
                                                    .setContentText(message.getBody())
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            if (avatar != null) {
                                                notification.setLargeIcon(getCircleBitmap(avatar));
                                            }
                                            notificationManager.notify(new Random().nextInt(), notification.build());
                                        }, throwable ->
                                                Log.e(throwable.getMessage())
                                );
                    }
                }
            }
        } catch (Exception e) {
            String error = "";
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public void subscribeOnMessage(Observer<MessageEntity> observer) {
        messagePubsub.subscribe(observer);
    }

    @Override
    public void presenceAvailable(FullJid address, Presence availablePresence) {
        String kek = "";
    }

    @Override
    public void presenceUnavailable(FullJid address, Presence presence) {

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

    /*
      This method for groups (muc) chat
   */
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

                ChatListRepository.INSTANCE.getChatByJid(JidCreate.from(roomJid))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            onIncomingMessageInternal(chatEntity, message, roomJid, message.getFrom().asUnescapedString());
                                        });
                            }
                        });
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chatEntity -> {
                    //onIncomingMessageInternal(chatEntity, message, chatJid, from.asEntityBareJidString());
                }, e -> {
                    if (e.getClass() == NotFoundException.class) {
                        try {
                            MainApplication.getXmppConnection().joinChat(room.getRoom().asUnescapedString());
                            MainApplication.getXmppConnection().addUserStatusListener(room.getRoom().asUnescapedString());
                            MainApplication.getXmppConnection().addChatStatusListener(room.getRoom().asUnescapedString());

                            RoomInfo info =
                                    MainApplication.getXmppConnection().multiUserChatManager
                                            .getRoomInfo(room.getRoom());

                            ChatEntity chatEntity = new ChatEntity(
                                    0,
                                    room.getRoom().asEntityBareJidString(),
                                    info.getName(),
                                    true,
                                    0
                            );
                            addChat(chatEntity);

                            VCardManager vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().getConnection());

                            VCard cardInviter = vm.loadVCard(inviter.asEntityBareJid());
                            Resourcepart nickNameInviter = Resourcepart.from(cardInviter.getNickName());

                            String nameChat = MainApplication.getXmppConnection().multiUserChatManager
                                    .getRoomInfo(room.getRoom()).getName();

                            createNotificationInvite(room.getRoom().asEntityBareJidString(), nickNameInviter + " пригласил вас в чат " + "\"" + nameChat + "\"", nameChat);
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
                    //  onIncomingMessageInternal(chatEntity, message,  room.getRoomInfo().asEntityBareJidString(), inviter.asEntityBareJidString());
                }, e -> {
                    Logger.d(e);
                });
    }

    @SuppressLint("CheckResult")
    private void createNotificationInvite(String chatJid, String message, String nameChat) {
        try {
            if (SecurePreferences.getBooleanValue("notification_state", true)) {
                MainApplication.getXmppConnection().loadAvatar(chatJid, nameChat)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(bytes -> {
                                    Intent intent = new Intent(MainApplication.getContext(), MainActivity.class);
                                    intent.putExtra("screen", "chat");
                                    intent.putExtra("chatId", chatJid);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(MainApplication.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                    Bitmap avatar = null;
                                    if (bytes != null) {
                                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    }
                                    NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                                            .setSmallIcon(R.drawable.amu_bubble_mask)
                                            .setContentTitle("Вас пригласили в чат")
                                            .setContentText(message)
                                            .setContentIntent(pendingIntent)
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                    if (avatar != null) {
                                        notification.setLargeIcon(getCircleBitmap(avatar));
                                    }
                                    notificationManager.notify(new Random().nextInt(), notification.build());
                                }, throwable ->
                                        Log.e(throwable.getMessage())
                        );
            }
        } catch (Exception e) {
            Logger.d(e.getMessage());
        }
    }

    @Override
    public void joined(EntityFullJid participant) {
        super.joined(participant);
        try {
            String chatJid = participant.asUnescapedString().split("/")[0];

            if (chatJid.equals("support@conference.moonshard.tech")) return;

            String jidAuthor = participant.asUnescapedString().split("/")[1];

            ChatListRepository.INSTANCE.getChatByJid(JidCreate.from(chatJid))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(chatEntity -> {
                        ChatUserRepository.INSTANCE.getUserAsSingle(JidCreate.from(participant))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(user -> {
                                            return;
                                        }, throwable -> {
                                            if (throwable instanceof NotFoundException) {
                                                MultiUserChat muc = MainApplication.getXmppConnection().multiUserChatManager
                                                        .getMultiUserChat(JidCreate.entityBareFrom(chatEntity.getJid()));

                                                Occupant occupantUser = muc.getOccupant(participant.asEntityFullJidIfPossible());

                                                Logger.d(throwable);
                                                ChatUser user = new ChatUser(0, participant.asUnescapedString(), jidAuthor, -1, false);
                                                chatEntity.users.add(user);
                                                ChatUserRepository.INSTANCE.saveUser(user);
                                                onIncomingMessageJoin(chatEntity, chatJid, participant.asUnescapedString(), user, occupantUser.getJid().asBareJid());
                                            }
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
    private void onIncomingMessageJoin(ChatEntity chatEntity, String chatJid, String fromJid, ChatUser chatUser, BareJid fullJidUser) {
        try {
            String author = fromJid.split("/")[1];
            String messageText = author + " присоединился к чату";

            MessageEntity messageEntity = new MessageEntity(
                    0,
                    UUID.randomUUID().toString(),
                    null,
                    TrueTimeRx.now().getTime(),
                    messageText,
                    true,
                    false,
                    false,
                    true, false
            );
            messageEntity.chat.setTarget(chatEntity);
            messageEntity.sender.setTarget(chatUser);
            saveMessageJoin(messageEntity, chatJid, chatEntity, fullJidUser.asUnescapedString());
        } catch (Exception e) {
            Logger.d(e);
        }
    }

    void saveMessageJoin(MessageEntity messageEntity, String chatJid, ChatEntity chatEntity, String jidUser) {
        MessageRepository.INSTANCE.saveMessage(messageEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (messagePubsub.hasObservers()) {
                        messagePubsub.onNext(messageEntity);

                    }
                    ChatListRepository.INSTANCE.updateUnreadMessagesCountByJid(JidCreate.bareFrom(chatEntity.getJid()), chatEntity.getUnreadMessagesCount() + 1).subscribe(() -> {

                    }, Logger::d);
                    if (!MainApplication.getCurrentChatActivity().equals(chatJid)) {
                        createNotificationJoin(chatJid, messageEntity.getText(), jidUser);
                    }
                }, throwable -> {
                    Log.e(throwable.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    private void createNotificationJoin(String chatJid, String message, String jidUser) {
        try {
            if (SecurePreferences.getBooleanValue("notification_state", true)) {

                String myJid = SecurePreferences.getStringValue("jid", null);

                EntityBareJid groupId = JidCreate.entityBareFrom(chatJid);

                MultiUserChat muc =
                        MainApplication.getXmppConnection().multiUserChatManager
                                .getMultiUserChat(groupId);

                if (!myJid.equals(jidUser)) {
                    String nickNameChat = MainApplication.getXmppConnection().multiUserChatManager
                            .getRoomInfo(muc.getRoom()).getName();
                    MainApplication.getXmppConnection().loadAvatar(chatJid, nickNameChat)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(bytes -> {
                                        Bitmap avatar = null;
                                        if (bytes != null) {
                                            avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        }

                                        Intent intent = new Intent(MainApplication.getContext(), MainActivity.class);
                                        intent.putExtra("screen", "chat");
                                        intent.putExtra("chatId", chatJid);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(MainApplication.getContext(), 0, intent, 0);

                                        NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                                                .setSmallIcon(R.drawable.amu_bubble_mask)
                                                .setContentTitle(nickNameChat)
                                                .setContentText(message)
                                                .setContentIntent(pendingIntent)
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                        if (avatar != null) {
                                            notification.setLargeIcon(getCircleBitmap(avatar));
                                        }
                                        notificationManager.notify(new Random().nextInt(), notification.build());
                                    }, throwable ->
                                            Log.e(throwable.getMessage())
                            );
                }
            }
        } catch (Exception e) {
            Logger.d(e.getMessage());
        }
    }

    @Override
    public void left(EntityFullJid participant) {
        super.left(participant);
    }

    @Override
    public void kicked(Jid actor, String reason) {
        String test = "";
        //todo дергается у пользователя которого кикинули,actor почему-то 0
    }

    @Override
    public void voiceGranted() {
        String test = "";
    }

    @Override
    public void voiceRevoked() {
        String test = "";
    }

    @Override
    public void banned(Jid actor, String reason) {
        String test = "";
    }

    @Override
    public void membershipGranted() {
        String test = "";
    }

    @Override
    public void membershipRevoked() {
        String test = "";
    }

    @Override
    public void moderatorGranted() {
        String test = "";
    }

    @Override
    public void moderatorRevoked() {
        String test = "";
    }

    @Override
    public void ownershipGranted() {
        String test = "";
    }

    @Override
    public void ownershipRevoked() {
        String test = "";
    }

    @Override
    public void adminGranted() {
        String test = "";
    }

    @Override
    public void adminRevoked() {
        String test = "";
    }

    @Override
    public void roomDestroyed(MultiUserChat alternateMUC, String reason) {
        try {
            VCardManager vm =
                    VCardManager.getInstanceFor(MainApplication.getXmppConnection().getConnection());
            VCard card = vm.loadVCard();
            String nickname = card.getNickName();
            String chatJid = alternateMUC.getRoom().asUnescapedString();

            String messageText = nickname + " удалил чат";

            removeChatFromBd(chatJid, messageText, reason);
        } catch (Exception e) {
            Logger.d(e);
        }
    }

    private void removeChatFromBd(String chatJid, String messageText, String adminJid) {
        try {
            ChatListRepository.INSTANCE.getChatByJidSingle(JidCreate.from(chatJid))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(chat -> {
                        ChatListRepository.INSTANCE.removeChat(chat)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    createNotificationDestroyRoom(chatJid, messageText, adminJid, chat.getChatName());
                                }, throwable -> {
                                    Logger.d(throwable);
                                });
                    }, error -> {
                        Logger.d(error);
                    });
        } catch (Exception e) {
            Logger.d(e);
        }
    }

    @SuppressLint("CheckResult")
    private void createNotificationDestroyRoom(String chatJid, String message, String adminJid, String nickNameChat) {
        try {
            if (SecurePreferences.getBooleanValue("notification_state", true)) {

                String myJid = SecurePreferences.getStringValue("jid", null);

                if (!myJid.equals(adminJid)) {
                    MainApplication.getXmppConnection().loadAvatar(chatJid, nickNameChat)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(bytes -> {
                                        Intent intent = new Intent(MainApplication.getContext(), MainActivity.class);
                                        intent.putExtra("screen", "my_chats");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(MainApplication.getContext(), 0, intent, 0);

                                        Bitmap avatar = null;
                                        if (bytes != null) {
                                            avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        }
                                        NotificationCompat.Builder notification = new NotificationCompat.Builder(MainApplication.getContext(), NOTIFICATION_CHANNEL_ID)
                                                .setSmallIcon(R.drawable.amu_bubble_mask)
                                                .setContentTitle(nickNameChat)
                                                .setContentText(message)
                                                .setContentIntent(pendingIntent)
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                        if (avatar != null) {
                                            notification.setLargeIcon(getCircleBitmap(avatar));
                                        }
                                        notificationManager.notify(new Random().nextInt(), notification.build());
                                    }, throwable ->
                                            Log.e(throwable.getMessage())
                            );
                }
            }
        } catch (Exception e) {
            Logger.d(e.getMessage());
        }
    }

    @Override
    public void fileTransferRequest(FileTransferRequest request) {
        try {
            IncomingFileTransfer kek = request.accept();
            Bitmap bitmap = BitmapFactory.decodeStream(kek.receiveFile());
        } catch (Exception e) {
            String kek = e.getLocalizedMessage();
        }
    }
}
