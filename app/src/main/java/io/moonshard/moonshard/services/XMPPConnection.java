package io.moonshard.moonshard.services;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.UUID;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import io.moonshard.moonshard.EmptyLoginCredentialsException;
import io.moonshard.moonshard.LoginCredentials;
import io.moonshard.moonshard.MainApplication;
import io.moonshard.moonshard.common.utils.Utils;
import io.moonshard.moonshard.helpers.NetworkHandler;
import io.moonshard.moonshard.repository.ChatListRepository;
import io.moonshard.moonshard.ui.activities.BaseActivity;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import trikita.log.Log;

import static io.moonshard.moonshard.MainApplication.getLoginActivity;

public class XMPPConnection implements ConnectionListener {
    private final static String LOG_TAG = "XMPPConnection";
    private LoginCredentials credentials = new LoginCredentials();
    private XMPPTCPConnection connection = null;
    private NetworkHandler networkHandler;
    private Roster roster;
    private MamManager mamManager;
    public MultiUserChatManager multiUserChatManager = null;
    public ChatManager chatManager = null;

    public enum ConnectionState {
        CONNECTED,
        DISCONNECTED
    }

    public enum SessionState {
        LOGGED_IN,
        LOGGED_OUT
    }

    public XMPPConnection() {
        String jid = SecurePreferences.getStringValue("jid", null);
        String password = SecurePreferences.getStringValue("pass", null);

        if (jid != null && password != null) {
            String username = jid.split("@")[0];
            String jabberHost = jid.split("@")[1];
            credentials.username = username;
            credentials.jabberHost = jabberHost;
            credentials.password = password;
            MainApplication.setCurrentLoginCredentials(credentials);
            try {
                MainApplication.setJid(JidCreate.from(username + "@" + jabberHost).asUnescapedString());
            } catch (XmppStringprepException e) {
                Log.e(e.getMessage());
            }
        }
        networkHandler = new NetworkHandler();
    }

    public LoginCredentials getLoginCredentials() {
        return credentials;
    }

    public Jid getJid() throws XmppStringprepException {
        return JidCreate.bareFrom(credentials.username + "@" + credentials.jabberHost);
    }

    public void connect() throws XMPPException, IOException, SmackException, EmptyLoginCredentialsException {

        //   if (credentials.isEmpty()) {
        //       throw new EmptyLoginCredentialsException();
        //   }

        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(credentials.jabberHost)
                .setHost(credentials.jabberHost)
                .setResource(MainApplication.APP_NAME + "." + UUID.randomUUID().toString())
                .setKeystoreType(null)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                .setCompressionEnabled(true)
                .setConnectTimeout(7000)
                .build();

        connection = new XMPPTCPConnection(conf);
        connection.addConnectionListener(this);

        if (credentials.jabberHost.equals("") && credentials.password.equals("") && credentials.username.equals("")) {
            throw new IOException();
        }
        try {
            connection.connect();
            SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");

            if (!credentials.username.equals("") || !credentials.password.equals("")) {
                connection.login(credentials.username, credentials.password);
            }

        } catch (Exception e) {
            BaseActivity baseActivity = getLoginActivity();
            if (baseActivity != null) {
                baseActivity.onError(e);
            }
            e.printStackTrace();
        }

        chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addIncomingListener(networkHandler);
        chatManager.addOutgoingListener(networkHandler);
        roster = Roster.getInstanceFor(connection);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        roster.addPresenceEventListener(networkHandler);
        MainApplication.setJid(credentials.username + "@" + credentials.jabberHost);
        FileTransferNegotiator.IBB_ONLY = true;

        if (MainApplication.isIsMainActivityDestroyed()) {
            sendUserPresence(new Presence(Presence.Type.unavailable));
        }
        multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        multiUserChatManager.addInvitationListener(networkHandler);

        setStatus(true, "ONLINE");

    }

