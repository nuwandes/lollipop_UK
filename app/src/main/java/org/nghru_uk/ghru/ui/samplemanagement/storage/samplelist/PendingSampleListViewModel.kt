package org.nghru_uk.ghru.ui.samplemanagement.storage.samplelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.SampleRequestRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.request.SampleRequest
import javax.inject.Inject


class PendingSampleListViewModel
@Inject constructor(sampleRequestRepository: SampleRequestRepository) : ViewModel() {
    private val _pendingSampleLis = MutableLiveData<String>()

//    val pendingSampleList: LiveData<Resource<List<SampleRequest>>>? = Transformations
//            .switchMap(_pendingSampleLis) { pendingSampleLis ->
//                if (pendingSampleLis == null) {
//                    AbsentLiveData.create()
//                } else {
//                    sampleRequestRepository.getSampleRequests();
//                }
//            }

    val pendingSampleListOnline: LiveData<Resource<List<SampleRequest>>>? = Transformations
            .switchMap(_pendingSampleLis) { pendingSampleLis ->
                if (pendingSampleLis == null) {
                    AbsentLiveData.create()
                } else {
                    sampleRequestRepository.getSampleStoragePendingOnline()
                }
            }

    fun setId(lang: String?) {
        if (_pendingSampleLis.value != lang) {
            _pendingSampleLis.value = lang
        }
    }

    fun retry() {
        _pendingSampleLis.value?.let {
            _pendingSampleLis.value = it
        }
    }
}
