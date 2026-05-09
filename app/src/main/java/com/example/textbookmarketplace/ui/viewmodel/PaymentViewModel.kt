package com.example.textbookmarketplace.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textbookmarketplace.domain.model.PaymentStatus
import com.example.textbookmarketplace.domain.model.PaymentTransaction
import com.example.textbookmarketplace.domain.model.UiState
import com.example.textbookmarketplace.utils.PaymentSimulator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentSimulator: PaymentSimulator
) : ViewModel() {

    private val _paymentState = MutableStateFlow<UiState<PaymentTransaction>>(UiState.Empty)
    val paymentState: StateFlow<UiState<PaymentTransaction>> = _paymentState.asStateFlow()

    fun processPayment(phoneNumber: String, amount: Double, bookTitle: String) {
        viewModelScope.launch {
            _paymentState.value = UiState.Loading
            val result = paymentSimulator.processPayment(phoneNumber, amount, bookTitle)
            _paymentState.value = UiState.Success(result)
        }
    }

    fun resetState() {
        _paymentState.value = UiState.Empty
    }
}