    public void disconnect() {
        SecurePreferences.setValue("logged_in", false);
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    public void disconnectMain() {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    public void setStatus(boolean available, String status) throws XMPPException {
        Presence.Type type = available ? Presence.Type.available : Presence.Type.unavailable;
        Presence presence = new Presence(type);
        presence.setStatus(status);
        try {
            connection.sendStanza(presence);
        } catch (Exception e) {
            Logger.d(e);
        }
    }

    @Override
    public void connected(org.jivesoftware.smack.XMPPConnection connection) {
        XMPPConnectionService.CONNECTION_STATE = ConnectionState.CONNECTED;
    }

    public NetworkHandler getNetwork() {
        return networkHandler;
    }

    @Override
    public void authenticated(org.jivesoftware.smack.XMPPConnection connection, boolean resumed) {
        XMPPConnectionService.SESSION_STATE = SessionState.LOGGED_IN;
        SecurePreferences.setValue("logged_in", true);
        try {
            // setStatus(true,"ONLINE");
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

        BaseActivity baseActivity = getLoginActivity();
        if (baseActivity != null) {
            baseActivity.onAuthenticated();
        }

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(MainApplication.getXmppConnection().getConnection());
        ReconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

        joinAllChats();
    }

    @Override
    public void connectionClosed() {
        XMPPConnectionService.CONNECTION_STATE = ConnectionState.DISCONNECTED;
        XMPPConnectionService.SESSION_STATE = SessionState.LOGGED_OUT;
        SecurePreferences.setValue("logged_in", false);
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        XMPPConnectionService.CONNECTION_STATE = ConnectionState.DISCONNECTED;
        XMPPConnectionService.SESSION_STATE = SessionState.LOGGED_OUT;
        SecurePreferences.setValue("logged_in", false);
        Log.e(LOG_TAG, "Connection closed, exception occurred");
        e.printStackTrace();
    }

    public String sendMessage(EntityBareJid recipientJid, String messageText) {
        Chat chat = chatManager.chatWith(recipientJid);
        try {
            Message message = new Message(recipientJid, Message.Type.chat);
            message.setBody(messageText);
            chat.send(message);
            return message.getStanzaId();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            if (connectAndLogin()) {
                sendMessage(recipientJid, messageText);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendFile(EntityFullJid jid, File file) {
        //  try {

        Observable.fromCallable(() -> {
            HttpFileUploadManager manager = HttpFileUploadManager.getInstanceFor(connection);
            try {
                Single.just(manager.uploadFile(file));
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    String kek = "";
                    //Use result for something
                });
    }

    Single<URL> getTest(File file) {
        HttpFileUploadManager manager = HttpFileUploadManager.getInstanceFor(connection);
        try {
            return Single.just(manager.uploadFile(file));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendMessageGroupChat(EntityBareJid recipientJid, String messageText) {
        try {
            Message message = new Message(recipientJid, Message.Type.groupchat);
            message.setBody(messageText);
            try {
                MainApplication.getXmppConnection().connection.sendStanza(message);
                return message.getStanzaId();
            } catch (SmackException.NotConnectedException ex) {
                ex.printStackTrace();
                if (connectAndLogin()) {
                    sendMessageGroupChat(recipientJid, messageText);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Single<byte[]> loadAvatar(String senderID) {
        return Single.create(emitter -> {
            if (!senderID.isEmpty()) {
                if (MainApplication.avatarsCache.containsKey(senderID)) {
                    emitter.onSuccess(MainApplication.avatarsCache.get(senderID));
                    return;
                }
                if (MainApplication.getXmppConnection() == null || !MainApplication.getXmppConnection().isConnectionReady()) {
                    emitter.onSuccess(createTextAvatar(Character.toString(Character.toUpperCase(senderID.charAt(0)))));
                    return;
                }
                EntityBareJid jid = null;
                try {
                    jid = JidCreate.entityBareFrom(senderID);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                byte[] avatarBytes = MainApplication.getXmppConnection().getAvatar(jid);

                if (avatarBytes != null) {
                    MainApplication.avatarsCache.put(senderID, avatarBytes);
                } else {
                    avatarBytes = createTextAvatar(Character.toString(Character.toUpperCase(senderID.charAt(0))));
                }
                emitter.onSuccess(avatarBytes);
            } else {
                emitter.onError(new IllegalArgumentException());
            }
        });
    }

    public Single<byte[]> loadAvatar(String senderID, String nameChat) {
        return Single.create(emitter -> {
            if (!senderID.isEmpty()) {
                if (MainApplication.avatarsCache.containsKey(senderID)) {
                    emitter.onSuccess(MainApplication.avatarsCache.get(senderID));
                }
                if (MainApplication.getXmppConnection() == null || !MainApplication.getXmppConnection().isConnectionReady()) {
                    emitter.onSuccess(createTextAvatar(Character.toString(Character.toUpperCase(nameChat.charAt(0)))));
                    return;
                }
                EntityBareJid jid = null;
                try {
                    jid = JidCreate.entityBareFrom(senderID);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                byte[] avatarBytes = MainApplication.getXmppConnection().getAvatar(jid);

                if (avatarBytes != null) {
                    MainApplication.avatarsCache.put(senderID, avatarBytes);
                } else {
                    avatarBytes = createTextAvatar(Character.toString(Character.toUpperCase(nameChat.charAt(0))));
                }
                emitter.onSuccess(avatarBytes);
            } else {
                emitter.onError(new IllegalArgumentException());
            }
        });
    }

    private byte[] createTextAvatar(String firstLetter) {
        Drawable avatarText = TextDrawable.builder()
                .beginConfig()
                .width(64)
                .height(64)
                .endConfig()
                .buildRound(firstLetter, ColorGenerator.MATERIAL.getColor(firstLetter));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Utils.INSTANCE.drawableToBitmap(avatarText).compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    //must be without @
    public void register(String user, String pass) throws XMPPException, SmackException.NoResponseException, SmackException.NotConnectedException {
        BaseActivity baseActivity = getLoginActivity();
        Log.d("Auth: ", "inside XMPP register method, " + user + " : " + pass);
        long l = System.currentTimeMillis();
        try {
            AccountManager accountManager = AccountManager.getInstance(getConnection());
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(Localpart.from(user), pass);
            if (baseActivity != null) {
                baseActivity.onSuccess();
            }
        } catch (Exception e) {
            if (baseActivity != null) {
                baseActivity.onError(e);
            }
            e.printStackTrace();
        }
        Log.d("Auth", "Time taken to register: " + (System.currentTimeMillis() - l));
    }


    public boolean login(String jid, String pass) {
        String username = jid.split("@")[0];
        XMPPTCPConnection connection = getConnection();

        try {
            if (connection.isAuthenticated()) {
                Log.d("User already logged in");
                return true;
            }

            Log.d("userLog: " + username + " and pass: " + pass);
            SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
            if (!connection.isConnected()) {
                connection.connect();
            }
            connection.login(username, pass);

            mamManager = MamManager.getInstanceFor(this.connection);
            try {
                if (mamManager.isSupported()) {
                    MamManager.getInstanceFor(this.connection).enableMamForAllMessages();
                } else {
                    mamManager = null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.d("LOGIN ERROR" + connection.isAuthenticated());
            e.printStackTrace();
            return false;
        }
        PingManager pingManager = PingManager.getInstanceFor(connection);
        pingManager.setPingInterval(5000);
        Log.d("LOGIN SUCCESSFUL");
        return true;
    }

    public boolean logOut() {
        //there also must be clean base date for our user :/
        try {
            connection.disconnect();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public XMPPTCPConnection getConnection() {
        return connection;
    }

    public byte[] getAvatar(EntityBareJid jid) {
        try {
            if (isConnectionReady()) {
                VCardManager manager = VCardManager.getInstanceFor(connection);
                byte[] avatar = null;
                try {
                    avatar = manager.loadVCard(jid).getAvatar();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return avatar;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private boolean connectAndLogin() {
        if (!credentials.isEmpty()) {
            try {
                connect();
            } catch (XMPPException | SmackException | IOException | EmptyLoginCredentialsException e) {
                e.printStackTrace();
                return false;
            }
            return login(credentials.username, credentials.password);
        }
        return false;
    }

    public Set<RosterEntry> getContactList() {
        if (isConnectionReady()) {
            while (roster == null) ;
            return roster.getEntries();
        }
        return null;
    }

    public boolean isConnectionReady() {
        return connection.isConnected() && connection.isAuthenticated();
    }

    public Presence getUserPresence(BareJid jid) {
        return roster.getPresence(jid);
    }

    public Roster getRoster() {
        return roster;
    }

    public void addUserToGroup(String userName, String groupName) {
        try {
            BareJid bareJid = JidCreate.bareFrom(userName);

            RosterGroup group = roster.getGroup(groupName);
            if (null == group) {
                group = roster.createGroup(groupName);
            }
            RosterEntry entry = roster.getEntry(bareJid);
            if (entry != null) {
                try {
                    group.addEntry(entry);
                } catch (XMPPException e) {

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    if (connectAndLogin()) {
                        addUserToGroup(userName, groupName);
                    }
                }
            }
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    public void sendUserPresence(Presence presence) {
        if (connection != null) {
            if (isConnectionReady()) {
                try {
                    connection.sendStanza(presence);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public MamManager getMamManager() {
        if (isConnectionReady()) {
            return mamManager;
        }
        return null;
    }

    public void addChatStatusListener(String jid) {
        try {
            EntityBareJid groupId = JidCreate.entityBareFrom(jid);
            MultiUserChat muc =
                    MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().getConnection())
                            .getMultiUserChat(groupId);

            muc.addParticipantStatusListener(networkHandler);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    public void joinAllChats() {
        ChatListRepository.INSTANCE.getChats()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chats -> {
                    try {
                        for (int i = 0; i < chats.size(); i++) {
                            if (chats.get(i).isGroupChat()) {
                                joinChat(chats.get(i).getJid());
                            } else {
                                MainApplication.getXmppConnection().chatManager.chatWith(JidCreate.entityBareFrom(chats.get(i).getJid()));
                            }
                        }
                    }catch (Exception e){
                        Logger.d(e);
                    }
                });
    }

    private void joinChat(String jid) {
        try {
            MultiUserChatManager manager =
                    MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection);
            EntityBareJid entityBareJid = JidCreate.entityBareFrom(jid);
            MultiUserChat muc = manager.getMultiUserChat(entityBareJid);

            VCardManager vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection);
            VCard card = vm.loadVCard();
            Resourcepart nickName = Resourcepart.from(card.getNickName());

            if (!muc.isJoined()) {
                muc.join(nickName);
            }
        } catch (Exception e) {
            Logger.d(e.getMessage());
        }
    }
}
