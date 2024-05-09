from flask import Flask, jsonify, request
import cv2
import face_recognition
import numpy as np

app = Flask(__name__)

@app.route('/compare_faces', methods=['POST'])
def compare_faces():
    with app.app_context():
        image1 = np.frombuffer(request.files['image1'].read(), np.uint8)
        image2 = np.frombuffer(request.files['image2'].read(), np.uint8)
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

        face_location1 = face_recognition.face_locations(image1)[0]
        face_location2 = face_recognition.face_locations(image2)[0]

        face_loc1 = face_location1
        face_loc2 = face_location2

        face_image1_encodings = face_recognition.face_encodings(image1, known_face_locations=[face_loc1])[0]
        face_image2_encodings = face_recognition.face_encodings(image2, known_face_locations=[face_loc2])[0]

        print("Encodings de las caras calculados")

        result = face_recognition.compare_faces([face_image1_encodings], face_image2_encodings, 0.5)

        print("Comparación de caras realizada", result)

        return jsonify({"result": bool(result[0])})

if __name__ == '__main__':
    app.run(port=5000)