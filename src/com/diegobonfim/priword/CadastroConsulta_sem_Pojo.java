package com.diegobonfim.priword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class CadastroConsulta_sem_Pojo extends ActionBarActivity{
	
	ArrayList<Integer> classesSelecionadas = new ArrayList<Integer>();
	ArrayList<String> classesSelecionadasAntes = null;
	ArrayList<String> classesExemplos = new ArrayList<String>();
	
	MenuItem add, save, edit, cancel;
	
	SQLiteDatabase bancoDados = null;
	Cursor cursorPalavras, cursorGrupos, cursorGruposPalavras, cursorFrases;
	String nomeBanco = "vocabulario.db";
	String tabelaPalavras = "palavras";
	String[] camposTabelaPalavras = new String[]{"idPalava", "palavra", "definicao", "classes", "traducao", "irregular", "pastform", "pastparticiple"};
	int idPalava=-1;
	String tabelaGruposPalavras = "grupos_palavras";
	String[] camposTabelaGruposPalavras = new String[]{"grupoNome", "PalavraNome"};
	String tabelaGrupos = "grupos";
	String[] camposTabelaGrupos = new String[]{"idGrupos", "grupo"};
	String tabelaFrases = "frases";
	String[] camposTabelaFrases = new String[]{"idFrases", "frases", "palavraID"};
	
	
	EditText etCadastroConsultaClasses, etCadastroConsultaPalavras, etCadastroConsultaAddExemplo, 
				etCadastroConsultaDefinicao, etCadastroConsultaTraducao, etCadastroConsultaGrupo, etCadastroConsultaPastForm, 
					etCadastroConsultaPastParticiple;
	Button btCadastroConsultaAddExemplo;
	ListView lvCadastroConsultaExemplos;
	TextView tvCadastroConsultaFormaVerbos, tvCadastroConsultaPastForm, tvCadastroConsultaPastParticiple;
	CheckBox cbCadastroConsultaIrregular;

	boolean[] classesDefinidasBoolean = null;
	String palavraSelecionada ="";
	String[] classesExemplosString = null;
	String[][]	frasesExemplosString = null;
	String[] classesGramaticais = new String[] {"Adjective", "Adverb", "Article", "Conjunction", "Interjection", "Noun"
			, "Number",	"Phrasal Verb", "Postposition", "Preosition", "Pronoun", "Substantive",	"Verb"};
	
	boolean atualizando=false, erroPalavra=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadastro_consulta);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abreouCriaBanco();
		
		etCadastroConsultaPalavras = (EditText) findViewById(R.id.cadastro_consulta_etPalavra);
		etCadastroConsultaPalavras.requestFocus();
		etCadastroConsultaDefinicao = (EditText) findViewById(R.id.cadastro_consulta_etDefinicao);
		etCadastroConsultaClasses = (EditText) findViewById(R.id.cadastro_consulta_etClasses);
		etCadastroConsultaPastForm = (EditText) findViewById(R.id.cadastro_consulta_etPastForm);
		etCadastroConsultaPastForm.setVisibility(android.view.View.GONE);
		etCadastroConsultaPastParticiple = (EditText) findViewById(R.id.cadastro_consulta_etPastParticiple);
		etCadastroConsultaPastParticiple.setVisibility(android.view.View.GONE);
		etCadastroConsultaGrupo = (EditText) findViewById(R.id.cadastro_consulta_etGrupos);
		etCadastroConsultaTraducao = (EditText) findViewById(R.id.cadastro_consulta_etTraducao);
		etCadastroConsultaAddExemplo = (EditText) findViewById(R.id.cadastro_consulta_etAddExemplo);
		etCadastroConsultaAddExemplo.setEnabled(false);
		btCadastroConsultaAddExemplo = (Button) findViewById(R.id.cadastro_consulta_btAddExemplo);
		btCadastroConsultaAddExemplo.setEnabled(false);
		cbCadastroConsultaIrregular = (CheckBox) findViewById(R.id.cadastro_consulta_cbIrregular);
		cbCadastroConsultaIrregular.setVisibility(android.view.View.GONE);
		tvCadastroConsultaFormaVerbos = (TextView) findViewById(R.id.cadastro_consulta_tvFormaVerbos);
		tvCadastroConsultaFormaVerbos.setVisibility(android.view.View.GONE);
		tvCadastroConsultaPastForm = (TextView) findViewById(R.id.cadastro_consulta_tvPastForm);
		tvCadastroConsultaPastForm.setVisibility(android.view.View.GONE);
		tvCadastroConsultaPastParticiple = (TextView) findViewById(R.id.cadastro_consulta_tvPastParticiple);
		tvCadastroConsultaPastParticiple.setVisibility(android.view.View.GONE);
		lvCadastroConsultaExemplos = (ListView) findViewById(R.id.cadastro_consulta_lvExemplos);
		
		Intent IDadosRecebidos = getIntent();
		if (IDadosRecebidos.getStringExtra("Palavra") != null){			
			palavraSelecionada = IDadosRecebidos.getStringExtra("Palavra");
			
			preencherTodaView(palavraSelecionada);
		}
				
		listener();
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        setContentView(R.layout.cadastro_consulta);

	    } else {
	        setContentView(R.layout.cadastro_consulta);
	    }
	}
	
	private void preencherTodaView(String palavra) {
		buscarDados();
		for (int i=0; i < cursorPalavras.getCount(); i++){
			if (cursorPalavras.getString(1).toString().equals(palavra)){
				etCadastroConsultaPalavras.setText(palavra);
				idPalava = cursorPalavras.getInt(cursorPalavras.getColumnIndex("idPalava"));
				etCadastroConsultaDefinicao.setText(cursorPalavras.getString(cursorPalavras.getColumnIndex("definicao")));
				etCadastroConsultaClasses.setText(cursorPalavras.getString(cursorPalavras.getColumnIndex("classes")));
				etCadastroConsultaTraducao.setText(cursorPalavras.getString(cursorPalavras.getColumnIndex("traducao")));
				if (cursorPalavras.getString(cursorPalavras.getColumnIndex("classes")).contains("Verb")){
					cbCadastroConsultaIrregular.setChecked(Boolean.valueOf(cursorPalavras.getString(cursorPalavras.getColumnIndex("irregular"))));
					cbCadastroConsultaIrregular.setVisibility(android.view.View.VISIBLE);
					etCadastroConsultaPastForm.setText(cursorPalavras.getString(cursorPalavras.getColumnIndex("pastform")));
					etCadastroConsultaPastParticiple.setText(cursorPalavras.getString(cursorPalavras.getColumnIndex("pastparticiple")));
					etCadastroConsultaPastForm.setVisibility(android.view.View.VISIBLE);
					etCadastroConsultaPastParticiple.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaFormaVerbos.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaPastForm.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaPastParticiple.setVisibility(android.view.View.VISIBLE);

				}
				//OBSERVAÇÂO
				//etCadastroConsultaGrupo.setText(cursorPalavras.getString(cursorPalavras.getColumnIndex("grupoID")));
				//OBSERVAÇÃO
			/*	classesExemplos.clear();
				classesExemplos = new ArrayList<String>
				(Arrays.asList(cursorPalavras.getString(cursorPalavras.getColumnIndex("frases")).split("#$")));				
				criarLista();*/
				break;
			}				
			cursorPalavras.moveToNext();
		}
		desabilitarCampos();
	}

	private void buscarDados() {
		try {
			   cursorPalavras = bancoDados.query(tabelaPalavras, camposTabelaPalavras, 
					   null,//selection, 
					   null,//selectionArgs, 
					   null,//groupBy, 
					   null,//having, 
					   null,//"order by palavra"//orderBy)
					   null); // Limite de registros retornados	
			   if (cursorPalavras.getCount() > 0){
				   cursorPalavras.moveToFirst();
			   }
			   cursorGrupos = bancoDados.query(tabelaGrupos, camposTabelaGrupos, 
					   null, null, null, null, null, null);
			   if (cursorGrupos.getCount() > 0){
				   cursorGrupos.moveToFirst();
			   }
			   cursorGruposPalavras = bancoDados.query(tabelaGruposPalavras, camposTabelaGruposPalavras, 
					   null, null, null, null, null, null);
			   if (cursorGruposPalavras.getCount() > 0){
				   cursorGruposPalavras.moveToFirst();
			   }
			   cursorFrases = bancoDados.query(tabelaFrases, camposTabelaFrases, 
					   null, null, null, null, null, null);
			   if (cursorFrases.getCount() > 0){
				   cursorFrases.moveToFirst();
			   }
			  
			   
		   } catch(Exception erro) {
			     Toast.makeText(CadastroConsulta_sem_Pojo.this, "Erro buscar dados no banco: "+erro.getMessage(), Toast.LENGTH_LONG).show();	    
			     //return false;
		   }
		
	}

	private void desabilitarCampos() {
		etCadastroConsultaPalavras.setEnabled(false);
		etCadastroConsultaDefinicao.setEnabled(false);
		etCadastroConsultaClasses.setEnabled(false);
		etCadastroConsultaTraducao.setEnabled(false);
		etCadastroConsultaPastForm.setEnabled(false);
		etCadastroConsultaPastParticiple.setEnabled(false);
		etCadastroConsultaGrupo.setEnabled(false);
		etCadastroConsultaAddExemplo.setEnabled(false);
		etCadastroConsultaAddExemplo.setVisibility(android.view.View.GONE);
		btCadastroConsultaAddExemplo.setEnabled(false);
		btCadastroConsultaAddExemplo.setVisibility(android.view.View.GONE);
		cbCadastroConsultaIrregular.setEnabled(false);
		lvCadastroConsultaExemplos.setEnabled(false);
		
		etCadastroConsultaPalavras.setTextColor(Color.GRAY);
		etCadastroConsultaDefinicao.setTextColor(Color.GRAY);	
		etCadastroConsultaClasses.setTextColor(Color.GRAY);
		etCadastroConsultaTraducao.setTextColor(Color.GRAY);
		etCadastroConsultaPastForm.setTextColor(Color.GRAY);
		etCadastroConsultaPastParticiple.setTextColor(Color.GRAY);
		etCadastroConsultaGrupo.setTextColor(Color.GRAY);
	}

	private void listener() {
		etCadastroConsultaPalavras.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus){
					buscarDados();
					ArrayList<String> palavrasCadastradas = new ArrayList<String>();
					for (int i=0; i < cursorPalavras.getCount(); i++){
						palavrasCadastradas.add(cursorPalavras.getString(cursorPalavras.getColumnIndex("palavra")).toLowerCase(Locale.ENGLISH));
						cursorPalavras.moveToNext();
					}
					if(!atualizando){
						if (palavrasCadastradas.contains(etCadastroConsultaPalavras.getText().toString().toLowerCase(Locale.ENGLISH))){
							etCadastroConsultaPalavras.setError("Palavra ja cadastrada!");
							etCadastroConsultaPalavras.setTextColor(Color.RED);
							erroPalavra=true;
						}  else {
							erroPalavra=false;
							etCadastroConsultaPalavras.setTextColor(Color.BLACK);
						}
					}
					cursorPalavras.moveToFirst();
				}
				
			}
		});
		etCadastroConsultaClasses.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				pegarClassesDefinidas(true);
				abrePopupClasse("Classes Gramaticais", classesGramaticais, classesDefinidasBoolean, "edittext");		
			}
		});
		
		btCadastroConsultaAddExemplo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!etCadastroConsultaAddExemplo.getText().toString().equals("")){
					pegarClassesDefinidas(false);
					classesExemplosString = new String[classesSelecionadas.size()]; //Converte o Array List em String[] para usar no método
							classesExemplosString = classesSelecionadasAntes.toArray(classesExemplosString);
					abrePopupClasse("Escolha em qual classe deseja adicionar o exemplo", 
							classesExemplosString, classesDefinidasBoolean, "exemplos");
				}
			}
		});
		etCadastroConsultaGrupo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder opcoes = new AlertDialog.Builder(CadastroConsulta_sem_Pojo.this);
				opcoes.setTitle("Selecione o(s) Grupo(s)!");
				
				/*opcoes.setView(et);
				opcoes.setMultiChoiceItems(items, checkedItems, listener)*/
				opcoes.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				opcoes.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();	
					}
				});
				opcoes.show();	
			}
		});
		cbCadastroConsultaIrregular.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (cbCadastroConsultaIrregular.isChecked()){
					etCadastroConsultaPastForm.setVisibility(android.view.View.VISIBLE);
					etCadastroConsultaPastParticiple.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaFormaVerbos.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaPastForm.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaPastParticiple.setVisibility(android.view.View.VISIBLE);
				} else {
					etCadastroConsultaPastForm.setVisibility(android.view.View.GONE);
					etCadastroConsultaPastParticiple.setVisibility(android.view.View.GONE);
					tvCadastroConsultaFormaVerbos.setVisibility(android.view.View.GONE);
					tvCadastroConsultaPastForm.setVisibility(android.view.View.GONE);
					tvCadastroConsultaPastParticiple.setVisibility(android.view.View.GONE);
				}
			}
		});
		lvCadastroConsultaExemplos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					final int posicao = position;
					AlertDialog.Builder opcoes = new AlertDialog.Builder(CadastroConsulta_sem_Pojo.this);
					opcoes.setTitle("Edite a Frase?");
					opcoes.setMessage("Se quiser APAGAR o item basta clickar e segurar.");
					
					final EditText et = new EditText(CadastroConsulta_sem_Pojo.this);
					String frase = classesExemplos.get(position).toString();
					ArrayList<String> classeFrase = new ArrayList<String>
					(Arrays.asList(frase.split(": \n")));
					et.setText(classeFrase.get(1).toString());
					
					opcoes.setView(et);
					opcoes.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (!et.getText().toString().equals("")){								
								classesExemplos.remove(posicao);
								classesExemplos.add(classesSelecionadasAntes.get(posicao)+": \n"
										+et.getText().toString());
								criarListaExemplo();
								Toast.makeText(CadastroConsulta_sem_Pojo.this, "Exemplo editado com sucesso!", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(CadastroConsulta_sem_Pojo.this, "Nenhum Exemplo modificado. Campo não pode ficar vazio!", Toast.LENGTH_SHORT).show();
							}
						}
					});
					opcoes.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();	
						}
					});
					opcoes.show();				
			}
		});
		lvCadastroConsultaExemplos.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int posicao2 = position;
				AlertDialog.Builder opcoes = new AlertDialog.Builder(CadastroConsulta_sem_Pojo.this);
				opcoes.setTitle("Deseja apagar o item?");
				opcoes.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						classesExemplos.remove(posicao2);
						criarListaExemplo();
						Toast.makeText(CadastroConsulta_sem_Pojo.this, "Exemplo removido com sucesso!", Toast.LENGTH_SHORT).show();
					}
				});
				opcoes.setNegativeButton("Não", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();	
					}
				});
				opcoes.show();				
				return true;
			}
		});
	}

	private void preencheEditTextClasses(ArrayList<Integer> classes){
		String classesFinal="";
		Collections.sort(classes);
		if (!classes.isEmpty()){
			etCadastroConsultaAddExemplo.setEnabled(true);
			btCadastroConsultaAddExemplo.setEnabled(true);
			for (int i=0; i < classes.size();i++){ //Monta String pro EditText
				if (classesGramaticais[Integer.valueOf(classes.get(i))].equals("Verb"))
					cbCadastroConsultaIrregular.setVisibility(android.view.View.VISIBLE);
				classesFinal += classesGramaticais[Integer.valueOf(classes.get(i))];
				classesFinal +=", ";
			}
		} else {
			classesFinal +=", ";
		}
		etCadastroConsultaClasses.setText(classesFinal.substring(0, classesFinal.length()-2));
	}
	
	private void abrePopupClasse(String titulo, String[] classes, boolean[] marcados, final String qual) {// Cria popup de opções de classes e exemplos
		AlertDialog.Builder popupClasses = new AlertDialog.Builder(CadastroConsulta_sem_Pojo.this); 
		popupClasses.setTitle(titulo);
		popupClasses.setMultiChoiceItems(classes, marcados, 
				new OnMultiChoiceClickListener() {					

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if (isChecked) {
                    classesSelecionadas.add(which);
                    if (qual.equals("exemplos")){
	            	 	criaCamposExemplo(classesSelecionadas); //Cria a lista com exemplos
	            	 	dialog.cancel(); //Selecionar apenas a classe equivalente.
                    }
                 } else if (classesSelecionadas.contains(which)) { 
                   	 classesSelecionadas.remove(Integer.valueOf(which));	        	         
                 }
			}
		});
		if (qual.equals("edittext")){
			popupClasses.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	             @Override
	             public void onClick(DialogInterface dialog, int id) {
	            	 preencheEditTextClasses(classesSelecionadas); //Coloca as Classes Gramaticasi no EditText
	             }
	         });
	        popupClasses.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	             @Override
	             public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();	                   
	             }
	         });
		}
		popupClasses.show();		
	}
	
	private void pegarClassesDefinidas(boolean run){
		classesSelecionadas.clear(); // Apaga o vetor possibilitando seu uso para os 2 AlertDialog com Checkbox
		classesDefinidasBoolean = new boolean[classesGramaticais.length]; // Cria o array booleano para passar ao contrutor do popupClasses.setMultiChoiceItems
		String classesDefininasNome = etCadastroConsultaClasses.getText().toString();
		if (!classesDefininasNome.equals("")){ //Se o Edit Text Estiver vazio não precisa montar array booleano 
			classesSelecionadasAntes = new ArrayList<String>
												(Arrays.asList(classesDefininasNome.split(", ")));
			if (run){ //Vai rodar somente quanto for necessário
				for (int j=0; j < classesGramaticais.length;j++){
					classesDefinidasBoolean[j] = false;	
					for (int i=0; i < classesSelecionadasAntes.size();i++){	
						if (classesSelecionadasAntes.get(i).equals(classesGramaticais[j])){
							classesDefinidasBoolean[j] = true; // Define true as classes presentes para marcar o checkbox
							classesSelecionadas.add(j); //reconstroi o vetor zerado pelo pegarClassesDefinidas() para sempre manter o valor correto
						}
					}
				}
			}
		}
	}
	
	private void criaCamposExemplo(ArrayList<Integer> classes) { //Monta a lista dos Exemplos.
		if (!classes.isEmpty()){
			
			classesExemplos.add(classesSelecionadasAntes.get(classes.get(0))+": \n"
								+etCadastroConsultaAddExemplo.getText().toString());	
			criarListaExemplo();
			etCadastroConsultaAddExemplo.setText("");
			Toast.makeText(CadastroConsulta_sem_Pojo.this, "Exemplo adicionado com sucesso!", Toast.LENGTH_SHORT).show();
		}	
	}
	
	private void criarListaExemplo(){
		Collections.sort(classesExemplos); //Ordena para deixar todos agrupados.
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
							android.R.layout.simple_list_item_1, classesExemplos);
		lvCadastroConsultaExemplos.setAdapter(adapter);
		getListViewSize(lvCadastroConsultaExemplos); // Define tamanho da lista.		
	}
	
    public static void getListViewSize(ListView myListView) { //Redefine tamanho da Lista para Usa-la em ScrollView
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }
    
    public void abreouCriaBanco() {
		 try {		   
			   //cria ou abre o banco de dados
			   bancoDados = openOrCreateDatabase(nomeBanco, MODE_PRIVATE, null);
		   }
		   catch(Exception erro)
		   {
			   Log.i("Erro Banco", "Erro ao abrir ou criar o banco: "+erro.getMessage());
		   }
	}
    
    public void gravarDados(){
    	try {
			   String sqlPalavra="INSERT INTO palavras (palavra, definicao, classes, traducao, irregular, pastform, pastparticiple) values ('"+
					   		etCadastroConsultaPalavras.getText().toString()+
					   		"', '"+etCadastroConsultaDefinicao.getText().toString()+
					   		"', '"+etCadastroConsultaClasses.getText().toString()+
					   		"', '"+etCadastroConsultaTraducao.getText().toString()+
					   		"', '"+cbCadastroConsultaIrregular.isChecked()+
					   		"', '"+etCadastroConsultaPastForm.getText().toString()+
					   		"', '"+etCadastroConsultaPastParticiple.getText().toString()+"')";		   
			   bancoDados.execSQL(sqlPalavra);	
			   Toast.makeText(CadastroConsulta_sem_Pojo.this, "Dados Gravados com Sucesso!", Toast.LENGTH_LONG).show();
		 } catch(Exception erro) {
			   Toast.makeText(CadastroConsulta_sem_Pojo.this, "Erro ao gravar dados no banco: "+erro.getMessage(), Toast.LENGTH_LONG).show();
			  
		 }
    }
    
    public void atualizarDados(){
    	try {
			   String sqlPalavra="UPDATE palavras set palavra='"+etCadastroConsultaPalavras.getText().toString()+"', " +
			   								  "definicao='"+etCadastroConsultaDefinicao.getText().toString()+"', "+
			   								  "classes='"+etCadastroConsultaClasses.getText().toString()+"', "+
			   								  "traducao='"+etCadastroConsultaTraducao.getText().toString()+"', "+
			   								  "irregular='"+cbCadastroConsultaIrregular.isChecked()+"', "+
			   								  "pastform='"+etCadastroConsultaPastForm.getText().toString()+"', "+
			   								  "pastparticiple='"+etCadastroConsultaPastParticiple.getText().toString()+"' WHERE  idPalava='"+idPalava+"';";
			   								  
			   bancoDados.execSQL(sqlPalavra);	
			   Toast.makeText(CadastroConsulta_sem_Pojo.this, "Dados Atualizados com Sucesso!", Toast.LENGTH_LONG).show();
		   }
		   catch(Exception erro) {
			   Toast.makeText(CadastroConsulta_sem_Pojo.this, "Erro ao ataualizar dados no banco: "+erro.getMessage(), Toast.LENGTH_LONG).show();
			   Log.i("Erro", ""+erro.getMessage());
			  
		   }
    }
    
    public void habilitarCampos(){
    	etCadastroConsultaPalavras.setEnabled(true);
		etCadastroConsultaDefinicao.setEnabled(true);
		etCadastroConsultaClasses.setEnabled(true);
		etCadastroConsultaTraducao.setEnabled(true);
		etCadastroConsultaPastForm.setEnabled(true);
		etCadastroConsultaPastParticiple.setEnabled(true);
		etCadastroConsultaGrupo.setEnabled(true);
		etCadastroConsultaAddExemplo.setEnabled(true);
		etCadastroConsultaAddExemplo.setVisibility(android.view.View.VISIBLE);
		btCadastroConsultaAddExemplo.setEnabled(true);
		btCadastroConsultaAddExemplo.setVisibility(android.view.View.VISIBLE);
		cbCadastroConsultaIrregular.setEnabled(true);
		lvCadastroConsultaExemplos.setEnabled(true);
		
		etCadastroConsultaPalavras.setTextColor(Color.BLACK);
		etCadastroConsultaDefinicao.setTextColor(Color.BLACK);	
		etCadastroConsultaClasses.setTextColor(Color.BLACK);
		etCadastroConsultaTraducao.setTextColor(Color.BLACK);
		etCadastroConsultaPastForm.setTextColor(Color.BLACK);
		etCadastroConsultaPastParticiple.setTextColor(Color.BLACK);
		etCadastroConsultaGrupo.setTextColor(Color.BLACK);
		
		Toast.makeText(CadastroConsulta_sem_Pojo.this, "Você pode editar os campos agora. ", Toast.LENGTH_LONG).show();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.main, menu);		
		add = menu.findItem(R.id.menu_add);
		save = menu.findItem(R.id.menu_save);
		edit = menu.findItem(R.id.menu_edit);
		cancel = menu.findItem(R.id.menu_cancel);
		add.setVisible(false);
		cancel.setVisible(false);
		save.setVisible(false);
			
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
			Intent iTelaCadastroConsulta = new Intent(this, CadastroConsulta_sem_Pojo.class);
			startActivity(iTelaCadastroConsulta);
			return true;
		}
		if (id == R.id.menu_edit){
			atualizando=true;
			edit.setVisible(false);
			save.setVisible(true);
			cancel.setVisible(true);
			habilitarCampos();
			return true;
		}
		if (id == R.id.menu_save){
			if (!etCadastroConsultaPalavras.getText().toString().equals("")){
				if (!erroPalavra){
					if (atualizando){
						atualizarDados();
					} else {			
						gravarDados();
					}
					edit.setVisible(true);
					cancel.setVisible(false);
					save.setVisible(false);
					desabilitarCampos();
				} else {
					Toast.makeText(CadastroConsulta_sem_Pojo.this, "Corriga os erros antes de salvar!", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(CadastroConsulta_sem_Pojo.this, "O campo palavra não pode estar vazio!", Toast.LENGTH_LONG).show();
			}
			return true;
		}
		if (id == R.id.menu_cancel){
			atualizando=false;
			edit.setVisible(true);
			save.setVisible(false);
			desabilitarCampos();
			cancel.setVisible(false);
			return true;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onPrepareOptionsMenu (Menu menu){
		Intent IDadosRecebidos = getIntent();
		if (IDadosRecebidos.getStringExtra("Palavra") != null){	
			if(!save.isVisible()){
				edit.setVisible(true);
				save.setVisible(false);
			}
		} else {
			save.setVisible(true);
			edit.setVisible(false);
		}
		
		return true;		
	}
}
