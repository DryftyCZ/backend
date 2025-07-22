# Enhanced Event Creation API

## Create Event with Ticket Types

### Endpoint
```
POST /api/events
```

### Request Body Example
```json
{
  "name": "Koncert XYZ",
  "description": "Nejlepší koncert roku s hvězdami světového formátu",
  "address": "Českomoravská 2345/17a",
  "city": "Praha",
  "date": "2024-12-15",
  "ticketTypes": [
    {
      "name": "Standard",
      "description": "Standardní vstupenka",
      "price": 750.00,
      "quantity": 200
    },
    {
      "name": "VIP",
      "description": "VIP vstupenka s prémiovou obsluhou",
      "price": 1500.00,
      "quantity": 50
    },
    {
      "name": "Balkon",
      "description": "Místa na balkoně s výborným výhledem",
      "price": 1200.00,
      "quantity": 75
    }
  ]
}
```

### Response
```json
{
  "id": 1,
  "name": "Koncert XYZ",
  "description": "Nejlepší koncert roku s hvězdami světového formátu",
  "address": "Českomoravská 2345/17a",
  "city": "Praha",
  "date": "2024-12-15",
  "ticketTypes": [
    {
      "id": 1,
      "name": "Standard",
      "description": "Standardní vstupenka",
      "price": 750.00,
      "quantity": 200,
      "availableQuantity": 200
    },
    {
      "id": 2,
      "name": "VIP",
      "description": "VIP vstupenka s prémiovou obsluhou",
      "price": 1500.00,
      "quantity": 50,
      "availableQuantity": 50
    },
    {
      "id": 3,
      "name": "Balkon",
      "description": "Místa na balkoně s výborným výhledem",
      "price": 1200.00,
      "quantity": 75,
      "availableQuantity": 75
    }
  ]
}
```

## Get Event Ticket Types

### Endpoint
```
GET /api/events/{eventId}/ticket-types
```

### Response
```json
[
  {
    "id": 1,
    "name": "Standard",
    "description": "Standardní vstupenka",
    "price": 750.00,
    "quantity": 200,
    "availableQuantity": 195
  },
  {
    "id": 2,
    "name": "VIP",
    "description": "VIP vstupenka s prémiovou obsluhou",
    "price": 1500.00,
    "quantity": 50,
    "availableQuantity": 48
  }
]
```

## Authentication
All endpoints require authentication with ORGANIZER or ADMIN role.

## Frontend Integration
The frontend should:
1. Provide a form for event name, description, address, and city
2. Include a date picker for event date
3. Allow dynamic addition/removal of ticket types
4. Each ticket type should have: name, description, price, quantity fields
5. Submit the complete EventCreateRequest to the backend

## Database Schema
- `events` table: id, name, description, address, city, date
- `ticket_types` table: id, name, description, price, quantity, available_quantity, event_id
- `tickets` table: id, qr_code, used, ticket_number, customer_email, customer_name, purchase_date, used_date, country, city, ip_address, event_id, ticket_type_id, customer_id