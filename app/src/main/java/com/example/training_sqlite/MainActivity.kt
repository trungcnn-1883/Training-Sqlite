package com.example.training_sqlite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.DialogInterface
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.*
import android.widget.Toast
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.database.MyDataBaseHelper
import com.example.model.Note


class MainActivity : AppCompatActivity() {

    private var listView: ListView? = null

    companion object {
        private val MENU_ITEM_VIEW = 111
        private val MENU_ITEM_EDIT = 222
        private val MENU_ITEM_DELETE = 444
        private val MY_REQUEST_CODE = 1000
    }


    private val noteList = ArrayList<Note>()
    private var listViewAdapter: ArrayAdapter<Note>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)

        val db = MyDataBaseHelper(this)
        db.createDefaultNoteIfNeed()

        this.noteList.addAll(db.getAllNotes())

        this.listViewAdapter = ArrayAdapter<Note>(
            this,
            android.R.layout.simple_list_item_1, android.R.id.text1, this.noteList
        )

        this.listView!!.setAdapter(this.listViewAdapter)

        registerForContextMenu(this.listView)

        setSupportActionBar(findViewById(R.id.main_toolbar))
    }

    fun onHandleAddBtn(view: View) {
        val intent = Intent(this, AddEditNoteActivity::class.java)

        this.startActivityForResult(intent, MY_REQUEST_CODE)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var selected: Boolean = false
        when (item?.itemId) {
            R.id.action_refresh -> {
                refreshList()
                selected = true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return selected
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_toolbar, menu)
        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu, view: View,
        menuInfo: ContextMenu.ContextMenuInfo
    ) {

        super.onCreateContextMenu(menu, view, menuInfo)
        menu.setHeaderTitle("Select The Action")

        // groupId, itemId, order, title
        menu.add(0, MENU_ITEM_VIEW, 0, "View Note")
        menu.add(0, MENU_ITEM_EDIT, 1, "Edit Note")
        menu.add(0, MENU_ITEM_DELETE, 2, "Delete Note")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.getMenuInfo() as AdapterView.AdapterContextMenuInfo

        val selectedNote = this.listView!!.getItemAtPosition(info.position) as Note

        when (item.itemId) {
            MENU_ITEM_VIEW -> Toast.makeText(this, selectedNote.content, Toast.LENGTH_LONG).show()
            MENU_ITEM_EDIT -> {
                val intent = Intent(this, AddEditNoteActivity::class.java)
                intent.putExtra("note", selectedNote)
                this.startActivityForResult(intent, MY_REQUEST_CODE)
            }
            MENU_ITEM_DELETE -> {
                AlertDialog.Builder(this)
                    .setMessage("Note: " + selectedNote.title + "\n Are you sure you want to delete?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Yes",
                        DialogInterface.OnClickListener { dialog, id -> deleteNote(selectedNote) })
                    .setNegativeButton("No", null)
                    .show()
            }

            else -> return false
        }

        return true
    }

    /**
     * Use when an user agrees to delete a note
     *
     */
    private fun deleteNote(note: Note) {
        val db = MyDataBaseHelper(this)
        db.deleteNote(note)
        this.noteList.remove(note)
        this.listViewAdapter!!.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == MY_REQUEST_CODE) {
            val needRefresh = data!!.getBooleanExtra("needRefresh", true)
            if (needRefresh) {
                refreshList()
            }
        }
    }

    fun refreshList() {
        this.noteList.clear()
        val db = MyDataBaseHelper(this)
        this.noteList.addAll(db.getAllNotes())
        this.listViewAdapter!!.notifyDataSetChanged()
    }
}
