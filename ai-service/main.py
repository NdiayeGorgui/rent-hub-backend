from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from dotenv import load_dotenv
from openai import OpenAI
import openai
import os
import json

load_dotenv()

app = FastAPI(title="Gonifty AI Service", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

CATEGORIES = {
    1: "Électronique",
    2: "Électroménager",
    3: "Événements",
    4: "Véhicules",
    5: "Bébé & Enfants",
    6: "Sport & Loisirs",
    7: "Maison & Meubles",
    8: "Mode & Vêtements",
    9: "Outils & Bricolage",
    10: "Autres",
    11: "Matériel de construction",
}


class GenerateDescriptionRequest(BaseModel):
    title: str
    category_id: int
    item_type: str
    price_per_day: float | None = None
    city: str | None = None


class GenerateDescriptionResponse(BaseModel):
    description: str


class SuggestPriceRequest(BaseModel):
    title: str
    category_id: int
    item_type: str


class SuggestPriceResponse(BaseModel):
    min_price: float
    max_price: float
    recommended_price: float
    reasoning: str


@app.get("/health")
def health():
    return {"status": "ok", "service": "ai-service"}


@app.post("/api/ai/generate-description", response_model=GenerateDescriptionResponse)
def generate_description(request: GenerateDescriptionRequest):
    category_name = CATEGORIES.get(request.category_id, "Autre")

    if request.item_type == "RENTAL":
        type_context = f"mise en location à {request.price_per_day}$/jour" if request.price_per_day else "mise en location"
    else:
        type_context = "mise aux enchères"

    city_context = f" à {request.city}" if request.city else ""

    prompt = f"""Tu es un assistant spécialisé dans la rédaction d'annonces de location et d'enchères entre particuliers au Québec.

Génère une description professionnelle, attrayante et honnête pour l'annonce suivante :

- Titre : {request.title}
- Catégorie : {category_name}
- Type : {type_context}
- Localisation : {city_context if city_context else "Québec"}

Règles importantes :
- Écris en français québécois naturel (pas trop formel)
- Entre 80 et 150 mots
- Mets en valeur l'utilité de l'item
- Mentionne l'état supposé (bon état, bien entretenu)
- Ajoute un appel à l'action subtil à la fin
- N'invente pas de caractéristiques techniques précises
- Ne mets pas de prix dans la description
- Ne mets pas de titre, juste la description pure

Génère UNIQUEMENT la description, sans introduction ni explication."""

    try:
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            max_tokens=300,
            messages=[
                {"role": "user", "content": prompt}
            ]
        )
        description = response.choices[0].message.content.strip()
        return GenerateDescriptionResponse(description=description)

    except openai.APIError as e:
        raise HTTPException(status_code=500, detail=f"Erreur API OpenAI: {str(e)}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erreur interne: {str(e)}")


@app.post("/api/ai/suggest-price", response_model=SuggestPriceResponse)
def suggest_price(request: SuggestPriceRequest):
    category_name = CATEGORIES.get(request.category_id, "Autre")

    prompt = f"""Tu es un expert du marché de la location entre particuliers au Québec.

Suggère un prix de location par jour pour cet item :

- Titre : {request.title}
- Catégorie : {category_name}

Réponds UNIQUEMENT avec ce format JSON exact, sans aucun texte avant ou après :
{{
  "min_price": <nombre>,
  "max_price": <nombre>,
  "recommended_price": <nombre>,
  "reasoning": "<explication courte en français de 1-2 phrases>"
}}"""

    try:
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            max_tokens=200,
            messages=[
                {"role": "user", "content": prompt}
            ]
        )
        raw = response.choices[0].message.content.strip()
        data = json.loads(raw)

        return SuggestPriceResponse(
            min_price=data["min_price"],
            max_price=data["max_price"],
            recommended_price=data["recommended_price"],
            reasoning=data["reasoning"]
        )

    except json.JSONDecodeError:
        raise HTTPException(status_code=500, detail="Réponse IA invalide")
    except openai.APIError as e:
        raise HTTPException(status_code=500, detail=f"Erreur API OpenAI: {str(e)}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erreur interne: {str(e)}")


if __name__ == "__main__":
    import uvicorn
    port = int(os.getenv("PORT", 8190))
    uvicorn.run("main:app", host="0.0.0.0", port=port, reload=True)