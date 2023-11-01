package org.nghru_uk.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.api.ApiResponse
import org.nghru_uk.ghru.api.NghruService
import org.nghru_uk.ghru.db.QuestionnaireDao
import org.nghru_uk.ghru.db.QuestionnaireSelfDao
import org.nghru_uk.ghru.vo.*
import org.nghru_uk.ghru.vo.request.HLQResponse
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class QuestionnaireSelfRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val questionnaireDao: QuestionnaireSelfDao,
    private val nghruService: NghruService
) : Serializable {
//    fun getQuestionnaire(
//        network: Boolean,
//        language :String
//    ): LiveData<Resource<Questionnaire>> {
//
//        return object : NetworkBoundResource<Questionnaire, ResourceData<Questionnaire>>(appExecutors) {
//            override fun saveCallResult(item: ResourceData<Questionnaire>) {
//               // item.data?.json?.replace("\r", "")?.replace("\n","")?.replace("\\" , "")
//                questionnaireDao.insert(item.data!!)
//            }
//
//            override fun shouldFetch(data: Questionnaire?): Boolean = network
//
//            override fun loadFromDb(): LiveData<Questionnaire> {
//                return questionnaireDao.getQuestionnaire()
//            }
//
//            override fun createCall(): LiveData<ApiResponse<ResourceData<Questionnaire>>> {
//                return nghruService.getQuestionnaire(language)
//            }
//        }.asLiveData()
//    }


    fun getQuestionnaireSelfList(
        network: Boolean,
        language :String
    ): LiveData<Resource<List<QuestionnaireSelf>>> {

        return object : NetworkBoundResource<List<QuestionnaireSelf>, ResourceData<List<QuestionnaireSelf>>>(appExecutors) {
            override fun saveCallResult(item: ResourceData<List<QuestionnaireSelf>>) {
                // item.data?.json?.replace("\r", "")?.replace("\n","")?.replace("\\" , "")
                if(network){
                    questionnaireDao.nukeTable()
                    questionnaireDao.insert(item.data!!)
                }else{
                    questionnaireDao.insert(item.data!!)
                }

            }

            override fun shouldFetch(data: List<QuestionnaireSelf>?): Boolean = network

            override fun loadFromDb(): LiveData<List<QuestionnaireSelf>> {
                return questionnaireDao.getQuestionnaires()
            }

            override fun createCall(): LiveData<ApiResponse<ResourceData<List<QuestionnaireSelf>>>> {
                return nghruService.getQuestionnaireSelf()
            }
        }.asLiveData()
    }

    fun updateHLQSelf(
        hlqRequest: HLQResponse,
        screening_id: String
    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.updateHLQSelf(hlqRequest,screening_id)
            }
        }.asLiveData()
    }
}
