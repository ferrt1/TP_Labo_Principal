
---
# Proyecto de Reconocimiento Facial

![alt text](image.png)

#### Primer cuatrimestre 2024 - Universidad Nacional de General Sarmiento - Comisión 02

**Profesores:**

- Ing. Francisco Orozco De La Hoz @ forozco@campus.ungs.edu.ar 
- Lic. Leandro Dikenstein @ ldikenstein@campus.ungs.edu.ar 

**Equipo de trabajo:**

- Flavio Ybarra
- Alejandro Moras
- Javier Galeano
- Melanie Ibarra
- Ivan Sanchez
- Fernando Trejo

---

## Introducción

Bienvenidos al proyecto de Autenticación Facial en Kotlin. Este documento tiene como objetivo dar a conocer el desarrollo de una aplicación capaz de autenticar a los usuarios a través del reconocimiento facial enfocado para Tablets. La misma está diseñada para funcionar sin conexión a internet.


## Objetivos del Proyecto

1. Desarrollar una aplicación de reconocimiento facial.
2. Implementar la aplicación en Android usando Kotlin y Android Studio.
3. Utilizar OpenCV y TensorFlow Lite para el reconocimiento facial.
4. Almacenar en el dispositivo imágenes cifradas/encriptadas.
5. Facilitar el uso de la aplicación para cualquier tipo de usuario.
6. Optimizar la aplicación para un uso eficiente de los recursos del dispositivo.

## Objetivos del Documento

Este documento tiene como objetivo explicar cuáles son los pasos a seguir  en el ciclo de vida del desarrollo de este software, es decir, se detallarán los requerimientos funcionales, no funcionales, armado de la WBS (funcionalidades del proyecto), definición de roles, estimaciones de implementación y diagrama de arquitectura. Más adelante se detalla mejor el objetivo de cada uno.

1. Proporcionar una visión general del proyecto.
2. Definir los roles y responsabilidades del equipo.
3. Esbozar la metodología del proyecto.

## Definición de Roles

- **Líder del Proyecto**: Es el individuo responsable de administrar el proyecto.
- **Scrum Master**: Supervisa el progreso del proyecto y se asegura de que se cumplan los plazos.
- **Desarrolladores**: Encargados de la codificación y la implementación de la aplicación.
- **Tester**: Responsable de probar la aplicación en diferentes escenarios y reportar errores.

## Equipo de Trabajo y Roles

| Nombre | Rol Primario | Rol Secundario |
|---------|-------|--------|
| Profesor | Líder del Proyecto | Sponsor |
| Flavio Ybarra | Scrum Master | Tester |
| Alejandro Moras | Desarrollador | UX/UI |
| Fernando Trejo | Desarrollador | UX/UI |
| Javier Galeano | Desarrollador | UX/UI |
| Ivan Sanchez | Tester | Capacitador y Prueba de Usuario |
| Melanie Ibarra | Tester | Scrum Master |

## Metodología

En este proyecto, implementaremos una combinación de metodologías ágiles y Waterfall, también conocida como "Wagile" o "Agilefall". Este enfoque nos permitirá aprovechar lo mejor de ambos métodos para adaptarnos a las necesidades específicas de nuestro equipo de seis personas. A continuación les presentaremos un resumen de cómo lo haremos:

- **Comprender las metodologías**: Todo el equipo debe entender Agile y Waterfall.
- **Identificar las fases del proyecto**: Dividiremos el proyecto en fases claramente definidas.
- **Aplicar Waterfall en las fases iniciales**: Usaremos Waterfall para la planificación, análisis de requerimientos y diseño.
- **Implementar Agile en las fases de desarrollo**: Aplicaremos Agile para las fases de desarrollo y pruebas.
- **Facilitar la comunicación y colaboración**: Fomentaremos la comunicación abierta y la colaboración durante todo el proceso.
- **Realizar retrospectivas periódicas**: Programaremos reuniones regulares de retrospectiva al final de cada fase o sprint.
- **Ser flexible y adaptativo**: Mantendremos una mentalidad flexible y adaptativa a medida que evolucione el proyecto.

