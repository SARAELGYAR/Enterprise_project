# Get Authentication Tokens for Postman
$baseUrl = "http://localhost:8080"

Write-Host "=== GETTING TOKENS FOR POSTMAN ===" -ForegroundColor Green

# Get Tenant A Token
Write-Host "`n👉 Tenant A (Admin) Login:" -ForegroundColor Yellow
$tenantALogin = @{email = "admin@tenant1.com"; password = "password123"} | ConvertTo-Json
$tenantAResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body $tenantALogin
$tenantAToken = $tenantAResponse.token

Write-Host "TENANT_A_TOKEN:" -ForegroundColor Cyan
Write-Host $tenantAToken -ForegroundColor White

# Get Tenant B Token  
Write-Host "`n👉 Tenant B (Admin) Login:" -ForegroundColor Yellow
$tenantBLogin = @{email = "admin@tenant2.com"; password = "password123"} | ConvertTo-Json
$tenantBResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body $tenantBLogin
$tenantBToken = $tenantBResponse.token

Write-Host "TENANT_B_TOKEN:" -ForegroundColor Cyan
Write-Host $tenantBToken -ForegroundColor White

# Get User Token
Write-Host "`n👉 Regular User Login:" -ForegroundColor Yellow
$userLogin = @{email = "user@tenant1.com"; password = "password123"} | ConvertTo-Json
$userResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body $userLogin
$userToken = $userResponse.token

Write-Host "USER_TOKEN:" -ForegroundColor Cyan
Write-Host $userToken -ForegroundColor White

Write-Host "`n✅ TOKENS READY - Copy these into Postman requests!" -ForegroundColor Green
Write-Host "Use POSTMAN-REQUESTS.md file for exact requests to copy-paste." -ForegroundColor Yellow
