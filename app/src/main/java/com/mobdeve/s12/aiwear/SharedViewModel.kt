import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobdeve.s12.aiwear.ClothesItem

class SharedViewModel : ViewModel() {

    private val _clothesUpdateEvent = MutableLiveData<Unit>()
    val clothesUpdateEvent: LiveData<Unit> = _clothesUpdateEvent
    private val _refreshListEvent = MutableLiveData<Unit>()
    val refreshListEvent: LiveData<Unit> get() = _refreshListEvent

    fun notifyListUpdate() {
        _refreshListEvent.value = Unit
    }

    fun notifyClothesChanged() {
        _clothesUpdateEvent.value = Unit
    }

}
