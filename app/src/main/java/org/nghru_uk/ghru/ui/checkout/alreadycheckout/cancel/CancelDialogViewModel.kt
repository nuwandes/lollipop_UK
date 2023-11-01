package org.nghru_uk.ghru.ui.checkout.alreadycheckout.cancel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.CancelRequestRepository
import org.nghru_uk.ghru.repository.UserRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.MessageCancel
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.User
import org.nghru_uk.ghru.vo.request.CancelRequest
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class CancelDialogViewModel @Inject constructor(cancelRequestRepository: CancelRequestRepository,
                                                userRepository: UserRepository
) : ViewModel() {

    private val _cancelId: MutableLiveData<CancelId> = MutableLiveData()


    var cancelId: LiveData<Resource<MessageCancel>>? = Transformations
        .switchMap(_cancelId) { input ->
            input.ifExists { participantRequest, cancelRequest ->
                cancelRequestRepository.syncCancelRequest(participantRequest, cancelRequest)
            }
        }

    fun setLogin(participantRequest: ParticipantRequest?, cancelRequest: CancelRequest?) {
        val update = CancelId(participantRequest, cancelRequest)
        if (_cancelId.value == update) {
            return
        }
        _cancelId.value = update
    }

    data class CancelId(val participantRequest: ParticipantRequest?, val cancelRequest: CancelRequest?) {
        fun <T> ifExists(f: (ParticipantRequest, CancelRequest) -> LiveData<T>): LiveData<T> {
            return if (participantRequest == null || cancelRequest == null) {
                AbsentLiveData.create()
            } else {
                f(participantRequest, cancelRequest)
            }
        }
    }

//    ---------------------- set and get user ------------------------------------------------------------

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