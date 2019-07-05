package com.example.training_sqlite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.EditText
import com.example.database.MyDataBaseHelper
import com.example.model.Note


class AddEditNoteActivity : AppCompatActivity() {

    var note: Note? = Note("A", "A")
    private val MODE_CREATE = 1
    private val MODE_EDIT = 2

    private var mode: Int = 0
    private var textTitle: EditText? = null
    private var textContent: EditText? = null

    private var needRefresh: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        this.textTitle = this.findViewById(R.id.note_title_edt)
        this.textContent = this.findViewById(R.id.note_content_edt)

        val intent = this.intent
        if (intent.getSerializableExtra("note") != null) {
            this.note = intent.getSerializableExtra("note") as Note
            Log.d("Update", note!!.id.toString())
            this.mode = MODE_EDIT
            this.textTitle!!.setText(note!!.title)
            this.textContent!!.setText(note!!.content)
        } else this.mode = MODE_CREATE

    }


    fun buttonSaveClicked(view: View) {
        val db = MyDataBaseHelper(this)

        val title = this.textTitle!!.text.toString()
        val content = this.textContent!!.text.toString()

        if (title == "" || content == "") {
            Toast.makeText(
                this,
                "Please enter title & content", Toast.LENGTH_LONG
            ).show()
            return
        }

        if (mode == MODE_CREATE) {
            this.note = Note(title, content)
            db.addNote(note!!)
        } else {
            this.note!!.title = title
            this.note!!.content = content
            db.updateNote(note!!)
        }

        this.needRefresh = true
        this.onBackPressed()
    }

    fun buttonCancelClicked(view: View) {
        this.onBackPressed()
    }

    override fun finish() {

        val data = Intent()
        data.putExtra("needRefresh", needRefresh)
        this.setResult(Activity.RESULT_OK, data)
        super.finish()
    }
}
