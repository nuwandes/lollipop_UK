package org.nghru_uk.ghru.ui.checkout.bankdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.CheckoutRepository
import org.nghru_uk.ghru.repository.UserRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.Message
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.ResourceData
import org.nghru_uk.ghru.vo.User
import org.nghru_uk.ghru.vo.request.CheckoutRequest
import javax.inject.Inject

class BankDetailsViewModel
@Inject constructor(checkoutRepository: CheckoutRepository,
                    userRepository: UserRepository) : ViewModel() {

    // ----------------------------- post Checkout-----------------------------------

    private val _chkPostRequest: MutableLiveData<CheckoutRequest> = MutableLiveData()
    private var _participantId: String? = null

    fun setPostChk(cogRequest: CheckoutRequest, participantId: String?) {
        _participantId = participantId
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
                checkoutRepository.syncChk(cogPostRequest,_participantId!!)
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
}