package io.moonshard.moonshard.presentation.presenter

import android.util.Log
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.MapMainView
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.roster.RosterGroup
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smack.roster.Roster




@InjectViewState
class MapPresenter : MvpPresenter<MapMainView>() {

    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }

    fun getRooms(lat: String, lng: String, radius: String) {
        //this hard data - center Moscow
        compositeDisposable.add(useCase!!.getRooms("55.751244", "37.618423", 10000.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { rooms, throwable ->
                if (throwable == null) {
                    RoomsMap.clean()
                    RoomsMap.rooms = rooms
                    Log.d("rooms", rooms.size.toString())
                    viewState?.showRoomsOnMap(rooms)
                } else {
                    throwable.message?.let { viewState?.showError(it) }
                }
            })
    }

    fun getRoom(jid: String): MultiUserChat {
        val groupId = JidCreate.entityBareFrom(jid)
        val muc = MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            .getMultiUserChat(groupId)
        val info = MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection).getRoomInfo(muc.room)


        val roster = MainApplication.getXmppConnection().roster
        val entries = roster.entries
        for (entry in entries) {
            println(entry)
        }

        /*
        val group: RosterGroup? = MainApplication.getXmppConnection().roster.getGroup(jid)

        val map = HashMap<String, String>()
        for(i in group?.entries?.indices!!){
            map["USER"] = group.entries[i].name
            map["STATUS"] = group.entries[i].type.toString()
        }

         */
        val allUser = info.occupantsCount
        return muc
    }
/*
    fun test(){
        val usersList = ArrayList<HashMap<String, String>>()


      //  val presence = Presence(Presence.Type.available)
      //  Constants.connection.sendPacket(presence)
      //  setConnection(Constants.connection)

      //  val roster = Constants.connection.getRoster()
      //  val entries = roster.getEntries()

        var roster = MainApplication.getXmppConnection().roster

        val entries = roster.entries


        for (entry in entries) {

            val map = HashMap<String, String>()
            val entryPresence = roster.getPresence(entry.getUser())

            val type = entryPresence.getType()

            map["USER"] = entry.getName().toString()
            map["STATUS"] = type.toString()
            Log.e("USER", entry.getName().toString())

            usersList.add(map)

        }
            }
             */


}