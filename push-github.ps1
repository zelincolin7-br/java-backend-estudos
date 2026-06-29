# Execute apos: gh auth login
# Uso: .\push-github.ps1

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

gh auth status | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "Faca login primeiro: gh auth login" -ForegroundColor Yellow
    exit 1
}

$repoName = "java-backend-estudos"

if (-not (git remote get-url origin 2>$null)) {
    gh repo create $repoName --public --source=. --remote=origin --description "Estudos Java backend - projeto order-platform e labs"
}

git push -u origin main

Write-Host ""
Write-Host "Repositorio publicado:" -ForegroundColor Green
gh repo view --web 2>$null
gh repo view --json url -q .url
