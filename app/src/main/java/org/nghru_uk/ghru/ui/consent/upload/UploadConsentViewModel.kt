package org.nghru_uk.ghru.ui.consent.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.AssertRepository
import org.nghru_uk.ghru.ui.registerpatient.confirmation.ConfirmationViewModel
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.Message
import org.nghru_uk.ghru.vo.Resource
import javax.inject.Inject


class UploadConsentViewModel
@Inject constructor(
    assertRepository: AssertRepository
    ) : ViewModel() {

    private val _uploadConcent: MutableLiveData<UploadConcentId> = MutableLiveData()

    var uploadConcent: LiveData<Resource<Message>>? = Transformations
        .switchMap(_uploadConcent) { upload ->
            upload.ifExists { concentPhoto, screeningId ->
                assertRepository.uploadConcent(concentPhoto, screeningId)
            }
        }

    fun setUploadConcent(concentPhoto: String?, screeningId: String?) {
        val update = UploadConcentId(concentPhoto, screeningId)
        if (_uploadConcent.value == update) {
            return
        }
        _uploadConcent.value = update
    }

    data class UploadConcentId(val concentPhoto: String?, val screeningId: String?) {
        fun <T> ifExists(f: (String, String) -> LiveData<T>): LiveData<T> {
            return if (concentPhoto == null || screeningId == null) {
                AbsentLiveData.create()
            } else {
                f(concentPhoto, screeningId)
            }
        }
    }

    private val _uploadConsentNew: MutableLiveData<UploadConsentIdNew> = MutableLiveData()

    data class UploadConsentIdNew(val concentPhoto: String?, val screeningId: String?) {
        fun <T> ifExists(f: (String, String) -> LiveData<T>): LiveData<T> {
            return if (concentPhoto == null || screeningId == null) {
                AbsentLiveData.create()
            } else {
                f(concentPhoto, screeningId)
            }
        }
    }

    fun setUploadConsentNew(concentPhoto: String?, screeningId: String?) {
        val update = UploadConsentIdNew(concentPhoto, screeningId)
        if (_uploadConsentNew.value == update) {
            return
        }
        _uploadConsentNew.value = update
    }

    var uploadConsentNew: LiveData<Resource<Message>>? = Transformations
        .switchMap(_uploadConsentNew) { upload ->
            upload.ifExists { concentPhoto, screeningId ->
                assertRepository.uploadConcent(concentPhoto, screeningId)
            }
        }
}
