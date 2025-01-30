from flask import Blueprint, render_template, request, redirect, url_for, flash
import requests
import json

proyecto = Blueprint('proyecto', __name__)
URL = 'http://localhost:8080/api/proyecto'

#####################################################################################
@proyecto.route('/')
def home():
    criterio = request.args.get('criterio') 
    tipo = request.args.get('tipo')  

    if criterio and tipo :
        return redirect(url_for('proyecto.ordenar_proyecto', atributo=criterio, orden=tipo))

    try:
        headers = {'Authorization': 'public'}
        r = requests.get(f'{URL}/list', headers=headers)
        r.raise_for_status()
        data = r.json()
        return render_template('proyecto/lista.html', lista=data["data"],
                               criterio=criterio, tipo=tipo)
    except requests.RequestException as e:
        print(f"Error al obtener la lista de proyectos: {e}")
        flash("No se pudo cargar la lista de proyectos.", "error")
        return render_template('proyecto/lista.html', lista=[], error="No se pudo cargar la lista de proyectos.")
#####################################################################################
@proyecto.route('/proyecto/editar/<int:id>', methods=['GET'])
def editar_proyecto(id):
    try:
        r = requests.get(f"{URL}/get/{id}")
        r.raise_for_status()
        data = r.json()
        print(data)
        return render_template('proyecto/editar.html', proyecto=data["data"])
    except requests.RequestException as e:
        print(f"Error al obtener la proyecto con ID {id}: {e}")
        flash("Error al cargar los datos de la proyecto.", "error")
        return redirect(url_for('proyecto.home'))
#####################################################################################
@proyecto.route('/proyecto/register', methods=['GET'])
def registrar():
    return render_template('proyecto/registro.html')
#####################################################################################
@proyecto.route('/proyecto/save', methods=["POST"])
def guardar_asociacion():
    headers = {'Content-Type': 'application/json'}
    form = request.form
    try:
        dataF = {
            "nombre": form['nombre'],  
            "descripcion": form['descripcion'],  
        }
        
        r = requests.post(f'{URL}/save', data=json.dumps(dataF), headers=headers)
        r.raise_for_status()  
        flash("Asociacion registrada exitosamente", "success")
        return redirect(url_for('proyecto.home'))
    except requests.RequestException as e:
        if hasattr(e.response, 'text'):
            print("Detalle del error del API:", e.response.text)
        print("Error al registrar la proyecto:", e)
        flash("Error al registrar la proyecto. Intente nuevamente.", "error")
        return redirect(url_for('proyecto.registrar'))
#####################################################################################    
@proyecto.route('/proyecto/update', methods=['POST'])
def actualizar():
    headers = {'Content-Type': 'application/json'}
    form = request.form
    try:
        dataF = {
            "id": int(form["id"]),
            "nombre": form['nombre'],
            "descripcion": form['descripcion'],
        }

        r = requests.post(f'{URL}/update', data=json.dumps(dataF), headers=headers)
        r.raise_for_status()  
        dat = r.json()
        print(dat)
        flash("Proyecto actualizado exitosamente", "success")
        return redirect(url_for('proyecto.home'))
    except requests.RequestException as e:
        print(f"Error al eliminar la proyecto con ID {id}: {e}")
        flash("Error al eliminar la proyecto. Intente nuevamente.", "error")
        return redirect(url_for('proyecto.home'))

    except requests.RequestException as e:
        print("Error al actualizar la proyecto:", e)
        flash("Error al actualizar la proyecto. Intente nuevamente.", "error")
        return redirect(url_for('proyecto.home'))

#####################################################################################    
@proyecto.route('/proyecto/<int:id>/eliminar', methods=['GET'])
def eliminar_asociacion(id):
    try:
        r = requests.delete(f'{URL}/delete/{id}')
        r.raise_for_status()
        flash("Protecto eliminada exitosamente", "success")
        return redirect(url_for('proyecto.home'))
    except requests.RequestException as e:
        flash("Error al eliminar la proyecto. Intente nuevamente.", "error")
        return redirect(url_for('proyecto.home'))
#####################################################################################
@proyecto.route('/proyecto/order/<atributo>/<int:orden>', methods=['GET'])
def ordenar_proyecto(atributo, orden):
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
            return render_template('/proyecto/lista.html', lista=data.get("data", []),
                                   criterio=atributo, tipo=orden)
        else:
            flash(f"Error al ordenar las familias: {data.get('msg', 'Error desconocido')}", "error")
            return render_template('/proyecto/lista.html', lista=[])

    except requests.RequestException as e:
        print(f"Error al ordenar familias: {e}")
        flash("Error al ordenar las familias. Intente nuevamente.", "error")
        return render_template('/proyecto/lista.html', lista=[])
#######################################################################################3
@proyecto.route('/proyecto/search/<criterio>/<value>', methods=['GET'])
def buscar_usuario(criterio, value):
    url = "http://localhost:8080/api/proyecto/search/"
    print(f"Buscando usuarios con criterio {criterio} y value {value}") 

    lista = []
    print(lista)
    try:
        if criterio in ['apellido', 'nombre']:
            r = requests.get(url + criterio + '/' + value)
        else:
            flash("Criterio de búsqueda no válido.", category='error')
            return redirect(url_for('proyecto.home'))

        if r.status_code == 200:
            data1 = r.json()
            if "data" in data1:
                lista = data1["data"]  # Procesar lista directamente
                return render_template('proyecto/lista.html', lista=lista)
            else:
                flash("La respuesta del servidor está vacía.", category='error')
        else:
            flash(f"Error al obtener datos: {r.status_code}", category='error')

    except requests.exceptions.RequestException as e:
        flash(f"Error de conexión: {str(e)}", category='error')
    except ValueError as e:
        flash("Error al procesar la respuesta del servidor.", category='error')

    return redirect(url_for('proyecto.home'))
