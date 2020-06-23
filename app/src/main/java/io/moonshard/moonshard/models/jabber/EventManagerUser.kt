package io.moonshard.moonshard.models.jabber

import org.jivesoftware.smackx.muc.MUCAffiliation

data class EventManagerUser(val roleType:MUCAffiliation,val jid:String,val nickName:String)