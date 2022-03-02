package com.rpmtw.rpmtw_api_client.models.gson.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.sql.Timestamp

public class TimestampAdapter : TypeAdapter<Timestamp>() {
    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Timestamp {
        return Timestamp(`in`.nextLong())
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, timestamp: Timestamp) {
        out.value(timestamp.time)
    }
}