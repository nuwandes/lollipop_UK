package org.nghru_uk.ghru.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.HomeRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.HomeItem
import org.nghru_uk.ghru.vo.Resource
import javax.inject.Inject


class AllStationsViewModel
@Inject constructor(repository: HomeRepository) : ViewModel() {
    private val _home = MutableLiveData<String>()
    val home: LiveData<String>
        get() = _home

    val homeItem: LiveData<Resource<List<HomeItem>>> = Transformations
        .switchMap(_home) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repository.getDashboardItems()
            }
        }

    fun setId(lang: String?) {
        if (_home.value != lang) {
            _home.value = lang
        }
    }

}
