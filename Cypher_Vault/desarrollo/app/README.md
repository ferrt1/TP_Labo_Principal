# Desarrollo Fernando

## Interfaz de Registro

El código se divide en tres paquetes que se encuentran en `app > src > main > java > com.example.cypher_vault`. Acá hay dos paquetes: uno llamado 'controller' y otro llamado 'view'.

### Paquete Controller > Authentication
---


#### AuthenticationController

<br>

```kotlin
AuthenticationController
```

`AuthenticationController` es una clase que toma como parámetro un `NavController` y devuelve el `NavController` con la dirección a la que debe navegar. 
Cada dirección tiene su propia función: `fun navigateToCamera()`, `fun navigateToConfirmation()`, `fun navigateToLogin()` (esta última falta implementar).

<hr>

```kotlin
fun registerUser(
        email: String,
        name: String,
        showDialog: MutableState<Boolean>,
        errorMessage: MutableState<String>
    )
```

La función `registerUser` valida los campos por el momento. Más adelante deberá enviarlos al modelo para guardarlos en la base de datos. 
Recibe como parámetros `email`, `name`, `showDialog` y `errorMessage`. Estos parámetros son para que salga la alerta y mostrarla con sus respectivos mensajes.
Si todos los campos están bien, llama a `navigateToCamera` y los manda a la cámara.

Las funciones `validateMail()`, `validateName()` y `validateFields` verifican la validez de los campos de entrada.

 `validateMail(email: String)`: Se fija que se cumpla android.util.Patterns.EMAIL_ADDRESS.matcher(email).
 `validateName(name: String)`: Se fija que no tenga menos de 3 carácteres el nombre.
`validateFields(name: String, email: String)`: Se fija que no esten vacios.

<br>

### Paquete View > Registration

---

<br>

#### NavigationHost.kt

<hr>

```kotlin
fun NavigationHost()
```

`NavigationHost()` es una función que se utiliza para manejar la navegación en la aplicación, cada vez que se presiona un botón cambia las pantallas. 

Empieza en register por predeterminado y luego va cambiando, toma como parámetro las direcciones que le pasa el AuthenticationController.

**Definición de pantallas**: Dentro de esta función `NavHost`, se definen varias pantallas que representan diferentes partes:
    - **"register"**: Esta es la pantalla inicial donde los usuarios pueden registrarse. Muestra `InitialScreen`.
    - **"camera"**: Esta es la pantalla donde los usuarios pueden usar la cámara durante el proceso de registro. Muestra `RegistrationCameraScreen`.
    - **"confirmation"**: Esta es la pantalla donde los usuarios pueden confirmar su registro. Muestra `ConfirmationScreen`.
    - **"login"**: Esta es la pantalla donde los usuarios pueden iniciar sesión. Falta implementar.

<br>
<hr>

#### InitialScreen.kt
<hr>

```kotlin
fun RegistrationCameraScreen(authenticationController: AuthenticationController)
```

Recibe como parametro authenticationController para luego poder navegar por la aplicacion

`InitialScreen` es la pantalla inicial donde los usuarios se van a registrar. Se encuentran los campos de entrada para el correo electrónico y el nombre. Al hacer clic en el botón "Registrarse", se llama al método `registerUser` del 
`AuthenticationController`.

<br>
<hr>

#### RegistrationCameraScreen.kt

<hr>

```kotlin
fun RegistrationCameraScreen(authenticationController: AuthenticationController)
```

Recibe como parametro authenticationController para luego poder navegar por la aplicacion

Esta función Muestra la vista previa de la cámara
`ProcessCameraProvider:` Esta es una clase que se utiliza para interactuar con las cámaras disponibles en el dispositivo. En este caso, se obtiene una instancia de ProcessCameraProvider y 
se recuerda para su uso posterior.

`CameraSelector:` Esta es una clase que se utiliza para seleccionar una cámara en el dispositivo. En este caso, se está seleccionando la cámara frontal.


<hr>

```kotlin
fun CloseCameraButton(isCameraOpen: MutableState<Boolean>,
                      cameraProvider: ProcessCameraProvider,
                        authenticationController: AuthenticationController)
```

Botón que se muestra para cerrar la cámara e ir a la parte de ConfirmationScreen

<hr>

```kotlin
fun CameraPreview(preview: Preview)
```

Muestra la vista previa de la cámara en la interfaz de usuario. Utiliza la clase AndroidView para mostrar la vista previa de la cámara en la interfaz de usuario de Compose.

<hr>
<br>

#### ConfirmationScreen.kt

<hr>

```kotlin
fun ConfirmationScreen(authenticationController: AuthenticationController)
```

Recibe como parametro authenticationController para luego poder navegar por la aplicacion

`ConfirmationScreen` Es una pantalla que muestra un mensaje de que se pudo registrar y un botón para iniciar sesión
