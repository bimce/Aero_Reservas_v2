import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class Credenciales(val email: String, val contraseña: String)

data class RespuestaAutenticacion(val mensaje: String, val usuario: Usuario?)

data class Usuario(val rut: String, val nombre: String, val email: String)

data class Comando(
    val operacion: String,
    val rut: String,
    val nombre: String,
    val email: String,
    val contraseña: String
    // Puedes agregar otros campos según sea necesario
)

data class Avion(
    val Nombre_vuelo: String,
    val IDAerolinea: String,
    val Origen: String,
    val Destino: String,
    val Capacidad: Int,
    val Precio: Double,
    val image: String,  // Esto podría ser un enlace a la imagen
    val ID_Vuelos: String,
    val Fecha_y_hora_de_llegada: String,
    val Fecha_y_hora_de_salida: String


)

data class Reserva(
    @SerializedName("usuario_rut")
    val usuario_rut: String,
    @SerializedName("ID_Vuelos")
    val ID_Vuelos: String,
    @SerializedName("Nombre_Apellido")
    val Nombre_Apellido: String,
    @SerializedName("Pais")
    val Pais: String,
    @SerializedName("Numero_de_Documento")
    val Numero_de_Documento: String,
    @SerializedName("Fecha_de_Nacimiento")
    val Fecha_de_Nacimiento: String,
    @SerializedName("Sexo")
    val Sexo: String,
    @SerializedName("Email")
    val Email: String,
    @SerializedName("Telefono")
    val Telefono: String
)




interface ApiService {
    @POST("/autenticar-usuario")
    fun autenticarUsuario(@Body credenciales: Credenciales): Call<RespuestaAutenticacion>

    @POST("/registro-mongodb-android")
    fun registrarEnMongoDBAndroid(@Body comando: Comando): Call<String>

    @POST("/registro-sql-android")
    fun registrarEnSQLAndroid(@Body comando: Comando): Call<String>

    @GET("/obtener-aviones")
    fun obtenerAviones(): Call<List<Avion>>

    @GET("/buscar-vuelos")
    fun buscarVuelos(
        @Query("Origen") origen: String?,
        @Query("Destino") destino: String?,
        @Query("Fecha_y_hora_de_salida") fechaSalida: String?,
        @Query("Fecha_y_hora_de_llegada") fechaLlegada: String?
    ): Call<List<Avion>>
    @GET("/obtener-historial-reservas")
    fun obtenerHistorialReservas(@Query("rut") rut: String): Call<List<Reserva>>
}









