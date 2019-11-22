package com.team8.safetrip

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.ContactsContract
import java.io.File
import java.io.IOException


class Contact (phone : String) {

    val phone = phone



    fun retrieveContactPhoto(number:String, context : Context): Bitmap {
        val contentResolver = context.contentResolver
        var contactId:String = ""
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val projection = arrayOf<String>(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID)
        val cursor = contentResolver.query(
            uri,
            projection, null, null, null)
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
            }
            cursor.close()
        }
        var photo = BitmapFactory.decodeResource(context.getResources(),
            R.drawable.default_image)
        try
        {
            val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong()))
            if (inputStream != null)
            {
                photo = BitmapFactory.decodeStream(inputStream)
            }
            assert(inputStream != null)
            inputStream.close()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
        return photo
    }


    fun getContactName(phoneNumber: String, context : Context): String {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection =
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        var contactName = ""
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }
        return contactName
    }

}