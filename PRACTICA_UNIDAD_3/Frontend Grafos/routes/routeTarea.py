from flask import Blueprint, render_template, request, redirect, url_for, flash, abort
import requests
import json

tarea = Blueprint('tarea', __name__)
URL = 'http://localhost:8080/api/tarea'
#####################################################################################
@tarea.route('/tarea')
def home_tarea():
    criterio = request.args.get('criterio') 
    tipo = request.args.get('tipo')  

    if criterio and tipo :
        return redirect(url_for('tarea.ordenar_tarea', atributo=criterio, orden=tipo))

    try:
        headers = {'Authorization': 'public'}
        r = requests.get(f'{URL}/list', headers=headers)
        r.raise_for_status()  
        data = r.json()      
        return render_template('tarea/lista.html', lista=data["data"],
                               criterio=criterio, tipo=tipo)
    except requests.RequestException as e:
        print(f"Error al obtener la lista de tareas: {e}")
        flash("No se pudo cargar la lista de tareas.", "error")
        return render_template('tarea/lista.html', lista=[], error="No se pudo cargar la lista de tareas.")
#####################################################################################
@tarea.route('/tarea/register')
def registrar():
    try:
        headers = {'Authorization': 'public'}        
        r_usuarios = requests.get('http://localhost:8080/api/usuario/list', headers=headers)
        r_usuarios.raise_for_status()
        usuarios_data = r_usuarios.json()        
        r = requests.get(f"{URL}/prioridad", headers=headers)
        r.raise_for_status()  
        prioridades_data = r.json()        
        return render_template('tarea/registro.html', 
                               lista=prioridades_data["data"], 
                               usuarios=usuarios_data["data"])
    except requests.RequestException as e:
        print(f"Error al obtener los tipos de tareas: {e}")
        flash("No se pudieron cargar los tipos de tareas.", "error")
        return redirect(url_for('tarea.home_tarea'))
#####################################################################################
@tarea.route('/tarea/editar/<int:id>', methods=['GET'])
def editar_tarea(id):
    try:
        headers = {'Authorization': 'public'}
        r_usuarios = requests.get('http://localhost:8080/api/usuario/list', headers=headers)
        r_usuarios.raise_for_status()
        
        r = requests.get(f"{URL}/prioridad")
        data = r.json()
        r1 = requests.get(f"{URL}/get/{id}")
        data1 = r1.json()

        if r1.status_code == 200:
            return render_template('tarea/editar.html', 
                                   lista=data["data"], 
                                   tarea=data1["data"],
                                   usuarios=r_usuarios.json()["data"])
        else:
            flash(data1["data"], category='error')
            return redirect(url_for('tarea.home_tarea'))
    except requests.RequestException as e:
        print(f"Error al cargar los datos: {e}")
        flash("Error al cargar los datos. Intente nuevamente.", "error")
        return redirect(url_for('tarea.home_tarea'))
#####################################################################################
@tarea.route('/tarea/save', methods=["POST"])
def guardar_tarea():
    headers = {'Content-type': 'application/json'}
    form = request.form
    try:
        dataF = {
            "nombre": form['nombre'],
            "descripcion": form['descripcion'],
            "prioridad": request.form.get('prioridad'),
            "idUsuario": form['idUsuario']
        }
        print(dataF)
        r = requests.post(f'{URL}/save', data=json.dumps(dataF), headers=headers)
        dat = r.json()
        print(dat)
        flash("Tarea registrado exitosamente", "success")
        return redirect(url_for('tarea.home_tarea'))
    except requests.RequestException as e:
        print(f"Error al registrar el tarea: {e}")
        flash("Error al registrar el tarea. Intente nuevamente.", "error")
        return redirect(url_for('tarea.registrar'))
#####################################################################################
@tarea.route('/tarea/update', methods=['POST'])
def actualizar_tarea():
    headers = {'Content-Type': 'application/json'}
    form = request.form
    
    dataF = {
            "id": form["id"],
            "nombre": form['nombre'],
            "descripcion": form['descripcion'],
            "prioridad": request.form.get('prioridad'),
            "idUsuario": form['idUsuario']
        }
    print(dataF)
    r = requests.post(f'{URL}/update', data=json.dumps(dataF), headers=headers)
    dat = r.json()
    print(dat)
    flash("Tarea actualizado exitosamente", "success")
    return redirect(url_for('tarea.home_tarea'))
