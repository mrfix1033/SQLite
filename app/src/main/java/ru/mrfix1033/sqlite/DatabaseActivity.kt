package ru.mrfix1033.sqlite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DatabaseActivity : AppCompatActivityWithExitMenu() {
    private val positions = listOf("Junior", "Middle", "Senior", "TeamLead")
    private val persons = mutableListOf<Person>()
    private lateinit var listViewAdapter: ArrayAdapter<Person>
    private lateinit var databaseManager: DatabaseManager

    private lateinit var editTextName: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var spinnerPosition: Spinner
    private lateinit var buttonSaveData: Button
    private lateinit var buttonGetData: Button
    private lateinit var buttonDeleteData: Button
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setContentView(R.layout.activity_database)
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextName = findViewById(R.id.editTextName)
        editTextPhone = findViewById(R.id.editTextPhone)
        spinnerPosition = findViewById(R.id.spinnerPosition)
        buttonSaveData = findViewById(R.id.buttonSaveData)
        buttonGetData = findViewById(R.id.buttonGetData)
        buttonDeleteData = findViewById(R.id.buttonDeleteData)
        listView = findViewById(R.id.listView)

        spinnerPosition.adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            positions
        )

        buttonSaveData.setOnClickListener {
            databaseManager.insert(
                Person(
                    editTextName.text.toString(),
                    editTextPhone.text.toString(),
                    positions[spinnerPosition.selectedItemPosition]
                )
            )

            editTextName.text.clear()
            editTextPhone.text.clear()
            spinnerPosition.setSelection(0)
        }

        buttonGetData.setOnClickListener {
            databaseManager.select {
                if (it == null) throw RuntimeException("Произошла ошибка, cursor=null")
                persons.clear()
                while (it.moveToNext()) {
                    it.run {
                        persons.add(
                            Person(
                                getString(1),
                                getString(2),
                                getString(3)
                            )
                        )
                    }
                }
                listViewAdapter.notifyDataSetChanged()
            }
        }

        buttonDeleteData.setOnClickListener {
            databaseManager._REMOVE_ALL()
            persons.clear()
            listViewAdapter.notifyDataSetChanged()
        }

        createAndSetListViewAdapter()

        databaseManager = DatabaseManager(this, null)
    }

    private fun createAndSetListViewAdapter() {
        listViewAdapter = object : ArrayAdapter<Person>(this, R.layout.person_item, persons) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var view = convertView
                if (view == null) view = LayoutInflater.from(this@DatabaseActivity)
                    .inflate(R.layout.person_item, parent, false)!!
                view.run {
                    persons[position].run {
                        findViewById<TextView>(R.id.textViewName).text = name
                        findViewById<TextView>(R.id.textViewPhone).text = phoneNumber
                        findViewById<TextView>(R.id.textViewPosition).text = this.position
                    }
                }
                return view
            }
        }
        listView.adapter = listViewAdapter
    }
}