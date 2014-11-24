package com.diegobonfim.priword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;


public class CadastroConsulta extends Activity{
	
	ArrayList<Integer> classesSelecionadas = new ArrayList<Integer>();
	ArrayList<String> classesSelecionadasAntes = null;
	ArrayList<String> classesExemplos = new ArrayList<String>();
	ArrayList<String> arrayFrases = new ArrayList<String>();
	ArrayList<ArrayAdapter<String>> arrayAdaptador = null;
	ArrayList<ArrayList<String>> arrayClassesFrase = null;
	
	boolean[] classesDefinidasBoolean = null;
	EditText etCadatroConsultaClasses, etCadastroConsultaPalavras, etCadastroConsultaAddExemplo;
	Button btCadastroConsultaAddExemplo;

	ExpandableListView elvCadastroConsultaExemplosClasses;
	String[] classesExemplosString = null;
	String[][]	frasesExemplosString = null;
	String[] classesGramaticais = new String[] {"Adjective", "Adverb", "Article", "Conjunction", "Interjection", "Noun"
			, "Number",	"Phrasal Verb", "Postposition", "Preosition", "Pronoun", "Substantive",	"Verb"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadastro_consulta);
		
		elvCadastroConsultaExemplosClasses = (ExpandableListView)findViewById(R.id.cadastro_consulta_elvExemplosClasses);
		etCadatroConsultaClasses = (EditText) findViewById(R.id.cadastro_consulta_etClasses);
		etCadastroConsultaAddExemplo = (EditText) findViewById(R.id.cadastro_consulta_etAddExemplo);
		btCadastroConsultaAddExemplo = (Button) findViewById(R.id.cadastro_consulta_btAddExemplo);
		etCadastroConsultaAddExemplo.setEnabled(false);
		btCadastroConsultaAddExemplo.setEnabled(false);;
				
