package com.example.cypher_vault.view.login


import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.income.UserAccessController
import com.example.cypher_vault.controller.lockaccount.BlockUserController
import com.example.cypher_vault.controller.messages.getincorrectPassword
import com.example.cypher_vault.controller.messages.getsearcherMessage
import com.example.cypher_vault.controller.messages.getvalidaUserMessage
import com.example.cypher_vault.database.BlockedUsers
import com.example.cypher_vault.model.income.UserAccessManager
import com.example.cypher_vault.model.lockaccount.BlockUserManager
import com.example.cypher_vault.view.resources.CustomTitle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



private val databaseController = DatabaseController()

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun LoginText(){
    Text(
        "Inicio de sesión",
        style = textStyle,
        modifier = Modifier
            .padding(top = 70.dp)
            .offset(x = -(30.dp)),
    )

}

@Composable
fun NavigationLogin(navController: NavController) {
    val users by navController.users.collectAsState()
    val context = LocalContext.current
    val buttonTextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = thirdColor, fontFamily = fontFamily)
    var selectedPersona by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var userSelected by remember { mutableStateOf("") }

    var showConnectionOption by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var enteredPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val passwordVisible = remember { mutableStateOf(false) }

    var searcherVisible by remember { mutableStateOf(false) }
    var searcherUserVisible by remember { mutableStateOf(false) }


    val transition = updateTransition(showConnectionOption, label = "")
    val offsetY by transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
        label = ""
    ) { if (it) 0.dp else 300.dp }

    //Variables para el bloqueo de usuario
    val scope = rememberCoroutineScope()
    var blockUserManager = BlockUserManager()
    var blockUserController = BlockUserController(blockUserManager)
    var blockedUsers by remember { mutableStateOf<List<BlockedUsers?>>( emptyList()) }
    var blockedUser by remember { mutableStateOf<BlockedUsers?>(null) }

    LaunchedEffect(blockedUsers) {
        scope.launch {
            blockedUsers = blockUserController.getBlockedUsers()
        }
    }

    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .clickable { showConnectionOption = false }
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        )
        {
            CustomTitle()
            LoginText()


            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Busqué su usuario", style = TextStyle(color = thirdColor)) },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .onFocusChanged { focusState ->
                        searcherVisible = !focusState.isFocused
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = secondColor,
                    focusedBorderColor = firstColor,
                    unfocusedBorderColor = firstColor,
                    disabledBorderColor = firstColor,
                ),
                shape = RoundedCornerShape(4.dp),
                trailingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Icono de busqueda",
                        tint = thirdColor
                    )
                },
            )

            val filteredUsers = users.filter {
                it.firstName?.startsWith(searchQuery, ignoreCase = true) == true ||
                        it.email?.startsWith(searchQuery, ignoreCase = true) == true }
            if (filteredUsers.isEmpty() && searchQuery.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.iconwarning),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // Espacio entre la imagen y el texto
                        LimitedTextBox(
                            text = getvalidaUserMessage(),
                            maxWidth = 250.dp // Ajusta este valor según tus necesidades
                        )

                    }
                }
            }


            if(users.size!=0 && searchQuery.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.iconclarificatio),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // Espacio entre la imagen y el texto
                        LimitedTextBox(
                            text = getsearcherMessage(),
                            maxWidth = 250.dp // Ajusta este valor según tus necesidades
                        )

                    }
                }
            }



            LazyColumn(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .heightIn(max = 250.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val filteredUsers = users.filter {
                    it.firstName?.startsWith(searchQuery, ignoreCase = true) == true ||
                            it.email?.startsWith(searchQuery, ignoreCase = true) == true
                }

                items(filteredUsers) { user ->
                    Button(
                        onClick = {
                            userSelected = user.uid
                            selectedPersona = user.firstName
                            if (estaBloqueado(userSelected, blockedUsers)){
                                blockedUser = blockedUsers.find { it?.user_id == userSelected }
                                if(noTieneBloqueDeTiempo(blockedUser)){
                                    navController.navigateToLockScreen(userSelected)
                                }else{
                                    var tiempoRestanteDeBloqueo : Int = calcularTiempoRestante(blockedUser)
                                    Toast.makeText(
                                        context,
                                        "Tienes bloqueada la cuenta, quedan $tiempoRestanteDeBloqueo minutos.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }else{
                                showConnectionOption = true // Muestra las opciones de conexión
                            }
                        },
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(3.dp, firstColor),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = firstColor
                        ),
                        modifier = Modifier
                            .width(290.dp)
                            .padding(top = 15.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = user.firstName.toString(), //.take(16)?.let { if (it.length < 16) it else "$it..." } ?: "",
                                fontSize = 20.sp,
                                fontFamily = fontFamily,
                                color = thirdColor,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = true,
                                overflow = TextOverflow.Ellipsis //
                            )
                            Text(
                                text = user.email.toString(), //.take(23)?.let { if (it.length < 23) it else "$it..." } ?: "",
                                fontSize = 14.sp,
                                fontFamily = fontFamily,
                                color = thirdColor,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = true,
                                overflow = TextOverflow.Ellipsis //
                            )
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = { navController.navigateToRegister() },
                border = BorderStroke(2.dp, Color.White),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Registrarse", style = buttonTextStyle)
            }
        }
    }
    /////// PANEL DE OPCIONES DE LOGUEO POR USUARIO //////////////////////////////////////////////////////
    if (showConnectionOption) {
        Box(
            modifier = Modifier
                .offset(y = offsetY)
                .fillMaxSize()
                .clickable { showConnectionOption = false },
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .border(width = 2.dp, color = thirdColor, shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(Color.White)
                    .padding(0.dp, 32.dp, 0.dp, 32.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigateToCameraLogin(userSelected) },
                        shape = RoundedCornerShape(15.dp),
                        border = BorderStroke(3.dp, firstColor),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = thirdColor
                        ),
                        modifier = Modifier
                            .width(300.dp)
                    ) {
                        Text(
                            "Usar reconocimiento facial",
                            fontFamily = fontFamily,
                            color = thirdColor,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center

                        )
                    }
                    OutlinedButton(
                        onClick = {
                            showConnectionOption = false
                            showPasswordDialog = true
                            enteredPassword = "" },
                        shape = RoundedCornerShape(15.dp),
                        border = BorderStroke(3.dp, firstColor),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = thirdColor
                        ),
                        modifier = Modifier
                            .width(300.dp)
                            .padding(top = 30.dp)
                    ) {
                        Text(
                            "Usar contraseña",
                            fontFamily = fontFamily,
                            color = thirdColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    /////// PANEL DE CONTRASEÑA ///////////////////////////////////////////////////////////
    else if (showPasswordDialog) {
        var showError by remember { mutableStateOf(false) }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AlertDialog(
                containerColor = Color.White,
                shape = RoundedCornerShape(15.dp),
                onDismissRequest = { showPasswordDialog = false },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Inicio de sesión",
                            style = textStyle,
                            modifier = Modifier.background(Color.White),
                        )
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = enteredPassword,
                            onValueChange = {
                                if (it.length <= 32) {
                                    enteredPassword = it
                                    showError = false  // Resetear showError cuando la contraseña cambia
                                }
                            },
                            textStyle = TextStyle(
                                color = firstColor,
                                fontSize = 16.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            label = {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Contraseña",
                                        fontSize = 20.sp,
                                        fontFamily = fontFamily,
                                        color = thirdColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(
                                    onClick = { passwordVisible.value = !passwordVisible.value },
                                    modifier = Modifier.offset(y = 10.dp) // Ajusta este valor a tu preferencia
                                ) {
                                    Icon(
                                        imageVector = if (passwordVisible.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = if (passwordVisible.value) "Ocultar contraseña" else "Mostrar contraseña"
                                    )
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                cursorColor = thirdColor,
                                focusedIndicatorColor = firstColor,
                                unfocusedIndicatorColor = firstColor,
                            ),
                            modifier = Modifier
                                .width(250.dp)
                                .padding(top = 15.dp)
                                .border(BorderStroke(3.dp, firstColor), shape = RoundedCornerShape(4.dp))
                                .align(Alignment.CenterHorizontally)
                        )

                        if (showError) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.icoerror),
                                    contentDescription = "",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre la imagen y el texto
                                LimitedTextBox(
                                    text = getincorrectPassword(),
                                    maxWidth = 250.dp // Ajusta este valor según tus necesidades
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(
                            onClick = {
                                val password = enteredPassword
                                if (password.isNotEmpty()) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val isPasswordCorrect = databaseController.comparePasswords(userSelected, password)
                                        withContext(Dispatchers.Main) {
                                            if (isPasswordCorrect) {
                                                // Se agrega ingreso de usuario
                                                val userAccessManager = UserAccessManager()
                                                val userAccessController = UserAccessController(userAccessManager)
                                                // Ingreso de usuario
                                                navController.navigateToConfirmationLogin(userSelected,false)
                                                showPasswordDialog = false

                                            } else {
                                                showError = true
                                            }
                                        }
                                    }
                                }
                            },
                            shape = RoundedCornerShape(15.dp),
                            border = BorderStroke(3.dp, Color.Gray),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = thirdColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center)
                                .width(200.dp)
                                .padding(vertical = 5.dp)
                        ) {
                            Text(
                                "Aceptar",
                                fontFamily = fontFamily,
                                color = thirdColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                dismissButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(
                            onClick = {
                                showPasswordDialog = false
                            },
                            shape = RoundedCornerShape(15.dp),
                            border = BorderStroke(3.dp, Color.Gray),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = thirdColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center)
                                .width(200.dp)
                                .padding(vertical = 5.dp)
                        ) {
                            Text(
                                "Cancelar",
                                fontFamily = fontFamily,
                                color = thirdColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        }
    }


}

fun calcularTiempoRestante(blockedUser: BlockedUsers?): Int {
    if (blockedUser == null) {
        return 0
    }else{
        val bloqueo = 30 //minutos
        val tiempoActual = System.currentTimeMillis()
        val tiempoBloqueo = blockedUser.block_date
        val tiempoTranscurrido = (tiempoActual - tiempoBloqueo!!) / (1000 * 60)
        return (bloqueo - tiempoTranscurrido).toInt()
    }
}

fun noTieneBloqueDeTiempo(blockedUser: BlockedUsers?): Boolean {
    if (blockedUser == null) {
        return true
    }else{
        val bloqueo = 30 //minutos
        val tiempoActual = System.currentTimeMillis()
        val tiempoBloqueo = blockedUser.block_date
        val tiempoTranscurrido = (tiempoActual - tiempoBloqueo!!) / (1000 * 60)
        return tiempoTranscurrido >= bloqueo
    }
}

fun estaBloqueado(userSelected: String, blockedUsers: List<BlockedUsers?>?): Boolean {
    if (blockedUsers == null) {
        return false
    }
    for (user in blockedUsers) {
        if (user != null) {
            if (user.user_id == userSelected && user.blocked_user == true) {
                return true
            }
        }
    }
    return false
}


@Composable
fun LimitedTextBox(text: String, maxWidth: Dp) {
    Box(
        modifier = Modifier
            .width(maxWidth)


    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontFamily = fontFamily,
            color = thirdColor,
            fontWeight = FontWeight.Bold
        )
    }
}


