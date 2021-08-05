package com.test.weatherapp.utils.cookie

import java.io.*
import java.lang.reflect.Field
import java.net.HttpCookie
import kotlin.experimental.and

class SerializableHttpCookie : Serializable {
    @Transient
    private var cookie: HttpCookie? = null
    private lateinit var fieldHttpOnly: Field
    fun encode(cookie: HttpCookie?): String? {
        this.cookie = cookie
        val os = ByteArrayOutputStream()
        try {
            val outputStream = ObjectOutputStream(os)
            outputStream.writeObject(this)
        } catch (e: IOException) {
            return null
        }
        return byteArrayToHexString(os.toByteArray())
    }

    fun decode(encodedCookie: String): HttpCookie? {
        val bytes = hexStringToByteArray(encodedCookie)
        val byteArrayInputStream = ByteArrayInputStream(
            bytes
        )
        var cookie: HttpCookie? = null
        try {
            val objectInputStream = ObjectInputStream(
                byteArrayInputStream
            )
            cookie = (objectInputStream.readObject() as SerializableHttpCookie).cookie
        } catch (e: IOException) {
        } catch (e: ClassNotFoundException) {
        }
        return cookie
    }

    private var httpOnly: Boolean
        private get() {
            try {
                initFieldHttpOnly()
                return fieldHttpOnly!![cookie] as Boolean
            } catch (e: Exception) {
            }
            return false
        }
        private set(httpOnly) {
            try {
                initFieldHttpOnly()
                fieldHttpOnly!![cookie] = httpOnly
            } catch (e: Exception) {
            }
        }

    @Throws(NoSuchFieldException::class)
    private fun initFieldHttpOnly() {
        fieldHttpOnly = cookie!!.javaClass.getDeclaredField("httpOnly")
        fieldHttpOnly.isAccessible = true
    }

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.writeObject(cookie!!.name)
        out.writeObject(cookie!!.value)
        out.writeObject(cookie!!.comment)
        out.writeObject(cookie!!.commentURL)
        out.writeObject(cookie!!.domain)
        out.writeLong(cookie!!.maxAge)
        out.writeObject(cookie!!.path)
        out.writeObject(cookie!!.portlist)
        out.writeInt(cookie!!.version)
        out.writeBoolean(cookie!!.secure)
        out.writeBoolean(cookie!!.discard)
        out.writeBoolean(httpOnly)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) {
        val name = `in`.readObject() as String
        val value = `in`.readObject() as String
        cookie = HttpCookie(name, value)
        cookie!!.comment = `in`.readObject() as String
        cookie!!.commentURL = `in`.readObject() as String
        cookie!!.domain = `in`.readObject() as String
        cookie!!.maxAge = `in`.readLong()
        cookie!!.path = `in`.readObject() as String
        cookie!!.portlist = `in`.readObject() as String
        cookie!!.version = `in`.readInt()
        cookie!!.secure = `in`.readBoolean()
        cookie!!.discard = `in`.readBoolean()
        httpOnly = `in`.readBoolean()
    }

    private fun byteArrayToHexString(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (element in bytes) {
            val v: Byte = element and 0xff.toByte()
            if (v < 16) {
                sb.append('0')
            }
            sb.append(Integer.toHexString(v.toInt()))
        }
        return sb.toString()
    }

    private fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character
                .digit(hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    companion object {
        private val TAG = SerializableHttpCookie::class.java
            .simpleName
        private const val serialVersionUID = 6374381323722046732L
    }
}