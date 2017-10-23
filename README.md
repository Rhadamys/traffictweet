# TrafficTweet
<center><i>Taller de bases de datos - Segundo semestre 2017<br>
Grupo 5: Mario Álvarez, Fabián Escobar, Luis Migryk, Nicolás Paredes, Cristian Sepúlveda</i></center>

## 1. Herramientas
Para utilizar este proyecto se deben instalar las siguientes herramientas (_actualizado al Sprint 1_):
- Gradle
- JDK 1.8
- MongoDB
- MySQL Server
- Algún IDE (Recomendado: [_IntelliJ IDEA_](https://www.jetbrains.com/idea/))

## 2. Configuración
### 2.1 Configuración MySQL
Abrir una terminal y ejecutar `mysql -u root`. Esto entrará en la consola de MySQL en modo `root`. Seguidamente, ejecutar los siguientes comandos:
- Crear usuario `CREATE USER 'traffictweet'@'localhost' IDENTIFIED BY 'secret';`
- Crear _schema_ `CREATE DATABASE traffictweet;`
- Otorgar todos los permisos a _traffictweet_ `GRANT ALL PRIVILEGES ON traffictweet.* TO 'traffictweet'@'localhost';`

### 2.2 Preparar entorno Vue.js
Para ejecutar Vue, debe instalar las dependencias necesarias para ejecutar el servidor de Node.js. Para esto, realice los siguientes pasos:
- Dirigirse a la carpeta `src/main/resources/static`
- Abrir una terminal en dicha ubicación
- Ejecutar el comando `npm install`
    -   NOTA: Si ocurre un error en la instalación, ejecutar en modo superusuario con `sudo`

### 2.3 Dependencias del proyecto
__NOTA:__ Si utiliza el IDE _IntelliJ IDEA_, este paso no es necesario. El IDE descargará automáticamente las dependencias.
- Dirigirse a la raíz del proyecto.
- Abrir una terminal.
- Ejecutar `gradle build`. Esto descargará las dependencias necesarias para el proyecto.

## 3. Ejecutar
### 3.1 Obtener tweets con TwitterStreaming
Previo:
- Asegúrese que MongoDB está corriendo. Ejecutar `sudo service mongod start` para iniciar el servicio.

Pasos:
- Ejecutar el método _main_ de la clase _TwitterStreaming_ ubicada en `src/main/java/cl/usach/traffictweet/Twitter/`.
- Esperar a que se establezca la conexión.
- Los tweets comenzarán a descargarse. Se recomienda descargar una gran cantidad de tweets antes de detener.

### 3.2 Construir índice con Lucene
Previo:
- Asegúrese que MongoDB está corriendo. Ejecutar `sudo service mongod start` para iniciar el servicio.

Pasos:
- Ejecutar el método _main_ de la clase _Lucene_ ubicada en `src/main/java/cl/usach/traffictweet/Twitter/`.
- Se construirá el índice. Asegúrese que el proceso finalice con código 0 en la terminal.
- Los archivos del índice se ubican en `./indice/`.

### 3.3 Generar Scheme y poblar base de datos
Previo:
- Asegúrese que MongoDB está corriendo. Ejecutar `sudo service mongod start` para iniciar el servicio.
- También debe estar en ejecución el servicio de MySQL server y haber realizado la configuración necesaria explicada en la sección _2.1_.

Pasos:
- Diríjase a la raíz del proyecto y habra una terminal.
- Ejecutar el comando `gradle bootRun`
- Después de unos segundos (o minutos) se desplegará la aplicación.
- Puede acceder a los servicios REST a través de la URL `http://localhost:9090`. Consultar la tabla _3.1_ para obtener una lista de los servicios disponibles.

#### <center>Tabla 3.1: Servicios REST disponibles</center>

|Ruta|Descripción|
|-|-|
|`/categories`|Obtiene todas las categorías de evento|
|`/communes`|Obtiene todas las comunas de la RM|
|`/keywords`| Obtiene la bolsa de palabras para buscar en el índice de _Lucene_|
|`/occurrences`|Obtiene todos los eventos|

__NOTA:__ Para ejecuciones futuras, ejecutar solamente a partir de este paso. En caso de querer descargar más _tweets_, comenzar con el paso 1.

### 3.4 Front-end con Vue
Previo:
- Asegúrese de haber realizado todos los pasos anteriores antes de ejecutar este paso.

Pasos:
- Dirigirse a la carpeta `src/main/resources/static`
- Abrir una terminal en dicha ubicación
- Ejecutar el comando `npm run dev`
- Al finalizar la compilación, se abrirá automáticamente el navegador predeterminado.