Nuestro enfoque Agile se enfocará en Scrum, el cuál se basa en entregar funcionalidades de forma incremental, en períodos de dos semanas. Dentro de las mismas se realizan reuniones diarias del equipo para planificación, control y revisión del trabajo realizado hasta el momento.

Con este enfoque, nuestro equipo podrá gestionar eficazmente el proyecto, adaptarse a los cambios y entregar valor de manera constante y oportuna.


## Enlaces
- Repositorio: se decidió utilizar Github para que todos los miembros del equipo puedan acceder y trabajar con mayor comodidad. 
<a href="https://github.com/ferrt1/TP_Labo_Principal" target="_blank">Repositorio Github</a>

- WBS: Se decidio utilizar Miro que es una plataforma de colaboración digital para realizar la WBS. Por motivos de seguridad no se compartirá el link pero la misma se mostrará en la documentación.

- Herramientas a utilizar: Android Studio, OpenCV, TensorFlow Lite, Visual Studio.

- Comunicación de equipo: WhatsApp y Discord.

- User Stories: Trello

- Comunicación con el Líder del Proyecto: Telegram o Mail.

- Diagrama de arquitectura: draw.io

## Gestión


### Misión/Visión del Negocio
Nuestra visión: Aspirar en que sea una aplicación cómoda y fácil de usar dentro de los estandares de seguridad para así brindar tranquilidad y seguridad al usuario.
Nuestra misión: Es crear una aplicación en la cual el usuario se registra e ingresa a través del reconocimiento facial mediante la utilizacion de la cámara frontal del dispositivo (tablet). 
Posteriormente el ingreso del usuario se realizará comparando la foto tomada con las imágenes guardadas en el dispositivo, estas imágenes estan encriptadas y cifradas.

### Alcance
- Aplicación para dispositivos Android (Tablets) 
- Registro por reconocimiento facial
- Login por reconocimiento fácil
- Almacenamiento de imágenes en el dispositivo

*Como aún hay poca información sobre la implementación de la app no podemos confirmar las funcionalidades que quedan por fuera del alcance*

### Plan de Comunicaciones
Para facilitar la comunicación utilizamos la plataforma WhatApps y Discord, ya que nos proporcionan un canal de comunicación instantánea y versátil, que permite una interacción ágil entre los miembros del equipo. Esto nos permite compartir actualizaciones rápidas, discutir ideas y mantenernos conectados en tiempo real.
Para gestionar las tareas y el progreso del proyecto, se utilizará Trello. Nos permitió crear un flujo de trabajo estructurado y asignar tareas, asegurando que cada miembro del equipo esté al tanto de sus responsabilidades y plazos.


### Requerimientos
En este apartado se detallarán los requerimientos del sistema, además se hará mención de la nomenclatura a utilizar para la clasificación de dichos requerimientos.
Los requerimientos funcionales son aquellos que definen la funcionalidades que va a tener el software. Tales requerimientos se clasifican en estos tres tipos:

Requerimientos esenciales: Estos requerimientos hacen que el sistema tenga sentido, es decir, sin esta clases de funcionamientos no se cumplirían el objetivo que necesitan los usuarios.

Requerimientos importantes: Son aquellos que, si no están, el software funciona igual pero se limitará el funcionamiento.

Requerimientos deseables: Son componentes adicionales que pueden ser agregados al software pero su prioridad es la mínima.

Una vez explicado las clasificación de requerimientos funcionales,  se hará a continuación mención de los requerimientos no funcionales:

Requerimientos No funcionales: El objetivo de estos requerimientos es explicar las limitaciones o restricciones que el sistema posee. Estos requisitos no tienen ningún impacto en la funcionalidad del software, pero garantizan que el sistema satisfaga las necesidades de los usuarios del sistema.

Notación utilizada:

RFE: Requerimiento Funcional Esencial
RFI: Requerimiento Funcional Importante
RFD: Requerimiento Funcional Deseable
RNF: Requerimiento No Funcional

