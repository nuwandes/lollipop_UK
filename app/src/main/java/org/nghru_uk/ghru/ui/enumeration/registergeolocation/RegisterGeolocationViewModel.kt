package org.nghru_uk.ghru.ui.enumeration.registergeolocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.nghru_uk.ghru.repository.UserRepository
import org.nghru_uk.ghru.util.AbsentLiveData
import org.nghru_uk.ghru.vo.Resource
import org.nghru_uk.ghru.vo.User
import javax.inject.Inject


class RegisterGeolocationViewModel
@Inject constructor(userRepository: UserRepository) : ViewModel() {
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
