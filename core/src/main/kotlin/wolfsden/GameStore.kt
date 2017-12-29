package wolfsden

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.XmlReader
import squidpony.squidmath.StatefulRNG
import wolfsden.entity.Entity
import wolfsden.map.WolfMap
import java.io.*

object GameStore {
    var entityList: Map<String, Entity> = mutableMapOf()
    var mapList: Map<String, WolfMap> = mutableMapOf()

    val player: Entity
        get() = entityList["player"]!!

    val curMap: WolfMap
        get() = mapList[player.pos!!.mapID]!!

    val reader = XmlReader()

    val creatureBP = reader.parse(Gdx.files.internal("data/creatures.xml"))
    val equipBP = reader.parse(Gdx.files.internal("data/equipment.xml"))
    val itemBP = reader.parse(Gdx.files.internal("data/items.xml"))
    val heroBP = reader.parse(Gdx.files.internal("data/heroes.xml"))

    var WolfRNG = StatefulRNG(0xDEADBEEF)

    fun getByID(eID: String): Entity? = entityList[eID]

    fun saveGame() {
        val savePath = "${System.getProperty("user.home")}/WolfsDenKotlin"
        val fileName = player.id!!.name + ".wlf"
        try {
            val dir = File(savePath)
            if (!dir.exists() || !dir.isDirectory) {
                File(savePath).mkdir()
            }
            ObjectOutputStream(FileOutputStream("$savePath/$fileName")).use { it ->
                it.writeObject(entityList)
                it.writeObject(mapList)
                it.writeObject(WolfRNG)
            }
            println("Game saved")
        } catch (e: IOException) {
            println("Error saving game: ${e.stackTrace}")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun loadGame(fileName: String) {
        val filePath = "${System.getProperty("user.home")}/WolfsDenKotlin/$fileName"
        try {
            ObjectInputStream(FileInputStream(filePath)).use { it ->
                val entityBlob = it.readObject()
                val mapBlob = it.readObject()
                val rngBlob = it.readObject()

                when (entityBlob) {
                    is MutableMap<*, *> ->
                        entityList = entityBlob as MutableMap<String, Entity>
                    else -> throw IOException("Error loading entity table")
                }

                when (mapBlob) {
                    is MutableMap<*, *> -> mapList = mapBlob as MutableMap<String, WolfMap>
                    else -> throw IOException("Error loading map table")
                }

                when (rngBlob) {
                    is StatefulRNG -> WolfRNG = rngBlob
                    else -> throw IOException("Error loading RNG")
                }
            }
            println("$fileName loaded")
        } catch (e: IOException) {
            println("Error loading $fileName: ${e.stackTrace}")
        }
    }

    fun newGame(playerClass: String, playerName: String) {

    }
}