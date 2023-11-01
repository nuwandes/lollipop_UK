package org.nghru_uk.ghru.ui.checkout.selectedparticipant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.CheckoutRepository
import org.nghru_uk.ghru.repository.HomeRepository
import org.nghru_uk.ghru.repository.ParticipantRepository
import org.nghru_uk.ghru.repository.UserRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.*
import org.nghru_uk.ghru.vo.request.CheckoutRequest
import org.nghru_uk.ghru.vo.request.CheckoutRequestUK
import javax.inject.Inject


class SelectedParticipantViewModel
@Inject constructor(repository: HomeRepository,
                    participantRepository: ParticipantRepository,
                    userRepository: UserRepository,
                    checkoutRepository: CheckoutRepository) : ViewModel() {
    private val _home = MutableLiveData<String>()
    val home: LiveData<String>
        get() = _home

    val homeItem: LiveData<Resource<List<ParticipantStationsItem>>> = Transformations
        .switchMap(_home) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repository.getAllParticipantData()
            }
        }

    fun setId(lang: String?) {
        if (_home.value != lang) {
            _home.value = lang
        }
    }

    //    Get single participant stations ------------------------------------------------------------------------

    private val _participantId: MutableLiveData<ParticipantId> = MutableLiveData()

    var getSingleParticipantStations: LiveData<Resource<ResourceData<StationData<List<ParticipantStation>>>>>? = Transformations
        .switchMap(_participantId) { input ->
            input.ifExists { participant ->
                participantRepository.getSingleParticipantStations(participant!!.toString())

            }
        }

    fun setParticipantId(participant: String) {
        val update =
            ParticipantId(participant = participant)
        if (_participantId.value == update) {
            return
        }
        _participantId.value = update
    }

    data class ParticipantId(val participant: String?) {

        fun <T> ifExists(f: (String?) -> LiveData<T>): LiveData<T> {
            return if (participant == null) {
                AbsentLiveData.create()
            } else {
                f(participant)
            }
        }
    }


//    --------------------------------------------------------------------------------------------------------

//    Post checkout ------------------------------------------------------------------------------------------

    private val _chkPostRequest: MutableLiveData<CheckoutRequestUK> = MutableLiveData()
    private var _participant: String? = null

    fun setPostChk(cogRequest: CheckoutRequestUK, participantId: String?) {
        _participant = participantId
        if (_chkPostRequest.value == cogRequest) {
            return
        }
        _chkPostRequest.value = cogRequest
    }

    var chkPostComplete:LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_chkPostRequest) { cogPostRequest ->
            if (cogPostRequest == null) {
                AbsentLiveData.create()
            } else {
                checkoutRepository.syncChkUK(cogPostRequest,_participant!!)
            }
        }

    // ----------------------------------------------------------------------------

    private val _email = MutableLiveData<String>()

    val user: LiveData<Resource<User>>? = Transformations
        .switchMap(_email) { emailx ->
            if (emailx == null) {
                AbsentLiveData.create()
            } else {
                userRepository.loadUserDB()
            }
        }
    fun setUser(email: String?) {
        if (_email.value != email) {
            _email.value = email
        }
    }

//    --------------------------------------------------------------------------------------------------------


}
