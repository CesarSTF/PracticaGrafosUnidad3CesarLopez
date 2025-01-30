from flask import Blueprint, render_template, request, redirect, url_for, flash
import requests
import json

usuario = Blueprint('usuario', __name__)
URL = 'http://localhost:8080/api/usuario'

#####################################################################################
@usuario.route('/usuario')
def home_usuario():
    criterio = request.args.get('criterio') 
    tipo = request.args.get('tipo')  

    if criterio and tipo:
        return redirect(url_for('usuario.ordenar_usuarios', atributo=criterio, orden=tipo))

    try:
        headers = {'Authorization': 'public'}
        r = requests.get(f'{URL}/list', headers=headers)
        print(f"Enviando solicitud a {URL}/list con encabezados: {headers}")
        r.raise_for_status()
        data = r.json()

        return render_template('usuario/lista.html', lista=data["data"], 
                               criterio=criterio, tipo=tipo)
    except requests.RequestException as e:
        flash("Error al cargar la lista de usuarios.", "error")
        return render_template('usuario/lista.html', lista=[])
#####################################################################################
@usuario.route('/usuario/editar/<int:id>', methods=['GET'])
def editar_familia(id):
    try:
        r = requests.get(f"{URL}/get/{id}")
        r.raise_for_status()
        data = r.json()
        print(data)
        return render_template('usuario/editar.html', usuario=data["data"])
    except requests.RequestException as e:
        print(f"Error al obtener la usuario con ID {id}: {e}")
        flash("Error al cargar los datos de la usuario.", "error")
        return redirect(url_for('usuario.home_usuario'))
#####################################################################################
@usuario.route('/usuario/register', methods=['GET'])
def registrar():
    return render_template('usuario/registro.html')
#####################################################################################
@usuario.route('/usuario/save', methods=["POST"])
def guardar_familia():
    headers = {'Content-Type': 'application/json'}
    form = request.form
    try:
        dataF = {
            "apellido": form['apellido'],
            "nombre": form['nombre'],
            "correo":form['correo']
        }
        r = requests.post(f'{URL}/save', data=json.dumps(dataF), headers=headers)
        dat = r.json()
        print(dat)
        if r.status_code == 400 and "El correo ya existe" in dat.get("data", ""):
            flash("El correo ya está registrado. Intente con otro correo.", "error")
            return redirect(url_for('usuario.registrar'))

        if r.status_code == 200:
            flash("Usuario registrado exitosamente", "success")
            return redirect(url_for('usuario.home_usuario'))
        else:
            flash(f"Error: {dat.get('data', 'Error desconocido')}", "error")
            return redirect(url_for('usuario.registrar'))
    except requests.RequestException as e:
        print(f"Error al registrar la usuario: {e}")
        flash("Error al registrar la usuario. Intente nuevamente.", "error")
        return redirect(url_for('usuario.registrar'))
#####################################################################################
@usuario.route('/usuario/update', methods=['POST'])
def actualizar_familia():
    headers = {'Content-Type': 'application/json'}
    form = request.form
    try:
        dataF = {
            "id": form["id"],
            "apellido": form['apellido'],
            "nombre": int(form['nombre']),
            "correo": float(form['correo'])
        }
        r = requests.post(f'{URL}/update', data=json.dumps(dataF), headers=headers)
        dat = r.json()
        print(dat)
        flash("Familia actualizada exitosamente", "success")
        return redirect(url_for('usuario.home_usuario'))
    except requests.RequestException as e:
        print(f"Error al actualizar la usuario: {e}")
        flash("Error al actualizar la usuario. Intente nuevamente.", "error")
        return redirect(url_for('usuario.home_usuario'))
#####################################################################################
@usuario.route('/usuario/<int:id>/eliminar', methods=['GET'])
def eliminar_familia(id):
    try:
        r = requests.delete(f'{URL}/delete/{id}')
        r.raise_for_status()
        flash("Familia eliminada exitosamente", "success")
        return redirect(url_for('usuario.home_usuario'))
    except requests.RequestException as e:
        print(f"Error al eliminar la usuario con ID {id}: {e}")
        flash("Error al eliminar la usuario. Intente nuevamente.", "error")
        return redirect(url_for('usuario.home_usuario'))
