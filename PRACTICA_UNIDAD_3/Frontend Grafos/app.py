from flask import Flask

def create_app():
    app = Flask(__name__, instance_relative_config=False)
    with app.app_context():
        from routes.routeTarea import tarea  
        app.register_blueprint(tarea)
        from routes.routeUsuario import usuario
        app.register_blueprint(usuario)
        from routes.routeProyecto import proyecto  
        app.register_blueprint(proyecto)
    return app
