package org.nghru_uk.ghru.vo

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import org.nghru_uk.ghru.BR
import java.io.Serializable


class Triglycerides : BaseObservable(), Serializable {

    companion object {
        fun build(): Triglycerides {
            val hb1Ac = Triglycerides()
            hb1Ac.probeId = String()
            hb1Ac.value = String()
            hb1Ac.comment = String()
            hb1Ac.deviceId = String()
            return hb1Ac
        }
    }


    var value: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.value)
        }
        @Bindable get() = field


    var probeId: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.probeId)
        }
        @Bindable get() = field


    var comment: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.comment)
        }
        @Bindable get() = field


    var deviceId: String = String()
        set(value) {
            field = value
        }
        @Bindable get() = field
}
