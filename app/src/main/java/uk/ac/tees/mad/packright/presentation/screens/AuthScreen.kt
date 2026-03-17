package uk.ac.tees.mad.packright.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.ac.tees.mad.packright.domain.Supabase.Repo.AuthRepository
import uk.ac.tees.mad.packright.model.UserData
import uk.ac.tees.mad.packright.presentation.ViewModel.AppViewModel
import uk.ac.tees.mad.packright.presentation.ViewModel.AuthViewModelFactory
import uk.ac.tees.mad.packright.ui.theme.BackgroundLight
import uk.ac.tees.mad.packright.ui.theme.PackRightTheme
import uk.ac.tees.mad.packright.ui.theme.PrimaryBlue

@Composable
fun AuthScreen(
    appViewModel: AppViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository())
    ),
    onAuthSuccess: () -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }

    val loginState by appViewModel.loginScreenState
    val signupState by appViewModel.signupScreenState

    // Handle success states
    if (loginState.success || signupState.success) {
        onAuthSuccess()
    }

    AuthContent(
        isLoginMode = isLoginMode,
        onModeChange = { isLoginMode = it },
        onLogin = { email, password ->
            appViewModel.loginUser(UserData(email = email, password = password))
        },
        onRegister = { name, email, password ->
            appViewModel.registerUser(UserData(email = email, password = password))
        },
        isLoading = loginState.isLoading || signupState.isLoading,
        error = if (isLoginMode) loginState.error else signupState.error
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthContent(
    isLoginMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String) -> Unit,
    isLoading: Boolean,
    error: String?
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Blue Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    color = PrimaryBlue,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PackRight",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Toggle Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isLoginMode) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { onModeChange(true) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign In",
                            color = if (isLoginMode) PrimaryBlue else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isLoginMode) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isLoginMode) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { onModeChange(false) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Register",
                            color = if (!isLoginMode) PrimaryBlue else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (!isLoginMode) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (!isLoginMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            focusedBorderColor = PrimaryBlue
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        focusedBorderColor = PrimaryBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        focusedBorderColor = PrimaryBlue
                    )
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (isLoginMode) {
                            onLogin(email, password)
                        } else {
                            onRegister(name, email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.padding(2.dp)
                        )
                    } else {
                        Text(
                            text = if (isLoginMode) "Sign In" else "Register",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isLoginMode) "Don't have an account? " else "Already have an account? ",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (isLoginMode) "Sign Up" else "Sign In",
                        color = PrimaryBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onModeChange(!isLoginMode) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode - Login")
@Composable
fun AuthPreviewLightLogin() {
    PackRightTheme(darkTheme = false) {
        AuthContent(
            isLoginMode = true,
            onModeChange = {},
            onLogin = { _, _ -> },
            onRegister = { _, _, _ -> },
            isLoading = false,
            error = null
        )
    }
}

@Preview(showBackground = true, name = "Light Mode - Register")
@Composable
fun AuthPreviewLightRegister() {
    PackRightTheme(darkTheme = false) {
        AuthContent(
            isLoginMode = false,
            onModeChange = {},
            onLogin = { _, _ -> },
            onRegister = { _, _, _ -> },
            isLoading = false,
            error = null
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode - Login")
@Composable
fun AuthPreviewDarkLogin() {
    PackRightTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AuthContent(
                isLoginMode = true,
                onModeChange = {},
                onLogin = { _, _ -> },
                onRegister = { _, _, _ -> },
                isLoading = false,
                error = null
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode - Register")
@Composable
fun AuthPreviewDarkRegister() {
    PackRightTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AuthContent(
                isLoginMode = false,
                onModeChange = {},
                onLogin = { _, _ -> },
                onRegister = { _, _, _ -> },
                isLoading = false,
                error = null
            )
        }
    }
}
