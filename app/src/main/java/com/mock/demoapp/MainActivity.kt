package com.mock.demoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mock.demoapp.ui.theme.DemoAppTheme
import com.mock.mockpaymentsdk.PaymentSDK
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoAppTheme {
                PaymentScreen()
            }
        }
    }
}

@Composable
fun PaymentScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var amount by remember { mutableStateOf(TextFieldValue("1234")) }
    var currency by remember { mutableStateOf(TextFieldValue("USD")) }
    var recipient by remember { mutableStateOf(TextFieldValue("1234")) }
    var paymentResult by remember { mutableStateOf<String?>(null) }
    val sdk by remember { mutableStateOf(PaymentSDK.Builder().setApiKey("mock-api-key").build()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Payment SDK Demo", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = currency,
            onValueChange = { currency = it },
            label = { Text("Currency") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = recipient,
            onValueChange = { recipient = it },
            label = { Text("Recipient") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val result = sdk.makePayment(
                            amount = amount.text.toIntOrNull() ?: 0,
                            currency = currency.text,
                            recipient = recipient.text
                        )
                        result.fold(
                            onSuccess = { response ->
                                paymentResult = "Payment Successful: ${response.transactionId}"
                            },
                            onFailure = { error ->
                                paymentResult = when (error) {
                                    is com.mock.mockpaymentsdk.errors.PaymentRequestException -> {
                                        "Invalid Payment Request: ${error.localizedMessage}"
                                    }

                                    else -> {
                                        "Payment Failed: ${error.localizedMessage}"
                                    }
                                }
                            }
                        )
                    } catch (e: Exception) {
                        paymentResult = "Unexpected Error: ${e.localizedMessage}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Make Payment")
        }
        Spacer(modifier = Modifier.height(16.dp))
        paymentResult?.let {
            Text(
                text = it,
                color = if (it.contains("Successful")) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
