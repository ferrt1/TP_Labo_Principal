from flask import Flask, jsonify, request
import cv2
import torch
import numpy as np
from PIL import Image
from facenet_pytorch import InceptionResnetV1

# Cargar el modelo pre-entrenado de FaceNet
model = InceptionResnetV1(pretrained='vggface2').eval()

# Crear un detector de rostros con OpenCV
face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

app = Flask(__name__)

@app.route('/compare_faces', methods=['POST'])
def compare_faces():
    image1 = np.frombuffer(request.files['image1'].read(), np.uint8)
    image2 = np.frombuffer(request.files['image2'].read(), np.uint8)

    print("Imágenes recibidas")

    image1 = cv2.imdecode(image1, cv2.IMREAD_COLOR)
    image2 = cv2.imdecode(image2, cv2.IMREAD_COLOR)

    print("Imágenes decodificadas")

    # Rotar las imágenes 90 grados en sentido antihorario
    image1 = cv2.rotate(image1, cv2.ROTATE_90_COUNTERCLOCKWISE)
    image2 = cv2.rotate(image2, cv2.ROTATE_90_COUNTERCLOCKWISE)

    print("Imágenes rotadas")

    scale_percent_1 = 500 / image1.shape[1]
    width_1 = int(image1.shape[1] * scale_percent_1)
    height_1 = int(image1.shape[0] * scale_percent_1)
    dim_1 = (width_1, height_1)

    scale_percent_2 = 500 / image2.shape[1]
    width_2 = int(image2.shape[1] * scale_percent_2)
    height_2 = int(image2.shape[0] * scale_percent_2)
    dim_2 = (width_2, height_2)


    resizedImage1 = cv2.resize(image1, dim_1, interpolation = cv2.INTER_AREA)
    resizedImage2 = cv2.resize(image2, dim_2, interpolation = cv2.INTER_AREA)

    print("Imágenes redimensionadas")


    # Obtener las características de los rostros en las dos imágenes
    embedding1 = get_embedding(resizedImage1)
    embedding2 = get_embedding(resizedImage2)

    # Calcular la distancia entre las características de los rostros
    distance = torch.dist(embedding1, embedding2)
    print(distance)
    # Si la distancia es menor a un cierto umbral, entonces los rostros son el mismo
    if distance < 0.36:
        print("El rostro en la imagen 2 es el mismo que en la imagen 1")
        return jsonify({"result": True})
    else:
        print("El rostro en la imagen 2 no es el mismo que en la imagen 1")
        return jsonify({"result": False})\
        

    # Función para detectar rostros y extraer características
def get_embedding(img):
        # Detectar rostros
    faces = face_cascade.detectMultiScale(img, 1.1, 4)
    # Para cada rostro detectado, extraer las características con FaceNet
    for (x, y, w, h) in faces:
        face = img[y:y+h, x:x+w]
        face = cv2.resize(face, (160, 160))
        face = Image.fromarray(face)
        face = np.array(face)  # Convertir la imagen a un array de NumPy
        face = torch.unsqueeze(torch.tensor(face), 0)
        embedding = model(face.permute(0, 3, 1, 2).float())
            
        return embedding
    
    
if __name__ == '__main__':
    app.run(port=5000)

