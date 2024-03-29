
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;


/***
@author Dilan Steven Mejia Buitrago - mejiadilan@hotmail.com
Clase que permite sacar todos los posibles caminos de un BPM con su información estructural y textual.

Copyright (C) <2019>  <Dilan Steven Mejia Buitrago>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>


**/

public class Main 
{

	private static int countRoute=0;
	private static String [][] arregloTextual = null;
	private static String [][] arregloEstructural = null;
	private static String[] arregloRoute =null;
	private static int profundidad = 0;
	private static int indice = 0;

	private static String[][] arregloTransitions = null;

	private static  int posicionVector = 0;


	public static void main( String[] args )
	{	
		String idInicio = "";
		try {
			long time_start, time_end;
			time_start = System.currentTimeMillis();

			//Obtener data textual
			obtenerEstructuraTextual(new File("C:\\Users\\dmejia\\Desktop\\Data\\1x Manage LAGs in belongings.xpdl"));
			//Obtener data estructural
			obtenerDatosEstructurales(new File("C:\\Users\\dmejia\\Desktop\\Data\\1x Manage LAGs in belongings.xpdl"));

			arregloRoute = new String [countRoute*2];

			//Arreglo que me permite guardar todos los caminos posibles
			arregloTransitions = new String [(arregloEstructural.length)][(arregloEstructural.length)];

			for (int i = 0; i < arregloTextual.length; i++) {

				//Si es Start event lo guarndo, ya que es la primera transition
				if(arregloTextual[i][1].equalsIgnoreCase("StartEvent")==true){
					idInicio =arregloTextual[i][3];
				}

			}

			int posicion = 0;
			//Ingreso las transiciones de los router para saber si son de a uno o dos  
			for (int i = 0; i < arregloEstructural.length; i++) {

				for (int j = 0; j < arregloTextual.length; j++) {

					if(arregloTextual[j][3]!=null && arregloEstructural[i][1] !=null && 
							arregloTextual[j][3].equals(arregloEstructural[i][1])==true
							&& arregloTextual[j][1].equalsIgnoreCase("Route")==true){

						//Guardo los routers en una matriz route 
						arregloRoute[posicion]=arregloTextual[j][3];
						posicion++;

					}


				}

			}


			obtenerHijos(idInicio,0);

			for (int i = 0; i < arregloTransitions.length; i++) {
				for (int j = 0; j < arregloTransitions.length; j++) {
					if(arregloTransitions[i][j]!=null){
						for (int j2 = 0; j2 < arregloTextual.length; j2++) {
							if(arregloTransitions[i][j].trim().equalsIgnoreCase(arregloTextual[j2][3].trim())==true
									){
								System.out.println(arregloTextual[j2][0] + " "+ i);

							}
						}
					}

				}
			}

			time_end = System.currentTimeMillis();
			System.out.println("the task has taken "+ ( time_end - time_start ) +" milliseconds");

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Obtener datos textuales del BPM
	 * @author Dilan Steven Mejia Buitrago
	 * @param File archivo xpdl a procesar 
	 * @return String [][] Retorna la matriz con los caminos 
	 * */

	private static  String [][] obtenerEstructuraTextual(File arcFile) throws JDOMException, IOException {
		//Setup para acceder al xpdl 
		SAXBuilder builder = new SAXBuilder();
		Document doc = (Document) builder.build(arcFile);

		//Obtengo la raiz del documento y guardo sus hijos en una lista.
		Element raiz = doc.getRootElement();
		List<Element> hijosRaiz = raiz.getChildren();

		for (int i = 0; i < hijosRaiz.size(); i++) {
			Element nodo = hijosRaiz.get(i);
			List<Element> hijosNodo = nodo.getChildren();
			//Si hay algo en el documento
			if (hijosNodo.size() > 0) {

				for (int j = 0; j < hijosNodo.size(); j++) {
					Element nodoNIvel2 = hijosNodo.get(j);
					//Filtrar por Activities
					List<Element> hijosNodonivel2 = nodoNIvel2.getChildren("Activities",Namespace.getNamespace("http://www.wfmc.org/2009/XPDL2.2"));

					for (int k = 0; k < hijosNodonivel2.size(); k++) {
						Element nodoNIvel3 = hijosNodonivel2.get(k);
						//Filtrar por Activity
						List<Element> hijosNodonivel3 = nodoNIvel3.getChildren("Activity",Namespace.getNamespace("http://www.wfmc.org/2009/XPDL2.2"));
						//Arreglo textual que va contener los datos.
						arregloTextual = new String[hijosNodonivel3.size()][5];
						//Nombre de las actividades
						String[] nombreActividad = {
								"RouteComplex", "MarkerVisible", "RouteParallel", 
								"RouteInclusive", "ResultError", "TriggerTimer", 
								"TaskService", "Task", "StartEvent", 
								"Route", "EndEvent", "IntermediateEvent", 
								"TriggerResultMessage", "TaskUser", 
								"TaskManual", "BlockActivity", "TriggerResultSignal",
								"TaskScript", "TriggerConditional", "TriggerEscalation"
						};
						//Guardo los datos textuales
						for (int l = 0; l < hijosNodonivel3.size(); l++) {
							Element nodoNIvel4 = hijosNodonivel3.get(l);
							List<Element> hijosNodonivel4 = nodoNIvel4.getChildren();

							for (int n = 0; n < hijosNodonivel3.get(l).getChildren().size(); n++) {
								if(hijosNodonivel3.get(l).getChildren().get(n).getName().equalsIgnoreCase("Route")==true){
									arregloTextual[l][1] = hijosNodonivel3.get(l).getChildren().get(n).getName();
									countRoute++;
								}


								//Busco los tipo de actividades
								for (int typeActivity = 0; typeActivity < hijosNodonivel4.size(); typeActivity++) {
									//Verifico que no este vacio	
									if(!hijosNodonivel4.get(typeActivity).getChildren().isEmpty()){
										//Recorro el arreglo de actividades que existen 
										for (int m = 0; m < nombreActividad.length; m++) {
											//Comparo el tipo de actividad que encuentro con el del arreglo de tipo de actividades
											if(hijosNodonivel4.get(typeActivity).getChildren().get(0).getName().
													equals(nombreActividad[m])==true){
												//Si encuentro un tipo de actividad lo guardo
												arregloTextual[l][1] = nombreActividad[m];

											}
										}

									}

								}

							}


							//Obtengo el nombre de la actividad
							arregloTextual[l][0] = hijosNodonivel3.get(l).getAttributeValue("Name");
							//Obtengo la descripcion de la actividad
							//arregloTextual[l][2] = hijosNodonivel3.get(l).getChildren("Description",Namespace.getNamespace("http://www.wfmc.org/2009/XPDL2.2")).get(0).getTextTrim();
							/*Guardo las transiciones para cada actividad para despues mapearla
                        	este dato no se usa para la busqueda textual, pero si para la estructural*/
							arregloTextual[l][3] = hijosNodonivel3.get(l).getAttributeValue("Id");



						}
					}
				}
			}
		}

		return arregloTextual;
	}


	/**
	 * Obtener datos estructurales del BPM
	 * @author Dilan Steven Mejia Buitrago
	 * @param File archivo xpdl a procesar 
	 * @return String [][] Retorna la matriz con los caminos 
	 * */

	private static  String [][] obtenerDatosEstructurales(File arcFile) throws JDOMException, IOException {
		//Setup para acceder al xpdl 
		SAXBuilder builder = new SAXBuilder();
		Document doc = (Document) builder.build(arcFile);

		//Obtengo la raiz del documento y guardo sus hijos en una lista.
		Element raiz = doc.getRootElement();
		List<Element> hijosRaiz = raiz.getChildren();

		//
		for (int i = 0; i < hijosRaiz.size(); i++) {
			Element nodo = hijosRaiz.get(i);
			List<Element> hijosNodo = nodo.getChildren();
			//Si hay algo en el documento
			if (hijosNodo.size() > 0) {

				for (int j = 0; j < hijosNodo.size(); j++) {
					Element nodoNIvel2 = hijosNodo.get(j);
					//Filtrar por Transitions
					List<Element> hijosNodonivel2 = nodoNIvel2.getChildren("Transitions",Namespace.getNamespace("http://www.wfmc.org/2009/XPDL2.2"));

					for (int k = 0; k < hijosNodonivel2.size(); k++) {
						Element nodoNIvel3 = hijosNodonivel2.get(k);
						//Filtrar por Transition
						List<Element> hijosNodonivel3 = nodoNIvel3.getChildren("Transition",Namespace.getNamespace("http://www.wfmc.org/2009/XPDL2.2"));
						//Arreglo estructural que va contener los datos.
						arregloEstructural = new String[hijosNodonivel3.size()][3];

						//Guardo los datos textuales
						for (int l = 0; l < hijosNodonivel3.size(); l++) {

							//Obtengo el nombre de la actividad
							arregloEstructural[l][0] = hijosNodonivel3.get(l).getAttributeValue("Id");
							arregloEstructural[l][1] = hijosNodonivel3.get(l).getAttributeValue("From");
							arregloEstructural[l][2] = hijosNodonivel3.get(l).getAttributeValue("To");

						}
					}
				}
			}
		}

		return arregloEstructural;
	}


	/**
	 * Obtener todos los comaninos posibles para un workflow dado un o muchos  start events
	 * @author Dilan Steven Mejia Buitrago
	 * @param id Este parametro va ser el nodo que se va estar buscando hasta que termina el camino,al principio por lo general se ingresa el start event
	 * @param arregloTransicion Este parametro  es la la matriz  de transiciones obtenidos en el xpdl
	 * @param indice Este patarametro es el contador que aumenta a medida que se van ingresando nodos en una iteracion del metodo ( es decir indice +1 )
	 * @param arregloEstructural este parametro va ser la matriz que contenga los caminos del workflow 
	 * @param posicionVector Este parametro permite llevar la cuenta de cada uno de los nuevos caminos que aparecen. es decir (posicionVector +1)
	 * @return String [][] Retorna la matriz con los caminos 
	 * */

	private static  void obtenerHijos(String arregloCompuertas,int indice){
		/*Busco primero el eventStart  y luego en la siguiente iteracion el que le sigue hasta recorrer un camino*/
		int	indiceBifulcacion = 0;
		int contador = 0;

		//Itero para preguntar si es una compuerta para poder entrar
		for (int i = 0; i < arregloRoute.length; i++) {
			if(arregloCompuertas !=null && arregloRoute[i]!=null && arregloCompuertas.equals(arregloRoute[i])==true){
				contador++;
				indiceBifulcacion=indice-1;

			}
		}

		if( arregloCompuertas!=null ){
			indice++;
			arregloTransitions[posicionVector][indice-1] =arregloCompuertas;
			obtenerHijos(obtenerSiguienteNodo2(arregloCompuertas,0),indice);



			if(contador==2){

				for (int i = 0; i < indice-1; i++) {
					arregloTransitions[posicionVector+1][i] = arregloTransitions[posicionVector][i];
				}

				posicionVector++;
				arregloTransitions[posicionVector][indice-1] =arregloCompuertas;
				obtenerHijos(obtenerSiguienteNodo2(arregloCompuertas,1),indice);
			}


		}

	}


	private static  String obtenerSiguienteNodo2(String id,int bifulcacion){
		/*Busco primero el eventStart  y luego en la siguiente iteracion el que le sigue hasta recorrer un camino*/
		String nodo ="";
		int contador=0;
		String nodoRepetido="";
		for (int i = 0; i <arregloEstructural.length;i++) {
			if (arregloEstructural[i][1].equals(id)==true) {
				contador ++;

				if(contador==1){
					nodo = arregloEstructural[i][2];
				}

				if(contador==2){
					if(bifulcacion==1){
						nodoRepetido = arregloEstructural[i][2];

					}
				}


			}

		}
		if(nodoRepetido!=null && nodoRepetido!=""){
			return nodoRepetido;
		}

		if(nodo!=null && nodo!=""){
			return nodo;
		}


		return null;

	}



}
