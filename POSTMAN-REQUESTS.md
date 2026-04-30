# POSTMAN REQUESTS FOR SCREENSHOTS - Phase 2 Points 1 & 2

## 🎯 POINT 1: TENANT ISOLATION PROOF

### 1️⃣ Read Isolation (GET) - SCREENSHOT 1

#### Tenant A Request:
```
METHOD: GET
URL: http://localhost:8080/projects
HEADERS:
  Authorization: Bearer [TENANT_A_TOKEN]
Body: (none)
```

#### Expected Response:
```json
Status: 200 OK
[
  {
    "id": "d0a7a58f-92b0-4a9f-b81f-2290cd46d692",
    "name": "Admin Project",
    "tenantId": "abd2dcbf-a86b-4130-8fc1-0ac2befa5f80"
  },
  {
    "id": "903421a9-c906-415d-9d8f-770b17aafd07",
    "name": "Tenant 1 Project",
    "tenantId": "abd2dcbf-a86b-4130-8fc1-0ac2befa5f80"
  }
]
```

#### Tenant B Request:
```
METHOD: GET
URL: http://localhost:8080/projects
HEADERS:
  Authorization: Bearer [TENANT_B_TOKEN]
Body: (none)
```

#### Expected Response:
```json
Status: 200 OK
[
  {
    "id": "f5c13f3f-6150-44cf-8dcd-b2d522dde646",
    "name": "Tenant 2 Project",
    "tenantId": "897f95c5-41bf-4c09-9fca-771a73428498"
  }
]
```

---

### 2️⃣ Cross Tenant Access (محاولة اختراق) - SCREENSHOT 2

#### Tenant A tries to access Tenant B's project:
```
METHOD: GET
URL: http://localhost:8080/projects/f5c13f3f-6150-44cf-8dcd-b2d522dde646
HEADERS:
  Authorization: Bearer [TENANT_A_TOKEN]
Body: (none)
```

#### Expected Response:
```json
Status: 404 Not Found
OR
Status: 500 InternalServerError
```

---

### 3️⃣ Write Isolation (POST) - SCREENSHOT 3

#### Tenant A creates new project:
```
METHOD: POST
URL: http://localhost:8080/projects
HEADERS:
  Authorization: Bearer [TENANT_A_TOKEN]
  Content-Type: application/json
Body:
{
  "name": "New Project",
  "createdBy": "admin@tenant1.com"
}
```

#### Expected Response:
```json
Status: 200 OK
{
  "id": "7cf30722-61c0-4c00-a87b-cac24cbd5a8b",
  "name": "New Project",
  "tenantId": "abd2dcbf-a86b-4130-8fc1-0ac2befa5f80"
}
```

---

### 4️⃣ List Isolation - SCREENSHOT 4

#### Tenant A List:
```
METHOD: GET
URL: http://localhost:8080/projects
HEADERS:
  Authorization: Bearer [TENANT_A_TOKEN]
```

#### Expected Response: Count = 4 projects (all with tenantId: "abd2dcbf-a86b-4130-8fc1-0ac2befa5f80")

#### Tenant B List:
```
METHOD: GET
URL: http://localhost:8080/projects
HEADERS:
  Authorization: Bearer [TENANT_B_TOKEN]
```

#### Expected Response: Count = 1 project (with tenantId: "897f95c5-41bf-4c09-9fca-771a73428498")

---

## 🎯 POINT 2: RBAC PROOF

### 1️⃣ Without Login (401) - SCREENSHOT 5

#### Request without token:
```
METHOD: GET
URL: http://localhost:8080/projects
HEADERS: (none)
Body: (none)
```

#### Expected Response:
```json
Status: 401 Unauthorized
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

---

### 2️⃣ User tries Admin operations (403) - SCREENSHOT 6

#### Regular User POST:
```
METHOD: POST
URL: http://localhost:8080/projects
HEADERS:
  Authorization: Bearer [USER_TOKEN]
  Content-Type: application/json
Body:
{
  "name": "User Project",
  "createdBy": "user@tenant1.com"
}
```

#### Expected Response:
```json
Status: 403 Forbidden
{
  "error": "Forbidden",
  "message": "Access denied"
}
```

---

### 3️⃣ Admin does same operation (Success) - SCREENSHOT 7

#### Admin POST:
```
METHOD: POST
URL: http://localhost:8080/projects
HEADERS:
  Authorization: Bearer [ADMIN_TOKEN]
  Content-Type: application/json
Body:
{
  "name": "Admin Project",
  "createdBy": "admin@tenant1.com"
}
```

#### Expected Response:
```json
Status: 200 OK
{
  "id": "1b9fae48-8ee5-4bd5-90ec-bf772cc51f42",
  "name": "Admin Project",
  "tenantId": "abd2dcbf-a86b-4130-8fc1-0ac2befa5f80"
}
```

---

### 4️⃣ User Read Only Access - SCREENSHOT 8

#### User GET (should work):
```
METHOD: GET
URL: http://localhost:8080/projects
HEADERS:
  Authorization: Bearer [USER_TOKEN]
```

#### Expected Response:
```json
Status: 200 OK
[
  {
    "id": "d0a7a58f-92b0-4a9f-b81f-2290cd46d692",
    "name": "Admin Project"
  }
]
```

---

## 🔐 HOW TO GET TOKENS

### Login as Tenant A (Admin):
```
METHOD: POST
URL: http://localhost:8080/auth/login
HEADERS:
  Content-Type: application/json
Body:
{
  "email": "admin@tenant1.com",
  "password": "password123"
}
```

### Login as Tenant B (Admin):
```
METHOD: POST
URL: http://localhost:8080/auth/login
HEADERS:
  Content-Type: application/json
Body:
{
  "email": "admin@tenant2.com",
  "password": "password123"
}
```

### Login as Regular User:
```
METHOD: POST
URL: http://localhost:8080/auth/login
HEADERS:
  Content-Type: application/json
Body:
{
  "email": "user@tenant1.com",
  "password": "password123"
}
```

---

## 📸 SCREENSHOT CHECKLIST

### Point 1 Screenshots (4):
1. ✅ Tenant A GET /projects (shows only A's data)
2. ✅ Tenant B GET /projects (shows only B's data) 
3. ✅ Cross-tenant access attempt (404/500)
4. ✅ Write isolation (system assigns tenantId)

### Point 2 Screenshots (4):
5. ✅ No token → 401 Unauthorized
6. ✅ User tries POST → 403 Forbidden
7. ✅ Admin POST → 200 Success
8. ✅ User GET → 200 Success (read only)

---

## 🎯 SCREENSHOT TIPS

For each screenshot, make sure to capture:
- ✅ Request URL and Method
- ✅ Headers (especially Authorization)
- ✅ Request Body (if any)
- ✅ Response Status Code
- ✅ Response Body
- ✅ Postman interface showing the complete request/response

This will provide complete proof with:
**Request ✔ + Response ✔ + Status Code ✔**
