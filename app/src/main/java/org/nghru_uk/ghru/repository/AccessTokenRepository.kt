package org.nghru_uk.ghru.repository

import androidx.lifecycle.LiveData
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.api.ApiResponse
import org.nghru_uk.ghru.api.NghruService
import org.nghru_uk.ghru.db.AccessTokenDao
import org.nghru_uk.ghru.util.RateLimiter
import org.nghru_uk.ghru.util.TokenManager
import org.nghru_uk.ghru.vo.AccessToken
import org.nghru_uk.ghru.vo.RefreshToken
import org.nghru_uk.ghru.vo.Resource
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles User objects.
 */

@Singleton
class AccessTokenRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val accessTokenDao: AccessTokenDao,
    private val nghruService: NghruService,
    private val tokenManager: TokenManager

) {

    private val repoListRateLimit = RateLimiter<AccessToken>(1, TimeUnit.SECONDS)

    fun loginUser(
        email: String,
        password: String,
        isOnline : Boolean
    ): LiveData<Resource<AccessToken>> {
        return object : NetworkBoundResource<AccessToken, AccessToken>(appExecutors) {
            override fun saveCallResult(item: AccessToken) {
                tokenManager.saveToken(item);
                item.userName = email
                item.passwordEN = password
                accessTokenDao.insert(item);
            }

            override fun shouldFetch(data: AccessToken?) = isOnline

            override fun loadFromDb(): LiveData<AccessToken> {
                return accessTokenDao.getTokerByEmailPasword(email, password)
            }

            override fun createCall(): LiveData<ApiResponse<AccessToken>> {
                val params = HashMap<String, String>()
                params["username"] = email
                params["password"] = password
                return nghruService.getAccessToken(params)
            }
        }.asLiveData()
    }

    fun deleteToken(): Int {
        return accessTokenDao.nukeTable()
    }


    fun refreshToken(
        accessToken: AccessToken
    ): LiveData<Resource<AccessToken>> {
        return object : NetworkOnlyBoundResource<AccessToken>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<AccessToken>> {
                return nghruService.getRefresh(
                    RefreshToken(
                        refresh_token = accessToken.refreshToken!!
                    )
                )
            }
        }.asLiveData()
    }

    fun getTokerByEmail(
        email: String
    ): LiveData<Resource<AccessToken>> {
        return object : LocalBoundResource<AccessToken>(appExecutors) {
            override fun loadFromDb(): LiveData<AccessToken> {
                return accessTokenDao.getTokerByEmail(email)
            }
        }.asLiveData()
    }

    fun setLogout(accessToken: AccessToken
    ): LiveData<Resource<AccessToken>> {
        return object : LocalBoundUpateResource<AccessToken, Int>(appExecutors) {
            override fun loadFromDb(rowId: Int): LiveData<AccessToken> {
                return accessTokenDao.getTokerByEmail(accessToken.userName)
            }

            override fun updateDb(): Int {
                return accessTokenDao.logout(accessToken)

            }

        }.asLiveData()
    }


}
