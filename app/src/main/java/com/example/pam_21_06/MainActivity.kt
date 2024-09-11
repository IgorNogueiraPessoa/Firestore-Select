package com.example.pam_21_06

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pam_21_06.ui.theme.PAM2106Theme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PAM2106Theme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    App(db)
                }
            }
        }
    }
}

@Composable
fun App(db: FirebaseFirestore) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }

    // Estado para armazenar a lista de clientes
    var listaClientes by remember { mutableStateOf(listOf<Map<String, String>>()) }

    // Função para buscar clientes do Firestore e armazenar no estado
    fun selectClientes() {
        db.collection("Clientes")
            .get()
            .addOnSuccessListener { documents ->
                val clientes = mutableListOf<Map<String, String>>()
                for (document in documents) {
                    val cliente = mapOf(
                        "nome" to (document.getString("nome") ?: ""),
                        "telefone" to (document.getString("telefone") ?: "")
                    )
                    clientes.add(cliente)
                }
                listaClientes = clientes // Atualiza o estado com os dados
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    // Chamando selectClientes assim que o Composable é renderizado
    LaunchedEffect(Unit) {
        selectClientes()
    }

    PAM2106Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "App Firebase Firestore")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row {
                    Column(Modifier.padding(20.dp)) {
                        Text(text = "Nome:")
                    }
                    Column {
                        TextField(value = nome, onValueChange = { nome = it })
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(10.dp)) {
                        Text(text = "Telefone:")
                    }
                    Column {
                        TextField(value = telefone, onValueChange = { telefone = it })
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        val pessoas = hashMapOf(
                            "nome" to nome,
                            "telefone" to telefone
                        )

                        db.collection("Clientes").add(pessoas)
                            .addOnSuccessListener {
                                nome = ""
                                telefone = ""
                                selectClientes() // Atualiza a lista de clientes após a inserção
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    }) {
                        Text(text = "Cadastrar")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Exibindo a lista de clientes
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(listaClientes) { cliente ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = cliente["nome"] ?: "")
                            Text(text = cliente["telefone"] ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    val db = Firebase.firestore
    App(db)
}