package io.moonshard.moonshard.helpers;

import org.jivesoftware.smack.packet.IQ;

public class IQCustomAuth extends IQ {
    public final static String childElementName = "query";
    public final static String childElementNamespace = "com:prethia:query#auth";


    IQ.Type type;



    public IQCustomAuth(String userFrom, String server, IQ.Type type) {
        super(childElementName, childElementNamespace);

    }


    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return xml;
    }
}