#####################################################################################    
@tarea.route('/tarea/<int:id>/eliminar', methods=['GET'])
def eliminar_tarea(id):
    try:
        r = requests.delete(f'{URL}/delete/{id}')
        r.raise_for_status()
        flash("Tarea eliminado exitosamente", "success")
        return redirect(url_for('tarea.home_tarea'))
    except requests.RequestException as e:
        print(f"Error al eliminar el tarea con ID {id}: {e}")
        flash("Error al eliminar el tarea. Intente nuevamente.", "error")
        return redirect(url_for('tarea.home_tarea'))
#####################################################################################
def cargar_opciones(endpoint):
    try:
        r = requests.get(endpoint)
        r.raise_for_status()
        data = r.json()
        return [(item, item) for item in data.get("data", [])]
    except requests.RequestException as e:
        print(f"Error al cargar opciones desde {endpoint}: {e}")
        return []
#####################################################################################
@tarea.route('/tarea/order/<atributo>/<int:orden>', methods=['GET'])
def ordenar_tarea(atributo, orden):
    try:
        url = f'{URL}/order/{atributo}/{orden}'
        r = requests.get(url)
        r.raise_for_status()  
        data = r.json()

        if r.status_code == 200:
            if not data.get("data"):
                flash("No se encontraron resultados para los criterios especificados.", "warning")
            
            return render_template('tarea/lista.html', lista=data.get("data", []),
                                   criterio=atributo, tipo=orden)
        else:
            flash(f"Error al ordenar los tareas: {data.get('msg', 'Error desconocido')}", "error")
            return render_template('tarea/lista.html', lista=[])

    except requests.RequestException as e:
        print(f"Error al ordenar tareas: {e}")
        flash("Error al ordenar los tareas. Intente nuevamente.", "error")
        return render_template('/tarea/lista.html', lista=[])
#####################################################################################
@tarea.route('/tarea/search/<criterio>/<texto>', methods=['GET'])
def buscar_generador(criterio, texto):
    url = "http://localhost:8080/api/tarea/search/"
    lista = []
    
    try:
        if criterio in ['nombre','descripcion', 'prioridad']:
            r = requests.get(url + criterio + '/' + texto)
        else:
            flash("Criterio de búsqueda no valido.", category='error')
            return redirect(url_for('tarea.home_tarea'))

        if r.status_code == 200:
            if r.text.strip():  
                data1 = r.json()
                # Convertir lista enlazada en una lista normal
                if "data" in data1 and "head" in data1["data"]:
                    current = data1["data"]["head"]
                    while current:
                        lista.append(current["data"])
                        current = current.get("next", None)
                return render_template('tarea/lista.html', lista=lista)
            else:
                flash("La respuesta del servidor esta vacia.", category='error')
        else:
            flash(f"Error al obtener datos: {r.status_code}", category='error')
    
    except requests.exceptions.RequestException as e:
        flash(f"Error de conexion: {str(e)}", category='error')
    except ValueError as e:
        flash("Error al procesar la respuesta del servidor.", category='error')

    return redirect(url_for('tarea.home_tarea'))
#####################################################################################
@tarea.route('/usuario/detail/<int:id>')
def usuario_detail(id):
    url = f"http://localhost:8080/api/usuario/get/{id}"  
    headers = {'Authorization': 'public'}  

    try:
        response = requests.get(url, headers=headers)
        if response.status_code == 200:
            usuario_data = response.json().get("data")
            return render_template('tarea/usuarioDetail.html', usuario=usuario_data)
        else:
            error_msg = response.json().get("error", "Error inesperado")
            return render_template('usuarioDetail.html', error=error_msg)
    except Exception as e:
        return render_template('tarea/usuarioDetail.html', error=str(e))
#####################################################################################