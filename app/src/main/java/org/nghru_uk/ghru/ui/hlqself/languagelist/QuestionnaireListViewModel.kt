package org.nghru_uk.ghru.ui.hlqself.languagelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.QuestionnaireSelfRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.QuestionnaireSelf
import org.nghru_uk.ghru.vo.Resource
import javax.inject.Inject


class QuestionnaireListViewModel
@Inject constructor(questionnaireRepository: QuestionnaireSelfRepository) : ViewModel() {
    private val _language = MutableLiveData<QuestionnaireId>()

    val language: LiveData<Resource<List<QuestionnaireSelf>>>? = Transformations
        .switchMap(_language) { input ->
            input.ifExists { language, network ->
                questionnaireRepository.getQuestionnaireSelfList(language = language, network = network)
            }
        }


    fun getQuestionnaire(
        language: String?,
        network: Boolean?
    ) {
        val update =
            QuestionnaireId(language, network)
        if (_language.value == update) {
            return
        }
        _language.value = update
    }


    data class QuestionnaireId(
        val language: String?,
        val network: Boolean?
    ) {
        fun <T> ifExists(f: (String, Boolean) -> LiveData<T>): LiveData<T> {
            return if (language == null || network == null) {
                AbsentLiveData.create()
            } else {
                f(language, network)
            }
        }
    }

    fun retry() {
        _language.value?.let {
            _language.value = it
        }
    }
}
