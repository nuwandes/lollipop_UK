package org.nghru_uk.ghru.ui.questionnaire.confirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.QuestionnaireRepository
import org.nghru_uk.ghru.repository.UserRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.Message
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.ResourceData
import org.nghru_uk.ghru.vo.User
import org.nghru_uk.ghru.vo.request.HLQResponse
import javax.inject.Inject


class QuestionnaireConfirmationViewModel
@Inject constructor(questionnaireRepository: QuestionnaireRepository,
                    userRepository: UserRepository ) : ViewModel() {

    private val _hlqUpdateRequest: MutableLiveData<HLQResponse> = MutableLiveData()
    private var _participantId: String? = null

    fun setUpdateHLQ(hlqRequest: HLQResponse, participantId: String?) {
        _participantId = participantId
        if (_hlqUpdateRequest.value == hlqRequest) {
            return
        }
        _hlqUpdateRequest.value = hlqRequest
    }

    var hlqUpdateComplete:LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_hlqUpdateRequest) { hlqRequest ->
            if (hlqRequest == null) {
                AbsentLiveData.create()
            } else {
                questionnaireRepository.updateHLQ(hlqRequest,_participantId!!)
            }
        }

    val haveStaff = MutableLiveData<Boolean>()

    fun setHaveStaff(item: Boolean) {
        haveStaff.value = item
    }

    val haveAssistance = MutableLiveData<Boolean>()

    fun setHaveAssistance(item: Boolean) {
        haveAssistance.value = item
    }

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
