package com.diegobonfim.priword;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {
	
	SQLiteDatabase bancoDados = null;
	ArrayAdapter<String> adapterListaPalavras;
	String nomeBanco = "vocabulario";
	
	String [] formaVerbos = new String[] {"Base Form", "Past Tense", "Past participle"};
	String[] classesGramaticais = new String[] {"Adjective", "Adverb", "Article", "Conjunction", "Interjection", "Noun"
			, "Number",	"Phrasal Verb", "Postposition", "Preosition", "Pronoun", "Substantive",	"Verb"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		criarLista();
		abreouCriaBanco();
	}
	
	public void abreouCriaBanco() {
		 try {			   
			   //cria ou abre o banco de dados
			   bancoDados = openOrCreateDatabase(nomeBanco, MODE_PRIVATE, null);
			   String sql = "CREATE TABLE IF NOT EXISTS palavras"
				   +"(id INTEGER PRIMARY KEY AUTOINCREMENT, palavra TEXT);" +
				   "CREATE TABLE IF NOT EXISTS classe_exemplo"
				   +"(id INTEGER PRIMARY KEY AUTOINCREMENT, palavraID INTEGER, classePalavra TEXT, fraseClasse TEXT);" +
				   "CREATE TABLE IF NOT EXISTS formaVerbos"
				   +"(id INTEGER PRIMARY KEY AUTOINCREMENT, palavraID INTEGER, formaVerbo TEXT, tipo TEXT);";
			   bancoDados.execSQL(sql);
			   Log.i("Banco", "Banco criado com sucesso ");
		   }
		   catch(Exception erro)
		   {
			   Log.i("Erro Banco", "Erro ao abrir ou criar o banco: "+erro.getMessage());
		   }
	}

	public void fechaBanco(){
		   try {
	             bancoDados.close(); //fecha banco de dados		   
		   }
		   catch(Exception erro) {
			   Log.i("Erro Banco", "Erro ao fechar o banco: "+erro.getMessage());
		   }
	}
	
	/*private boolean buscarDados() {
		
		  try {
			   cursor = bancoDados.query("pessoas", 
					   new String [] {"nome","endereco","telefone"}, 
					   null,//selection, 
					   null,//selectionArgs, 
					   null,//groupBy, 
					   null,//having, 
					   null,//"order by nome"//orderBy)
					   null); // Limite de registros retornados
			   campoNome = cursor.getColumnIndex("nome");
			   campoEndereco = cursor.getColumnIndex("endereco");
			   campoTelefone = cursor.getColumnIndex("telefone");
			   int numeroRegistros = cursor.getCount();
			   if (numeroRegistros != 0)
			   {
				   // no java puro resultsewt.first();
				   cursor.moveToFirst(); //posiciona no primeiro registro
				   return true;
			   }
			   else
				   return false;
			   
			   
		   }
		   catch(Exception erro) {
		     mensagemExibir("Erro Banco", "Erro buscar dados no banco: "+erro.getMessage());	    
		     return false;
		   }
	}*/

	

	/*public void insereRegistro() {
		   try {
			   String sql="INSERT INTO pessoas (nome, endereco, telefone) values ('"
				   +etNome.getText().toString()+"','"
				   +etEndereco.getText().toString()+"','"
				   +etTelefone.getText().toString()+"')";		   
			   bancoDados.execSQL(sql);			   		   
		   }
		   catch(Exception erro) {
			   mensagemExibir("Erro Banco", "Erro ao gravar dados no banco: "+erro.getMessage());
			  
		   }
	}*/

	public void criarLista(){
		ListView listaPalavras = (ListView) findViewById(R.id.home_lvPalavras);
		adapterListaPalavras = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, classesGramaticais);
		listaPalavras.setAdapter(adapterListaPalavras);
		listaPalavras.setTextFilterEnabled(true);
		
		EditText etPesquisa = (EditText)findViewById(R.id.home_etPesquisaPrincipal);
		//Filtro do List View
		etPesquisa.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				adapterListaPalavras.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