- Funcionales:
<ol>
  <li>Registro:
    <ol>
      <li>El sistema debe ser capaz de capturar imágenes de la cámara frontal de la Tablet.</li>
      <li>Se debe crear una interfaz de login donde el usuario se registre con sus datos (nombre y mail) y su rostro.</li>
      <li>La interfaz debe tener un boton para capturar la imagen.</li>
      <li>Tiene que solicitar los permisos necesarios para acceder a la camara (en el archivo androidManifest.xml)</li>
      <li>Se debera guardar la imagen en una base de datos almacenada en la tablet</li>
      <li>El sistema debe ser capaz de detectar rostros en las imágenes capturadas.</li>
      <li>El sistema debe ser capaz de identificar a las personas a partir de sus rostros.</li>
      <li>Asociar las rostros de las personas a su cuenta de registro</li>
    </ol> 
  </li>
  <li>Autentificación
    <ol>
      <li>Una vez registrado el usuario debe ser capaz de loguearse a su cuenta atraves de la verificaion facial</li>
      <li>El sistema debe ser capaz de autenticar a las personas comparando sus rostros con una base de datos de rostros conocidos almacenada en la Tablet.</li>
      <li>El sistema debe mostrar un mensaje de "Acceso Permitido" o "Acceso Denegado" en la pantalla de la Tablet en función del resultado de la autenticación.</li>
      <li>El sistema deberá registrar un log con los datos de ingresos (Hora, ID de persona, etc.)</li>
      <li>El sistema deberá permitir una alternativa manual de ingreso ante posibles desconexiones (sin Wifi o datos).</li>
    </ol>
  </li>
  <li>Perfil de usuario
    <ol>
      <li>El sistema deberá permitir el ALTA/MODIFICACIONES de las personas a autenticar.</li>
      <li>El usuario podra modificar su informacion personal o registrar otra foto de su rostro</li>
    </ol>
  </li>
</ol>

- No Funcionales:

<ol>
  <li>Usabilidad
    <ol>
      <li>Si el usuario desea entrar y no esta registrado se le debe mostrar un mensaje de "acceso denegado, primero necesitas registrarte"</li>
      <li>Si el usuario desea ingresar a su cuenta con una foto u otro rostro (no asociado a su cuenta) se le debe mostrar un mensaje de "acceso denegado".</li>
      <li>Si el usuario pudo ingresar a su cuenta se le debe mostrar un mensaje de "acceso permitido".</li>
    </ol>
  </li>
  <li>Rendimiento
    <ol>
      <li>El sistema debe ser eficiente en el uso de la batería, la memoria y el procesador de la Tablet.</li>
    </ol>
  </li>
</ol>


### WBS
(Descripción del WBS)

### Diccionario
(Descripción del diccionario)

### Calendario
| Entrega | Fecha | Tareas |
|---------|-------|--------|
| 1 | (19/4)| Presentación del documento, definición de roles, planificación inicial |
| 2 | (3/4) | Diseño de interfaz de usuario, base de datos |
| 3 | (17/5) | Desarrollo del backend y terminacion de interfaz – ¿modelo de datos? |
| 4 | (31/5) | Se sigue con el backend en teoria |
| 5 | (14/6) | Hacer Tests y esas cosas |
| 6 | (/5) | Presentación final ya como servicio - aplicación |

### Estimaciones Iniciales
(Descripción de las estimaciones iniciales)

### Riesgos
(Descripción de los riesgos y planes de contingencia)

### Entregables
(Descripción de los entregables)

### Administración en el Manejo de Bugs
(Descripción de cómo se manejarán los bugs)

### Administración de Cambios
(Descripción de cómo se manejarán los cambios)

### Indicadores a Utilizar
- Funcionalidad Completa
- Nivel de Calidad
- Evolución de la Prueba
- Cobertura de la Prueba
- Burndown Chart

### Tecnologías
- Android Studio
- Kotlin
- OpenCV
- TensorFlow Lite
