package com.example.app_combugas_nueva.ui.inventario;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InventarioViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<String> mText;

    public InventarioViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Este es el inventario");
    }

    public LiveData<String> getText() {
        return mText;
    }
}