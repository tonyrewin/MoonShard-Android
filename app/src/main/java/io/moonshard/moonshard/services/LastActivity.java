package io.moonshard.moonshard.services;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

public class LastActivity extends IQ {

    public static final String ELEMENT = "unique";
    public static final String NAMESPACE = "muc#unique";

    public long lastActivity = -1;
    public String message;

    public LastActivity() {
        super(ELEMENT, NAMESPACE);
        setType(IQ.Type.get);
    }

    public LastActivity(Jid to) {
        this();
        setTo(to);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.optLongAttribute("seconds", lastActivity);

        // We don't support adding the optional message attribute, because it is usually only added
        // by XMPP servers and not by client entities.
        xml.setEmptyElement();
        return xml;
    }


    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }


    private void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns number of seconds that have passed since the user last logged out.
     * If the user is offline, 0 will be returned.
     *
     * @return the number of seconds that have passed since the user last logged out.
     */
    public long getIdleTime() {
        return lastActivity;
    }

    /**
     * Returns the status message of the last unavailable presence received from the user.
     *
     * @return the status message of the last unavailable presence received from the user
     */
    public String getStatusMessage() {
        return message;
    }


    /**
     * The IQ Provider for LastActivity.
     *
     * @author Derek DeMoro
     */
    public static class Provider extends IQProvider<LastActivity> {

        @Override
        public LastActivity parse(XmlPullParser parser, int initialDepth) throws Exception {
            LastActivity lastActivity = new LastActivity();
            String seconds = parser.getAttributeValue("", "seconds");
            if (seconds != null) {
                try {
                    lastActivity.setLastActivity(Long.parseLong(seconds));
                } catch (NumberFormatException e) {
                    throw new IOException("Could not parse last activity number", e);
                }
            }
            lastActivity.setMessage(parser.nextText());
            return lastActivity;
        }
    }
}
