[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

$loginResponse = Invoke-RestMethod -Uri "http://localhost:9081/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"mario@test.com","password":"123456"}'
$token = $loginResponse.token

Write-Host "Token obtido com sucesso!" -ForegroundColor Cyan
Write-Host "Iniciando 20 requisicoes." -ForegroundColor Cyan
Write-Host ""

for ($i = 1; $i -le 20; $i++) {
    Write-Host "Request #$i" -NoNewline
    try {
        $body = @{
            customerId = "Cliente $i"
            totalAmount = ($i * 10)
        } | ConvertTo-Json

        $response = Invoke-RestMethod -Uri "http://localhost:9081/orders" -Method POST -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $body
        Write-Host " -> OK (id: $($response.id))" -ForegroundColor Green
    } catch {
        $statusCode = "N/A"
        if ($_.Exception.Response) {
            $statusCode = $_.Exception.Response.StatusCode.value__
        }
        Write-Host " -> ERRO [$statusCode]: $($_.Exception.Message)" -ForegroundColor Red
    }
    Start-Sleep -Milliseconds 200
}

Write-Host ""
Write-Host "Teste finalizado!" -ForegroundColor Cyan