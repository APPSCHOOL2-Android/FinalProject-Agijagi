package likelion.project.agijagi.search.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import likelion.project.agijagi.search.repository.SearchRepository

class SearchViewModel : ViewModel() {
    private val repository = SearchRepository()
    var productList = MutableLiveData<List<String>>()

    fun getProductNameAndBrand() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = repository.getProductNameAndBrand()
                productList.postValue(data)
                Log.d("hyevm", data.toString())
            } catch (e: Exception) {
                Log.d("err", e.toString())
            }
        }
    }

}