#####################################################################################
@usuario.route('/usuario/order/<atributo>/<int:orden>', methods=['GET'])
def ordenar_usuarios(atributo, orden):
    try:
        url = f'{URL}/order/{atributo}/{orden}'
        r = requests.get(url)
        r.raise_for_status()  
        data = r.json()
        print(data)

        if r.status_code == 200:
            if not data.get("data"):
                flash("No se encontraron resultados para los criterios especificados.", "warning")
            
            # Pasar los valores actuales a la plantilla
            return render_template('/usuario/lista.html', lista=data.get("data", []),
                                   criterio=atributo, tipo=orden)
        else:
            flash(f"Error al ordenar las familias: {data.get('msg', 'Error desconocido')}", "error")
            return render_template('/usuario/lista.html', lista=[])

    except requests.RequestException as e:
        print(f"Error al ordenar familias: {e}")
        flash("Error al ordenar las familias. Intente nuevamente.", "error")
        return render_template('/usuario/lista.html', lista=[])
#######################################################################################3
@usuario.route('/usuario/search/<criterio>/<value>', methods=['GET'])
def buscar_usuario(criterio, value):
    url = "http://localhost:8080/api/usuario/search/"
    print(f"Buscando usuarios con criterio {criterio} y value {value}") 

    lista = []
    print(lista)
    try:
        if criterio in ['apellido', 'nombre', 'correo']:
            r = requests.get(url + criterio + '/' + value)
        else:
            flash("Criterio de búsqueda no válido.", category='error')
            return redirect(url_for('usuario.home_usuario'))

        if r.status_code == 200:
            data1 = r.json()
            if "data" in data1:
                lista = data1["data"]  # Procesar lista directamente
                return render_template('usuario/lista.html', lista=lista)
            else:
                flash("La respuesta del servidor está vacía.", category='error')
        else:
            flash(f"Error al obtener datos: {r.status_code}", category='error')

    except requests.exceptions.RequestException as e:
        flash(f"Error de conexión: {str(e)}", category='error')
    except ValueError as e:
        flash("Error al procesar la respuesta del servidor.", category='error')

    return redirect(url_for('usuario.home_usuario'))
#######################################################################################
@usuario.route('/usuario/grafo')
def ver_grafo():
    try:
        headers = {'Authorization': 'public'}
        r = requests.get(f'{URL}/grafo', headers=headers)
        r.raise_for_status()
        data = r.json()
        
        # Extraemos los nodos
        usuarios = data.get("data", [])
        nodos = []
        for usuario in usuarios:
            nodos.append({
                "id": usuario['id'],
                "label": usuario['nombre'],
                "title": f"{usuario['nombre']} {usuario['apellido']}<br>Correo: {usuario['correo']}",
            })
        
        # Extraemos las adyacencias (aristas)
        grafo_adyacencias = data.get("grafo", {}).get("listAdyacencias", [])
        aristas = []
        print("Aristas: ", aristas)
        
        id_nodos = [usuario['id'] for usuario in usuarios]
        id_nodos_set = set(id_nodos)  
        
        for i, nodo in zip(id_nodos, grafo_adyacencias[1:]): 
            if not nodo or 'head' not in nodo:
                continue  
            
            current = nodo['head']
            while current:
                if current['data']['destination'] in id_nodos_set and current['data']['destination'] != i:
                    aristas.append({
                        "from": i,
                        "to": current['data']['destination'],
                        "label": str(current['data']['weight']),
                        "arrows": 'to',
                    })
                current = current.get('next')  

        print("Aristas extraídas:", aristas)  
        
        return render_template('usuario/grafo.html', nodos=nodos, aristas=aristas)

    except requests.RequestException as e:
        flash("Error al cargar el grafo.", "error")
        return redirect(url_for('usuario.home_usuario'))
#######################################################################################
@usuario.route('/usuario/calcular_camino_corto', methods=['GET'])
def calcular_camino_corto():
    try:
        origen = request.args.get('origen')
        destino = request.args.get('destino')
        algoritmo = request.args.get('algoritmo')

        if not origen or not destino or not algoritmo:
            flash("Debe ingresar un origen, un destino y seleccionar un algoritmo.", "error")
            return redirect(url_for('usuario.ver_grafo'))

        # Hacer la solicitud al backend
        headers = {'Authorization': 'public'}
        r = requests.get(f'{URL}/camino_corto/{origen}/{destino}/{algoritmo}', headers=headers)
        r.raise_for_status()
        data = r.json()

        if data.get("status") == "success":
            # Solo pasamos el resultado y el algoritmo
            return render_template('usuario/grafo.html', resultado=data.get("resultado"), algoritmo=int(algoritmo))
        else:
            flash(f"Error: {data.get('message', 'Error desconocido')}", "error")
            return redirect(url_for('usuario.ver_grafo'))

    except requests.RequestException as e:
        flash("Error al calcular el camino corto.", "error")
        return redirect(url_for('usuario.ver_grafo')) 
    