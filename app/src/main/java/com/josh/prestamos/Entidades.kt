package com.josh.prestamos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import android.content.Context
@Entity
data class Persona(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val prestamo: Double,
    val pagoMensual: Double,
    val fechaPrestamoDia: Long,      // LocalDate.toEpochDay()
    val cancelado: Boolean = false
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Persona::class,
        parentColumns = ["id"],
        childColumns = ["personaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["personaId", "mes"], unique = true)]
)
data class Pago(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personaId: Long,
    val mes: String,                 // "2026-07"
    val monto: Double,
    val fechaPagoDia: Long
)

@Dao
interface PrestamoDao {
    @Query("SELECT * FROM Persona ORDER BY nombre")
    fun personas(): Flow<List<Persona>>
    @Update
    suspend fun actualizarPersona(p: Persona)
    @Query("SELECT * FROM Persona")
    suspend fun personasUnaVez(): List<Persona>

    @Query("SELECT * FROM Pago")
    suspend fun pagosUnaVez(): List<Pago>
    @Query("SELECT * FROM Pago")
    fun pagos(): Flow<List<Pago>>

    @Query("SELECT COUNT(*) FROM Persona")
    suspend fun contarPersonas(): Int

    @Insert
    suspend fun insertarPersona(p: Persona): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarPago(p: Pago)

    @Query("DELETE FROM Pago WHERE personaId = :personaId AND mes = :mes")
    suspend fun borrarPago(personaId: Long, mes: String)

    @Query("UPDATE Persona SET cancelado = 1 WHERE id = :id")
    suspend fun cancelar(id: Long)

    @Query("DELETE FROM Persona WHERE id = :id")
    suspend fun eliminarPersona(id: Long)
}

@Database(entities = [Persona::class, Pago::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun dao(): PrestamoDao
}
object Bd {
    @Volatile private var instancia: AppDb? = null

    fun get(context: Context): AppDb =
        instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                context.applicationContext, AppDb::class.java, "prestamos.db"
            ).build().also { instancia = it }
        }
}