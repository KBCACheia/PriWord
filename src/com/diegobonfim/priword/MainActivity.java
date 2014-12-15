package com.diegobonfim.priword;

import java.util.ArrayList;
import java.util.Collections;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity {
	
	
	ArrayAdapter<String> adapterListaPalavras;
	ArrayList<String> arrayPalavra = new ArrayList<String>();
	ArrayList<ArrayList<String>> arrayListaPalavras;
	SQLiteDatabase bancoDados = null;
	String tabelaPalavras = "palavras";
	String[] camposTabelaPalavras = new String[]{"idPalava", "palavra", "definicao", "classes", "traducao", "irregular", "pastform", "pastparticiple"};
	String tabelaGruposPalavras = "grupos_palavras";
	String[] camposTabelaGruposPalavras = new String[]{"grupoNome", "PalavraNome"};
	String tabelaGrupos = "grupos";
	String[] camposTabelaGrupos = new String[]{"idGrupos", "grupo"};
	String tabelaFrases = "frases";
	String[] camposTabelaFrases = new String[]{"idFrases", "frases", "palavrasID"};
	Cursor cursor;
	String nomeBanco = "vocabulario.db";
	ListView listaPalavras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		listaPalavras = (ListView) findViewById(R.id.home_lvPalavras);
		//deleteDatabase(nomeBanco);
		abreouCriaBanco();
		buscarDados();
		listener();
		criarLista();
		fechaBanco();
	}
	
	public void abreouCriaBanco() {
		 try {			   
			   //cria ou abre o banco de dados
			   bancoDados = openOrCreateDatabase(nomeBanco, MODE_PRIVATE, null);
			   String palavra = "CREATE TABLE IF NOT EXISTS "+tabelaPalavras+
					   "(idPalava INTEGER PRIMARY KEY AUTOINCREMENT," +
					   " palavra TEXT," +
					   " definicao TEXT," +
					   " classes TEXT," +
					   " traducao TEXT," +
					   " irregular TEXT," +
					   " pastform TEXT," +
					   " pastparticiple TEXT);";
			   bancoDados.execSQL(palavra);
			   String grupo_palavra = "CREATE TABLE IF NOT EXISTS "+tabelaGruposPalavras+
					   "(grupoNome TEXT," +
					   " PalavraNome TEXT);";
			   bancoDados.execSQL(grupo_palavra);
			   String grupos = "CREATE TABLE IF NOT EXISTS "+tabelaGrupos+
					   "(idGrupos INTEGER PRIMARY KEY AUTOINCREMENT," +
					   " grupo TEXT);";
			   bancoDados.execSQL(grupos);	
			   String frase = "CREATE TABLE IF NOT EXISTS "+tabelaFrases+"(idFrases INTEGER PRIMARY KEY AUTOINCREMENT," +
			   		" frases TEXT," +
			   		" palavraID INTEGER);";
			   bancoDados.execSQL(frase);	
			   Log.i("Banco Main", "Banco criado com sucesso ");
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
	
	private boolean buscarDados() {
		
		  try {
			   cursor = bancoDados.query("palavras", camposTabelaPalavras, 
					   null,//selection, 
					   null,//selectionArgs, 
					   null,//groupBy, 
					   null,//having, 
					   null,//"order by nome"//orderBy)
					   null); // Limite de registros retornados			   
			   
			   if (cursor.getCount() != 0)			   {
				   // no java puro resultsewt.first();
				   cursor.moveToFirst(); //posiciona no primeiro registro
				   return true;
			   }
			   else
				   return false;
			   
			   
		   } catch(Exception erro) {
			     Toast.makeText(MainActivity.this, "Erro buscar dados no banco: "+erro.getMessage(), Toast.LENGTH_LONG).show();	    
			     return false;
		   }
	}

	public void criarLista(){
		for (int i=0; i < cursor.getCount(); i++){
			arrayPalavra.add(cursor.getString(cursor.getColumnIndex("palavra")).toString());
			cursor.moveToNext();
		}
		Collections.sort(arrayPalavra);
		adapterListaPalavras = new ArrayAdapter<String>(this, R.layout.item_list, arrayPalavra);
		listaPalavras.setAdapter(adapterListaPalavras);
		listaPalavras.setTextFilterEnabled(true);
		listaPalavras.requestFocus();
		
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
	
	private void listener(){
		listaPalavras.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent iTelaCadastroConsulta = new Intent(MainActivity.this, CadastroConsulta.class);
				iTelaCadastroConsulta.putExtra("Palavra", listaPalavras.getItemAtPosition(position).toString());
				startActivity(iTelaCadastroConsulta);
				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem edit = menu.findItem(R.id.menu_edit);
		edit.setVisible(false);
		MenuItem save = menu.findItem(R.id.menu_save);
		save.setVisible(false);
		MenuItem cancel = menu.findItem(R.id.menu_cancel);
		cancel.setVisible(false);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_settings) {
			return true;
		}
		if (id == R.id.menu_add){
			Intent iTelaCadastroConsulta = new Intent(this, CadastroConsulta.class);
			startActivity(iTelaCadastroConsulta);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
