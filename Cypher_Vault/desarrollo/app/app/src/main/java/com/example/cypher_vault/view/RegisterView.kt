import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RegistrationNav() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "register") {
        composable("register") {
            RegisterView(navController)
        }
        composable("camera") {
            CameraScreen(navController)
        }
        composable("confirmation") {
            ConfirmationScreen(navController)
        }
        composable("login") {
            //
        }
    }
}

@Composable
fun RegisterView(navController: NavController) {
    // Agrega estados para los campos de texto
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val nameState = remember { mutableStateOf(TextFieldValue()) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            // Campo de texto para el correo electrónico
            TextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Correo electrónico") }
            )

            // Campo de texto para el nombre
            TextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Nombre") }
            )

            // Botón de la cámara
            Button(onClick = { navController.navigate("camera") }) {
                Text("Registrarse")
            }
        }
    }
}

@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val isCameraOpen = remember { mutableStateOf(true) }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()

    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder().build()

    LaunchedEffect(cameraProviderFuture) {
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isCameraOpen.value) {
            CameraPreview(preview)
            CloseCameraButton(isCameraOpen, cameraProvider, navController)
        }
    }
}


@Composable
fun CloseCameraButton(isCameraOpen: MutableState<Boolean>, cameraProvider: ProcessCameraProvider, navController: NavController) {
    Button(onClick = {
        // Cierra la cámara
        isCameraOpen.value = false
        cameraProvider.unbindAll()
        navController.navigate("confirmation")
    }) {
        Text("Tomar foto")
    }
}

@Composable
fun ConfirmationScreen(navController: NavController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.wrapContentSize()
        ) {
            Text( "¡Ya estás registrado! ")
            Button(onClick = {
                navController.navigate("confirmation")
            }) {
                Text("Iniciar sesión")
            }
        }
    }
}

@Composable
fun CameraPreview(preview: Preview) {
    AndroidView(
        factory = {
            PreviewView(it).apply {
                preview.setSurfaceProvider(this.surfaceProvider)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