		listener();
	}
	
	private void listener() {
		etCadatroConsultaClasses.setOnClickListener(new OnClickListener() {
		
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
					arrayFrases.add(etCadastroConsultaAddExemplo.getText().toString());
					pegarClassesDefinidas(false);
					classesDefinidasBoolean = new boolean[classesSelecionadasAntes.size()];
					classesExemplosString = new String[classesSelecionadas.size()]; //Converte o Array List em String[] para usar no método
							classesExemplosString = classesSelecionadasAntes.toArray(classesExemplosString);
					abrePopupClasse("Escolha em qual classe deseja adicionar o exemplo", 
							classesExemplosString, classesDefinidasBoolean, "exemplos");
				}
			}
		});
			
			
	}

	private void preencheEditTextClasses(ArrayList<Integer> classes){
		String classesFinal="";
		Collections.sort(classes);
		if (!classes.isEmpty()){
			etCadastroConsultaAddExemplo.setEnabled(true);
			btCadastroConsultaAddExemplo.setEnabled(true);
			arrayClassesFrase = new ArrayList<ArrayList<String>>();
			for (int i=0; i < classes.size();i++){
				
				//for (int i=0; i < classesSelecionadasAntes.size();i++){
					arrayClassesFrase.add(new ArrayList<String>());
			//	}
				classesFinal += classesGramaticais[Integer.valueOf(classes.get(i))];
				classesFinal +=", ";
			}
		} else {
			classesFinal +=", ";
		}
		etCadatroConsultaClasses.setText(classesFinal.substring(0, classesFinal.length()-2));
	}
	
	private void abrePopupClasse(String titulo, String[] classes, boolean[] marcados, final String qual) {// Cria popup de opções de classes e exemplos
		AlertDialog.Builder popupClasses = new AlertDialog.Builder(CadastroConsulta.this); 
		popupClasses.setTitle(titulo);
		popupClasses.setMultiChoiceItems(classes, marcados, 
				new OnMultiChoiceClickListener() {					

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if (isChecked) {
                    classesSelecionadas.add(which);
                    if (qual.equals("exemplos")){
	            	 	criaCamposExemplo(classesSelecionadas);
	            	 	dialog.cancel();
                    }
                 } else if (classesSelecionadas.contains(which)) { 
                   	 classesSelecionadas.remove(Integer.valueOf(which));	        	         
                 }
			}
		});
		if (qual.equals("edittext")){
			popupClasses.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	             @Override
	             public void onClick(DialogInterface dialog, int id) { //Chamará o método dependendo da razão pela qual foi chamada
	            	 preencheEditTextClasses(classesSelecionadas);
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
		String classesDefininasNome = etCadatroConsultaClasses.getText().toString();
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
	
	private void criaCamposExemplo(ArrayList<Integer> classes) {
		if (!classes.isEmpty()){			
			if (!classesExemplos.contains(classesSelecionadasAntes.get(classes.get(0)))){
					classesExemplos.add(classesSelecionadasAntes.get(classes.get(0)));
			}

			for (int i=0; i < classesSelecionadasAntes.size();i++){
				if (i == classes.get(0)){
					arrayClassesFrase.get(i).add(etCadastroConsultaAddExemplo.getText().toString());
					Log.i("Classe: ", ""+classesSelecionadasAntes.get(i));
					for (int j=0; j < arrayClassesFrase.get(i).size(); j++)
					Log.i("Frase Aramazenada", ""+arrayClassesFrase.get(i).get(j));
				}
			}
			final String[] listaPai = { "Categoria 1", "Categoria 2", "Categoria 3" };
			final String[][] listafilho = { { "Subcategoria 1", "Subcategoria 1.2" },
			{ "Subcategoria 2" }, { "Subcategoria 3" } };
			BaseExpandableListAdapter expandableAdapter = new BaseExpandableListAdapter() {
				
				/*@Override
				public boolean isChildSelectable(int groupPosition, int childPosition) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean hasStableIds() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public View getGroupView(int groupPosition, boolean isExpanded,
						View convertView, ViewGroup parent) {
						TextView textViewCategorias = new TextView(CadastroConsulta.this);
						textViewCategorias.setText(classesExemplos.get(groupPosition).toString());
						textViewCategorias.setPadding(30, 5, 0, 5);
						textViewCategorias.setTextSize(20);
						textViewCategorias.setTypeface(null, Typeface.BOLD);
					return textViewCategorias;
				}
				
				@Override
				public long getGroupId(int groupPosition) {
					// TODO Auto-generated method stub
					return groupPosition;
				}
				
				@Override
				public int getGroupCount() {
					// TODO Auto-generated method stub
					return classesExemplos.size();
				}
				
				@Override
				public Object getGroup(int groupPosition) {
					// TODO Auto-generated method stub
					return classesExemplos.get(groupPosition);
				}
				
				@Override
				public int getChildrenCount(int groupPosition) {
					// TODO Auto-generated method stub
					return arrayClassesFrase.get(groupPosition).size();
				}
				
				@Override
				public View getChildView(int groupPosition, int childPosition,
						boolean isLastChild, View convertView, ViewGroup parent) {
						TextView textViewSubLista = new TextView(CadastroConsulta.this);
						textViewSubLista.setText(arrayClassesFrase.get(groupPosition).get(childPosition).toString());
						textViewSubLista.setPadding(10, 5, 0, 5);
					return textViewSubLista;
				}
				
				@Override
				public long getChildId(int groupPosition, int childPosition) {
					// TODO Auto-generated method stub
					return childPosition;
				}
				
				@Override
				public Object getChild(int groupPosition, int childPosition) {
					// TODO Auto-generated method stub
					return arrayClassesFrase.get(groupPosition).get(childPosition);
				}*/
				@Override
				public Object getChild(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return listafilho[groupPosition][childPosition];
				}
				 
				@Override
				public long getChildId(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return childPosition;
				}
				 
				@Override
				public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
				 
				// Criamos um TextView que conterá as informações da listafilho que
				// criamos
				TextView textViewSubLista = new TextView(CadastroConsulta.this);
				textViewSubLista.setText(listafilho[groupPosition][childPosition]);
				// Definimos um alinhamento
				textViewSubLista.setPadding(10, 5, 0, 5);
				 
				return textViewSubLista;
				}
				 
				@Override
				public int getChildrenCount(int groupPosition) {
				// TODO Auto-generated method stub
				return listafilho[groupPosition].length;
				}
				 
				@Override
				public Object getGroup(int groupPosition) {
				// TODO Auto-generated method stub
				return listaPai[groupPosition];
				}
				 
				@Override
				public int getGroupCount() {
				// TODO Auto-generated method stub
				return listaPai.length;
				}
				 
				@Override
				public long getGroupId(int groupPosition) {
				// TODO Auto-generated method stub
				return groupPosition;
				}
				 
				@Override
				public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
				 
				// Criamos um TextView que conterá as informações da listaPai que
				// criamos
				TextView textViewCategorias = new TextView(CadastroConsulta.this);
				textViewCategorias.setText(listaPai[groupPosition]);
				// Definimos um alinhamento
				textViewCategorias.setPadding(30, 5, 0, 5);
				// Definimos o tamanho do texto
				textViewCategorias.setTextSize(20);
				// Definimos que o texto estará em negrito
				textViewCategorias.setTypeface(null, Typeface.BOLD);
				 
				return textViewCategorias;
				}
				 
				@Override
				public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return false;
				}
				 
				@Override
				public boolean isChildSelectable(int groupPosition, int childPosition) {
				// Defina o return como sendo true se vc desejar que sua sublista seja selecionável
				return false;
				}
			};
						
			elvCadastroConsultaExemplosClasses.setAdapter(expandableAdapter);
			etCadastroConsultaAddExemplo.setText("");
		}
	}
}
