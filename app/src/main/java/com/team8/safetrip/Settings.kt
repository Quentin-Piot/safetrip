package com.team8.safetrip

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.IOException


class Settings : AppCompatActivity() {
    private val TAG = "Contacts"
    private var dataObj = Data()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        getData()

        contact1.setOnClickListener{
            pickContact(0)
        }

        contact2.setOnClickListener{
            pickContact(1)
        }

        contact3.setOnClickListener{
            pickContact(2)
        }

        saveSettings.setOnClickListener{

            dataObj.saveData()

        }
    }


    private fun pickContact(numC : Int) {
        Intent(Intent.ACTION_PICK, Uri.parse("content://contacts")).also { pickContactIntent ->
            pickContactIntent.type =
                ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE // Show user only contacts w/ phone numbers
            startActivityForResult(pickContactIntent, numC)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check which request it is that we're responding to
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // We only need the NUMBER column, because there will be only one row in the result
                val projection: Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)

                // Get the URI that points to the selected contact
                if (data != null) {
                    data.data?.also { contactUri ->
                        // Perform the query on the contact to get the NUMBER column
                        // We don't need a selection or sort order (there's only one result for this URI)
                        // CAUTION: The query() method should be called from a separate thread to avoid
                        // blocking your app's UI thread. (For simplicity of the sample, this code doesn't
                        // do that.)
                        // Consider using <code><a href="/reference/android/content/CursorLoader.html">CursorLoader</a></code> to perform the query.
                        contentResolver.query(contactUri, projection, null, null, null)?.apply {
                            moveToFirst()

                            // Retrieve the phone number from the NUMBER column
                            val column: Int = getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            val number: String = getString(column)
                            val contact = Contact(number)
                            var name : String = getContactName(number)
                            when (requestCode) {
                                0 -> {
                                    nameContact1.text = name
                                    contact1.setImageBitmap(retrieveContactPhoto(number))
                                }
                                1 -> {
                                    nameContact2.text = name
                                    contact2.setImageBitmap(retrieveContactPhoto(number))
                                }
                                else -> {
                                    nameContact3.text = name
                                    contact3.setImageBitmap(retrieveContactPhoto(number))
                                }
                            }
                            dataObj.contactList[requestCode] = number
                            println("fal : ${dataObj.contactList[0]}")



                        }
                    }
                }
            }

    }







    fun retrieveContactPhoto(number:String): Bitmap {
        val contentResolver = this.contentResolver
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
        var photo = BitmapFactory.decodeResource(this.getResources(),
            R.drawable.default_image)
        try
        {
            val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(this.getContentResolver(),
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


    fun getContactName(phoneNumber: String): String {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection =
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        var contactName = ""
        val cursor: Cursor? = this.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }
        return contactName
    }


    fun getData(){
        for (i in 0 until dataObj.contactList.size) {
            if(dataObj.contactList[i] != ""){
                var number = dataObj.contactList[i]
                var name : String = getContactName(number)

                when (i) {
                    0 -> {
                        nameContact1.text = name
                        contact1.setImageBitmap(retrieveContactPhoto(number))
                    }
                    1 -> {
                        nameContact2.text = name
                        contact2.setImageBitmap(retrieveContactPhoto(number))
                    }
                    else -> {
                        nameContact3.text = name
                        contact3.setImageBitmap(retrieveContactPhoto(number))
                    }
                }
        }
        }


    }

}
