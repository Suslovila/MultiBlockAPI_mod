package com.suslovila.api

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.EOFException
import java.io.IOException


@Throws(JsonIOException::class, JsonSyntaxException::class)
fun <T> Gson.fromJsons(reader: JsonReader, typeOfT: TypeToken<T>?): T? {
    var isEmpty = true
    val oldLenient = reader.isLenient
    reader.isLenient = true
     try {
        reader.peek()
        isEmpty = false
        val typeAdapter: TypeAdapter<T> = this.getAdapter(typeOfT)
        return typeAdapter.read(reader)
    } catch (e: EOFException) {
        /*
             * For compatibility with JSON 1.5 and earlier, we return null for empty
             * documents instead of throwing.
             */
        if (isEmpty) {
            return null
        }
        throw JsonSyntaxException(e)
    } catch (e: IllegalStateException) {
        throw JsonSyntaxException(e)
    } catch (e: IOException) {
        // TODO(inder): Figure out whether it is indeed right to rethrow this as JsonSyntaxException
        throw JsonSyntaxException(e)
    } catch (e: AssertionError) {
        throw AssertionError("AssertionError (GSON ): " + e.message, e)
    } finally {
        reader.isLenient = oldLenient
    }
}