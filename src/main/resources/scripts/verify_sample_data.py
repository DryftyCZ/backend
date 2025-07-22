#!/usr/bin/env python3
"""
Verification script for sample analytics data
Connects to the H2 database and checks if sample data was properly inserted
"""

import requests
import json
from datetime import datetime

BASE_URL = "http://localhost:8080/api"
LOGIN_URL = f"{BASE_URL}/auth/signin"
DASHBOARD_URL = f"{BASE_URL}/dashboard/stats"
ANALYTICS_URL = f"{BASE_URL}/analytics"

ADMIN_USER = "admin"
ADMIN_PASS = "OstravaPoruba12*" 

def get_jwt_token():
    """Get JWT token for admin user"""
    try:
        data = {"identifier": ADMIN_USER, "password": ADMIN_PASS}
        response = requests.post(LOGIN_URL, json=data)
        response.raise_for_status()
        token = response.json().get("token")
        if not token:
            raise RuntimeError("Login failed - no token in response")
        return token
    except Exception as e:
        print(f"❌ Login failed: {e}")
        return None

def check_dashboard_stats(token):
    """Check dashboard statistics"""
    try:
        headers = {"Authorization": f"Bearer {token}"}
        response = requests.get(DASHBOARD_URL, headers=headers)
        response.raise_for_status()
        stats = response.json()
        
        print("📊 Dashboard Statistics:")
        print(f"   Total Visitors: {stats.get('totalVisitors', 'N/A')}")
        print(f"   Total Countries: {stats.get('totalCountries', 'N/A')}")
        print(f"   Total Revenue: €{stats.get('totalRevenue', 'N/A')}")
        print(f"   Total Tickets Sold: {stats.get('totalTicketsSold', 'N/A')}")
        print(f"   Conversion Rate: {stats.get('conversionRate', 'N/A')}%")
        print(f"   Active Visitors: {stats.get('activeVisitors', 'N/A')}")
        
        # Check if we have meaningful data (non-zero values)
        visitors = stats.get('totalVisitors', 0)
        revenue = stats.get('totalRevenue', 0)
        tickets = stats.get('totalTicketsSold', 0)
        
        if visitors > 50 and revenue > 10000 and tickets > 20:
            print("✅ Dashboard shows realistic sample data!")
            return True
        else:
            print("⚠️  Dashboard data seems low - sample data may not be loaded")
            return False
            
    except Exception as e:
        print(f"❌ Failed to fetch dashboard stats: {e}")
        return False

def check_analytics_data(token):
    """Check analytics endpoints"""
    try:
        headers = {"Authorization": f"Bearer {token}"}
        
        # Check visitor sessions
        response = requests.get(f"{ANALYTICS_URL}/visitor-sessions", headers=headers)
        if response.status_code == 200:
            sessions = response.json()
            print(f"📈 Visitor Sessions: {len(sessions)} records found")
        
        # Check geographic data  
        response = requests.get(f"{ANALYTICS_URL}/geographic", headers=headers)
        if response.status_code == 200:
            geo_data = response.json()
            print(f"🌍 Geographic Data: {len(geo_data)} countries found")
            
        # Check traffic sources
        response = requests.get(f"{ANALYTICS_URL}/traffic-sources", headers=headers)
        if response.status_code == 200:
            traffic_data = response.json()
            print(f"🚦 Traffic Sources: {len(traffic_data)} sources found")
            
        return True
        
    except Exception as e:
        print(f"❌ Failed to fetch analytics data: {e}")
        return False

def check_events_and_tickets(token):
    """Check events and tickets data"""
    try:
        headers = {"Authorization": f"Bearer {token}"}
        
        # Check events
        response = requests.get(f"{BASE_URL}/events", headers=headers)
        response.raise_for_status()
        events = response.json()
        print(f"🎪 Events: {len(events)} events found")
        
        # Check tickets for first event
        if events:
            event_id = events[0]['id']
            response = requests.get(f"{BASE_URL}/tickets/event/{event_id}", headers=headers)
            if response.status_code == 200:
                tickets = response.json()
                print(f"🎟️  Tickets for Event {event_id}: {len(tickets)} tickets found")
        
        return True
        
    except Exception as e:
        print(f"❌ Failed to fetch events/tickets: {e}")
        return False

def check_database_via_h2():
    """Instructions for checking database directly"""
    print("\n🔍 Database Direct Access:")
    print("   1. Open browser to: http://localhost:8080/h2-console")
    print("   2. Use these connection details:")
    print("      JDBC URL: jdbc:h2:file:./data/ticketing-db")
    print("      User Name: sa")
    print("      Password: password")
    print("   3. Run these queries to verify data:")
    print("      SELECT COUNT(*) FROM visitor_sessions;")
    print("      SELECT COUNT(*) FROM real_time_stats;") 
    print("      SELECT COUNT(*) FROM tickets WHERE used = true;")
    print("      SELECT COUNT(DISTINCT country) FROM visitor_sessions;")
    print("      SELECT SUM(revenue_generated) FROM visitor_sessions;")

def main():
    print("🔧 Verifying Sample Data for Ticketing Analytics Dashboard")
    print("=" * 60)
    
    # Check if backend is running
    try:
        response = requests.get(f"{BASE_URL}/test/hello", timeout=5)
        if response.status_code != 200:
            raise Exception("Backend not responding properly")
    except:
        print("❌ Backend not running or not accessible at http://localhost:8080")
        print("   Please start the backend first: cd backend && ./mvnw spring-boot:run")
        return
    
    print("✅ Backend is running")
    
    # Get authentication token
    token = get_jwt_token()
    if not token:
        return
    
    print("✅ Authentication successful")
    print()
    
    # Check various data endpoints
    dashboard_ok = check_dashboard_stats(token)
    print()
    
    analytics_ok = check_analytics_data(token)
    print()
    
    events_ok = check_events_and_tickets(token)
    print()
    
    # Provide H2 console instructions
    check_database_via_h2()
    
    # Summary
    print("\n" + "=" * 60)
    if dashboard_ok and analytics_ok and events_ok:
        print("✅ Sample data verification PASSED")
        print("   Your analytics dashboard should show realistic data!")
    else:
        print("⚠️  Sample data verification had issues")
        print("   Check the migration files and restart the backend")
        print("   Migration file: V4__Add_Sample_Analytics_Data.sql")
    
    print("\n🚀 Next steps:")
    print("   1. Open frontend: http://localhost:3000")
    print("   2. Login with admin/OstravaPoruba12*")
    print("   3. View Dashboard to see analytics")
    print("   4. Check Analytics section for detailed reports")

if __name__ == "__main__":
    main()