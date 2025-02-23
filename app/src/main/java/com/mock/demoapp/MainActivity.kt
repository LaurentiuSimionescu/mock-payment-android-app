package com.mock.demoapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mock.demoapp.ui.theme.DemoAppTheme
import com.mock.mockpaymentsdk.PaymentSDK
import com.mock.mockpaymentsdk.models.PaymentResponse
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

    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var currency by remember { mutableStateOf(TextFieldValue("USD")) }
    var recipient by remember { mutableStateOf(TextFieldValue("")) }

    var paymentResult by remember { mutableStateOf<String?>(null) }

    val sdk = PaymentSDK.Builder().setApiKey("mock-api-key").build()

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
                                paymentResult = "Payment Failed: ${error.localizedMessage}"
                            }
                        )
                    } catch (e: Exception) {
                        paymentResult = "SDK Error: ${e.message}"
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