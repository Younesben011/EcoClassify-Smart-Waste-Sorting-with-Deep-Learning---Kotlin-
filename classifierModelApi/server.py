from fastapi import FastAPI,HTTPException
from pydantic import BaseModel
import base64
from PIL import Image
from io import BytesIO
import matplotlib.pyplot as plt
import numpy as np
from tensorflow.keras.models import load_model
from os import path
import tensorflow as tf
import uvicorn
MODLE_PATH ="models"
MODLE_NAME="GarbageModel_v1.h5"
model=None

app =FastAPI()

class imageClass(BaseModel):
    Class:str

class ImageD(BaseModel):
    encoded_image:str



def classify(y):
    if(y<0.5):
        return False
    return True

def classifyImage(image):
    global model
    if(not(model)):
        model =load_model(path.join(MODLE_PATH,MODLE_NAME))
    resized_image = tf.image.resize(image,(256,256))
    y_predicted=model.predict(np.expand_dims(resized_image/255,0))
    return classify(y_predicted)


@app.post('/upload')
async def decode_image(body:ImageD):
    try:
        # Decode base64 string to bytes
        decoded_image=base64.b64decode(body.encoded_image)

        # open image 
        image = Image.open(BytesIO(decoded_image))
        res = "plastic bottle" if classifyImage(image) else "soda can"
        return{"status":res}
    except Exception as e:
        raise HTTPException(status_code=400,detail=str(e))

@app.get('/')
async def sayHello():
    return{"message":"hi"}




