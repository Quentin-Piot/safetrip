package com.team8.safetrip

import java.io.*
import java.lang.Exception


class Data : Serializable {


    private val serialVersionUID = 20180617104400L

    var contactList: ArrayList<String> = arrayListOf("", "", "")
    var password : String = "0000"

    init {
        try {
            contactList = loadData().contactList
            password = loadData().password
            println(password)
           // println(contactList[0])
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun saveData() {

        val dirPath = "sdcard/safetrip"
        val filepath = "bonsoir.tp"

        val dirFile = File(dirPath)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        val file = File("$dirPath/$filepath")

        val oos = ObjectOutputStream(FileOutputStream(file))

        oos.writeObject(this)
    }


    fun loadData(): Data {
        val dirPath = "sdcard/safetrip"
        val filepath = "bonsoir.tp"

        val dirFile = File(dirPath)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        val file = File("$dirPath/$filepath")

        val ois = ObjectInputStream(FileInputStream(file))

        // désérialization de l'objet
        // désérialization de l'objet

        return ois.readObject() as Data
    }
}






