import requests
import random
import string
from datetime import datetime, timedelta

BASE_URL = "http://localhost:8080/api"
LOGIN_URL = f"{BASE_URL}/auth/signin"
TICKETS_URL = f"{BASE_URL}/tickets"
EVENTS_URL = f"{BASE_URL}/events"

ADMIN_USER = "admin"
ADMIN_PASS = "OstravaPoruba12*" 

def random_name():
    return ''.join(random.choices(string.ascii_letters, k=8))

def random_email():
    return f"{random_name()}@test.com"

def get_jwt_token(identifier, password):
    data = {
        "identifier": identifier,
        "password": password
    }
    r = requests.post(LOGIN_URL, json=data)
    print("Login response status:", r.status_code)
    print("Login response body:", r.text)
    r.raise_for_status()
    token = r.json().get("token")
    if not token:
        raise RuntimeError("Login se nepovedl, token nenalezen v odpovědi")
    return token

def create_event(token):
    headers = {
        "Authorization": f"Bearer {token}"
    }
    event_date = (datetime.now() + timedelta(days=1)).date()  # LocalDate (bez času)
    data = {
        "name": f"Test Event {random_name()}",
        "location": "Ostrava, CZ",
        "date": event_date.isoformat()
    }
    r = requests.post(EVENTS_URL, json=data, headers=headers)
    if r.status_code == 201:
        event = r.json()
        print(f"✅ Event vytvořen: {event['id']} - {event['name']}")
        return event["id"]
    else:
        raise RuntimeError(f"❌ Chyba při vytváření eventu: {r.status_code} - {r.text}")

def generate_tickets(event_id, count=100, token=None):
    data = {
        "eventId": event_id,
        "count": count
    }
    headers = {
        "Authorization": f"Bearer {token}"
    }
    r = requests.post(f"{TICKETS_URL}/generate", json=data, headers=headers)
    if r.status_code == 200:
        print(f"✅ Vygenerováno {count} ticketů pro event {event_id}")
    else:
        print(f"❌ Chyba při generování ticketů: {r.status_code} - {r.text}")

def purchase_tickets(event_id, count=100, token=None):
    headers = {}
    if token:
        headers["Authorization"] = f"Bearer {token}"

    for i in range(count):
        data = {
            "eventId": event_id,
            "customerName": random_name(),
            "customerEmail": random_email()
        }
        r = requests.post(f"{TICKETS_URL}/purchase", json=data, headers=headers)
        if r.status_code == 200:
            print(f"✅ Ticket koupen {i+1}")
        else:
            print(f"❌ Chyba při koupi ticketu {i+1}: {r.text}")

def check_stats(token=None):
    headers = {
        "Authorization": f"Bearer {token}"
    }
    r_countries = requests.get(f"{TICKETS_URL}/stats/countries", headers=headers)
    r_cities = requests.get(f"{TICKETS_URL}/stats/cities", headers=headers)

    print("\n🌍 Country stats:")
    print(r_countries.json())

    print("\n🏙️ City stats:")
    print(r_cities.json())

if __name__ == "__main__":
    print("🔑 Přihlašuji se a získávám token...")
    token = get_jwt_token(ADMIN_USER, ADMIN_PASS)
    print("✅ Token získán!")

    print("\n📅 Vytvářím nový event...")
    event_id = create_event(token)

    print("\n🎟️ Generuji 100 ticketů...")
    generate_tickets(event_id, 100, token=token)

    print("\n💳 Nakupuji 100 ticketů...")
    purchase_tickets(event_id, 100, token=None)  # nebo token=token, pokud je potřeba

    print("\n📊 Kontroluji statistiky...")
    check_stats(token=token)
