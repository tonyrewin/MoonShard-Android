package io.moonshard.moonshard.services;

import android.content.Context;
import android.util.Log;

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
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import io.moonshard.moonshard.EmptyLoginCredentialsException;
import io.moonshard.moonshard.LoginCredentials;
import io.moonshard.moonshard.MainApplication;
import io.moonshard.moonshard.helpers.NetworkHandler;
import io.moonshard.moonshard.ui.activities.BaseActivity;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static io.moonshard.moonshard.MainApplication.getCurrentActivity;

public class XMPPConnection implements ConnectionListener {
    private final static String LOG_TAG = "XMPPConnection";
    private LoginCredentials credentials = new LoginCredentials();
    private XMPPTCPConnection connection = null;
    private NetworkHandler networkHandler;
    private Context context;
    private Roster roster;
    private MamManager mamManager;
    public MultiUserChatManager multiUserChatManager = null;

    public enum ConnectionState {
        CONNECTED,
        DISCONNECTED
    }

    public enum SessionState {
        LOGGED_IN,
        LOGGED_OUT
    }

    public XMPPConnection(Context context) {
        this.context = context;
        String jid = SecurePreferences.getStringValue("jid", null);
        String password = SecurePreferences.getStringValue("pass", null);

        if (jid != null && password != null) {
            String username = jid.split("@")[0];
            String jabberHost = jid.split("@")[1];
            credentials.username = username;
            credentials.jabberHost = jabberHost;
            credentials.password = password;
        }
        networkHandler = new NetworkHandler();
    }

    public void connect() throws XMPPException, IOException, SmackException, EmptyLoginCredentialsException {

        if (credentials.isEmpty()) {
            throw new EmptyLoginCredentialsException();
        }

        if (connection == null) {
            XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(credentials.jabberHost)
                    .setHost(credentials.jabberHost)
                    .setResource(MainApplication.APP_NAME)
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
                BaseActivity baseActivity = getCurrentActivity();
                if (baseActivity != null) {
                    baseActivity.onError(e);
                }
                e.printStackTrace();
            }

            ChatManager.getInstanceFor(connection).addIncomingListener(networkHandler);
            ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
            ReconnectionManager.setEnabledPerDefault(true);
            reconnectionManager.enableAutomaticReconnection();
            roster = Roster.getInstanceFor(connection);
            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            roster.addPresenceEventListener(networkHandler);
            MainApplication.setJid(credentials.username + "@" + credentials.jabberHost);
            FileTransferNegotiator.IBB_ONLY = true;

            if (MainApplication.isIsMainActivityDestroyed()) {
                sendUserPresence(new Presence(Presence.Type.unavailable));
            }
        }
        multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
    }

    public void disconnect() {
        SecurePreferences.setValue("logged_in", false);
        if (connection != null) {
            connection.disconnect();
            connection = null;
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

        BaseActivity baseActivity = getCurrentActivity();
        if (baseActivity != null) {
            baseActivity.onAuthenticated();
        }
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
        Chat chat = ChatManager.getInstanceFor(connection).chatWith(recipientJid);
        try {
            Message message = new Message(recipientJid, Message.Type.chat);
            message.setBody(messageText);
            chat.send(message);
            return message.getStanzaId();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
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
        MultiUserChat muc = null;
        try {
            EntityBareJid entityBareJid = JidCreate.entityBareFrom(recipientJid);
            muc = multiUserChatManager.getMultiUserChat(entityBareJid);

            Message message = new Message(recipientJid, Message.Type.groupchat);
            message.setBody(messageText);
            try {
                muc.sendMessage(messageText);
                return message.getStanzaId();
            } catch (SmackException.NotConnectedException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    //must be without @
    public void register(String user, String pass) throws XMPPException, SmackException.NoResponseException, SmackException.NotConnectedException {
        BaseActivity baseActivity = getCurrentActivity();
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


    public boolean login(String email, String pass)
            throws XMPPException, SmackException, IOException, InterruptedException {

        String username = email.split("@")[0];

        XMPPTCPConnection connect = getConnection();

        try {
            if (connect.isAuthenticated()) {
                Logger.d("User already logged in");
                return true;
            }

            Logger.d("userLog: " + email + " and pass: " + pass);
            SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
            connect.login(username, pass);

            mamManager = MamManager.getInstanceFor(connection);
            try {
                if (mamManager.isSupported()) {
                    MamManager.getInstanceFor(connection).enableMamForAllMessages();
                } else {
                    mamManager = null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Logger.d("LOGIN ERROR" + connect.isAuthenticated());
            e.printStackTrace();
            return false;
        }
        PingManager pingManager = PingManager.getInstanceFor(connect);
        pingManager.setPingInterval(5000);
        Logger.d("LOGIN JUST");
        return true;
    }

    public boolean logOut() {
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
        if (isConnectionAlive()) {
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
        return null;
    }

    public Set<RosterEntry> getContactList() {
        if (isConnectionAlive()) {
            while (roster == null) ;
            return roster.getEntries();
        }
        return null;
    }

    public boolean isConnectionAlive() {
        return XMPPConnectionService.CONNECTION_STATE.equals(ConnectionState.CONNECTED) && XMPPConnectionService.SESSION_STATE.equals(SessionState.LOGGED_IN);
    }

    public Presence getUserPresence(BareJid jid) {
        return roster.getPresence(jid);
    }

    public void sendUserPresence(Presence presence) {
        if (connection != null) {
            if (isConnectionAlive()) {
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
        if (isConnectionAlive()) {
            return mamManager;
        }
        return null;
    }
}
