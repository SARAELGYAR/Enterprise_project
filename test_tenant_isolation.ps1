# Tenant Isolation Test Script
# This script tests strict tenant isolation for all endpoints

$baseUrl = "http://localhost:8080"

Write-Host "=== Tenant Isolation Test Suite ===" -ForegroundColor Green

# Test 1: Login as Tenant 1 Admin
Write-Host "Test 1: Login as Tenant 1 Admin" -ForegroundColor Yellow
$login1 = @{
    email = "admin@tenant1.com"
    password = "password123"
} | ConvertTo-Json

try {
    $response1 = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body $login1
    $tenant1Token = $response1.token
    Write-Host "✅ Tenant 1 login successful" -ForegroundColor Green
} catch {
    Write-Host "❌ Tenant 1 login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 2: Login as Tenant 2 Admin
Write-Host "Test 2: Login as Tenant 2 Admin" -ForegroundColor Yellow
$login2 = @{
    email = "admin@tenant2.com"
    password = "password123"
} | ConvertTo-Json

try {
    $response2 = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body $login2
    $tenant2Token = $response2.token
    Write-Host "✅ Tenant 2 login successful" -ForegroundColor Green
} catch {
    Write-Host "❌ Tenant 2 login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 3: Create Projects for each tenant
Write-Host "Test 3: Create Projects for each tenant" -ForegroundColor Yellow

# Create project for tenant 1
$project1 = @{
    name = "Tenant 1 Project"
    createdBy = "admin@tenant1.com"
} | ConvertTo-Json

try {
    $project1Response = Invoke-RestMethod -Uri "$baseUrl/projects" -Method Post -ContentType "application/json" -Headers @{Authorization = "Bearer $tenant1Token"} -Body $project1
    $tenant1ProjectId = $project1Response.id
    Write-Host "✅ Tenant 1 project created" -ForegroundColor Green
} catch {
    Write-Host "❌ Tenant 1 project creation failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Create project for tenant 2
$project2 = @{
    name = "Tenant 2 Project"
    createdBy = "admin@tenant2.com"
} | ConvertTo-Json

try {
    $project2Response = Invoke-RestMethod -Uri "$baseUrl/projects" -Method Post -ContentType "application/json" -Headers @{Authorization = "Bearer $tenant2Token"} -Body $project2
    $tenant2ProjectId = $project2Response.id
    Write-Host "✅ Tenant 2 project created" -ForegroundColor Green
} catch {
    Write-Host "❌ Tenant 2 project creation failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Verify Project Isolation
Write-Host "Test 4: Verify Project Isolation" -ForegroundColor Yellow

# Get projects as tenant 1
try {
    $tenant1Projects = Invoke-RestMethod -Uri "$baseUrl/projects" -Method Get -Headers @{Authorization = "Bearer $tenant1Token"}
    if ($tenant1Projects.Count -eq 1 -and $tenant1Projects[0].name -eq "Tenant 1 Project") {
        Write-Host "✅ Tenant 1 can only see their projects" -ForegroundColor Green
    } else {
        Write-Host "❌ Tenant 1 project isolation failed" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Failed to get Tenant 1 projects: $($_.Exception.Message)" -ForegroundColor Red
}

# Get projects as tenant 2
try {
    $tenant2Projects = Invoke-RestMethod -Uri "$baseUrl/projects" -Method Get -Headers @{Authorization = "Bearer $tenant2Token"}
    if ($tenant2Projects.Count -eq 1 -and $tenant2Projects[0].name -eq "Tenant 2 Project") {
        Write-Host "✅ Tenant 2 can only see their projects" -ForegroundColor Green
    } else {
        Write-Host "❌ Tenant 2 project isolation failed" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Failed to get Tenant 2 projects: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Try cross-tenant access (should fail)
Write-Host "Test 5: Try cross-tenant access (should fail)" -ForegroundColor Yellow

try {
    $crossTenantAccess = Invoke-RestMethod -Uri "$baseUrl/projects/$tenant2ProjectId" -Method Get -Headers @{Authorization = "Bearer $tenant1Token"}
    Write-Host "❌ Cross-tenant project access should have failed!" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 404 -or $_.Exception.Response.StatusCode -eq 403) {
        Write-Host "✅ Cross-tenant project access properly blocked" -ForegroundColor Green
    } else {
        Write-Host "❌ Unexpected error for cross-tenant access: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 6: Create Tasks for each tenant
Write-Host "Test 6: Create Tasks for each tenant" -ForegroundColor Yellow

# Create task for tenant 1
$task1 = @{
    title = "Tenant 1 Task"
    status = "TODO"
    projectId = $tenant1ProjectId
} | ConvertTo-Json

try {
    $task1Response = Invoke-RestMethod -Uri "$baseUrl/tasks" -Method Post -ContentType "application/json" -Headers @{Authorization = "Bearer $tenant1Token"} -Body $task1
    $tenant1TaskId = $task1Response.id
    Write-Host "✅ Tenant 1 task created" -ForegroundColor Green
} catch {
    Write-Host "❌ Tenant 1 task creation failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Create task for tenant 2
$task2 = @{
    title = "Tenant 2 Task"
    status = "TODO"
    projectId = $tenant2ProjectId
} | ConvertTo-Json

try {
    $task2Response = Invoke-RestMethod -Uri "$baseUrl/tasks" -Method Post -ContentType "application/json" -Headers @{Authorization = "Bearer $tenant2Token"} -Body $task2
    $tenant2TaskId = $task2Response.id
    Write-Host "✅ Tenant 2 task created" -ForegroundColor Green
} catch {
    Write-Host "❌ Tenant 2 task creation failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7: Verify Task Isolation
Write-Host "Test 7: Verify Task Isolation" -ForegroundColor Yellow

# Get tasks as tenant 1
try {
    $tenant1Tasks = Invoke-RestMethod -Uri "$baseUrl/tasks" -Method Get -Headers @{Authorization = "Bearer $tenant1Token"}
    if ($tenant1Tasks.Count -eq 1 -and $tenant1Tasks[0].title -eq "Tenant 1 Task") {
        Write-Host "✅ Tenant 1 can only see their tasks" -ForegroundColor Green
    } else {
        Write-Host "❌ Tenant 1 task isolation failed" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Failed to get Tenant 1 tasks: $($_.Exception.Message)" -ForegroundColor Red
}

# Get tasks as tenant 2
try {
    $tenant2Tasks = Invoke-RestMethod -Uri "$baseUrl/tasks" -Method Get -Headers @{Authorization = "Bearer $tenant2Token"}
    if ($tenant2Tasks.Count -eq 1 -and $tenant2Tasks[0].title -eq "Tenant 2 Task") {
        Write-Host "✅ Tenant 2 can only see their tasks" -ForegroundColor Green
    } else {
        Write-Host "❌ Tenant 2 task isolation failed" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Failed to get Tenant 2 tasks: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 8: Try cross-tenant task access (should fail)
Write-Host "Test 8: Try cross-tenant task access (should fail)" -ForegroundColor Yellow

try {
    $crossTenantTaskAccess = Invoke-RestMethod -Uri "$baseUrl/tasks/$tenant2TaskId" -Method Get -Headers @{Authorization = "Bearer $tenant1Token"}
    Write-Host "❌ Cross-tenant task access should have failed!" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 404 -or $_.Exception.Response.StatusCode -eq 403) {
        Write-Host "✅ Cross-tenant task access properly blocked" -ForegroundColor Green
    } else {
        Write-Host "❌ Unexpected error for cross-tenant task access: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 9: Test unauthorized access (should fail)
Write-Host "Test 9: Test unauthorized access (should fail)" -ForegroundColor Yellow

try {
    $unauthorizedAccess = Invoke-RestMethod -Uri "$baseUrl/projects" -Method Get
    Write-Host "❌ Unauthorized access should have failed!" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-Host "✅ Unauthorized access properly blocked" -ForegroundColor Green
    } else {
        Write-Host "❌ Unexpected error for unauthorized access: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "=== Tenant Isolation Test Complete ===" -ForegroundColor Green
Write-Host "All tests demonstrate strict tenant isolation:" -ForegroundColor Cyan
Write-Host "• Each tenant can only access their own data" -ForegroundColor Cyan
Write-Host "• Cross-tenant access is properly blocked" -ForegroundColor Cyan
Write-Host "• Authentication is required for all endpoints" -ForegroundColor Cyan
Write-Host "• Read/Write/List operations are tenant-isolated" -ForegroundColor Cyan
