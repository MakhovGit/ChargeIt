package chargeit.profilescreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import chargeit.core.viewmodel.CoreViewModel
import chargeit.data.domain.model.State
import chargeit.data.interactor.CarInteractor
import chargeit.data.repository.LocalUserRepo
import chargeit.profilescreen.data.mapper.UserMapper
import chargeit.profilescreen.data.model.UserUiModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class ProfileRegistrationViewModel(
    private val userRepo: LocalUserRepo,
    private val carInteractor: CarInteractor,
    private val userMapper: UserMapper
) : CoreViewModel() {
    private val _carBrandsLiveData = MutableLiveData<List<String>>()
    val carBrandsLiveData: LiveData<List<String>> by this::_carBrandsLiveData

    private val _carModelLiveData = MutableLiveData<List<String>>()
    val carModelLiveData: LiveData<List<String>> by this::_carModelLiveData

    private val _registrationLiveData = MutableLiveData<State>()
    val registrationLiveData: LiveData<State> by this::_registrationLiveData


    fun loadCarBrands() {
        carInteractor.getAllCarBrands().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _carBrandsLiveData.value = it }
            .addViewLifeCycle()
    }

    fun loadCarModels(carBrand: String) {
        carInteractor.getAllModelsByBrand(carBrand).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _carModelLiveData.value = it }
            .addViewLifeCycle()
    }

    fun saveUser(uiModel: UserUiModel) {
        userRepo.saveUserEntity(userMapper.map(uiModel))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { _registrationLiveData.value = State.Success },
                { _registrationLiveData.value = State.Error })

    }
}